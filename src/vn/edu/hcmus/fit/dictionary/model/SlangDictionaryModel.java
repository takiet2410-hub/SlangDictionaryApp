package vn.edu.hcmus.fit.dictionary.model;

import vn.edu.hcmus.fit.dictionary.model.entity.*;
import vn.edu.hcmus.fit.dictionary.model.event.DictionaryModelListener;
import vn.edu.hcmus.fit.dictionary.model.index.AutocompleteTrie;
import vn.edu.hcmus.fit.dictionary.model.index.IndexCacheData;
import vn.edu.hcmus.fit.dictionary.model.index.InvertedDefinitionIndex;
import vn.edu.hcmus.fit.dictionary.model.index.SlangIndex;
import vn.edu.hcmus.fit.dictionary.model.repository.FileSlangRepository;
import vn.edu.hcmus.fit.dictionary.model.repository.SlangRepository;
import vn.edu.hcmus.fit.dictionary.util.Constants;
import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Model trung tâm quản lý toàn bộ trạng thái dữ liệu, cấu trúc Index và nghiệp vụ từ điển.
 * Tuân thủ mô hình Single Source of Truth trong RAM và Observer Pattern để thông báo cho View.
 */
public class SlangDictionaryModel {

    private final SlangRepository repository;
    private SlangIndex slangIndex;
    private InvertedDefinitionIndex definitionIndex;
    private AutocompleteTrie autocompleteTrie;
    private final List<SearchHistoryEntry> searchHistory;
    private final List<DictionaryModelListener> listeners;

    // Danh sách các từ slang ngẫu nhiên hóa để truy xuất Random O(1)
    private final List<SlangEntry> randomAccessList;

    public SlangDictionaryModel() {
        this(new FileSlangRepository());
    }

    public SlangDictionaryModel(SlangRepository repository) {
        this.repository = repository;
        this.slangIndex = new SlangIndex();
        this.definitionIndex = new InvertedDefinitionIndex();
        this.autocompleteTrie = new AutocompleteTrie();
        this.searchHistory = new ArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.randomAccessList = new ArrayList<>();
    }

    /**
     * Khởi tạo và nạp dữ liệu: Kiểm tra cache index, nếu hợp lệ deserialize trực tiếp (< 150ms),
     * nếu không hoặc hỏng sẽ đọc file và rebuild index tự động.
     */
    public synchronized void initialize() throws IOException {
        String currentSha256 = repository.getWorkingFileSha256();

        if (repository.hasValidIndexCache(currentSha256)) {
            IndexCacheData cacheData = repository.loadIndexCache();
            if (cacheData != null && cacheData.getSlangIndex() != null) {
                this.slangIndex = cacheData.getSlangIndex();
                this.definitionIndex = cacheData.getDefinitionIndex();
                this.autocompleteTrie = cacheData.getAutocompleteTrie();
                refreshRandomAccessList();
                loadHistoryFromDisk();
                notifyDictionaryLoaded(slangIndex.size());
                return;
            }
        }

        // Tự động Rebuild nếu cache không tồn tại, lỗi hoặc hash không khớp
        rebuildIndexesFromWorkingFile();
        loadHistoryFromDisk();
        notifyDictionaryLoaded(slangIndex.size());
    }

    /**
     * Đọc trực tiếp từ slang_user.txt và xây dựng lại toàn bộ các cấu trúc Index.
     */
    public synchronized void rebuildIndexesFromWorkingFile() throws IOException {
        Map<String, SlangEntry> rawMap = repository.loadUserData();

        SlangIndex newSlangIndex = new SlangIndex(rawMap.size());
        InvertedDefinitionIndex newDefIndex = new InvertedDefinitionIndex(rawMap.size() * 2);
        AutocompleteTrie newTrie = new AutocompleteTrie();

        for (SlangEntry entry : rawMap.values()) {
            newSlangIndex.put(entry);
            newDefIndex.indexEntry(entry);
            newTrie.insert(entry.getWord());
        }

        this.slangIndex = newSlangIndex;
        this.definitionIndex = newDefIndex;
        this.autocompleteTrie = newTrie;
        refreshRandomAccessList();

        // Lưu cache mới xuống đĩa
        String sha256 = repository.getWorkingFileSha256();
        repository.saveIndexCache(slangIndex, definitionIndex, autocompleteTrie, sha256);
    }

    private void refreshRandomAccessList() {
        randomAccessList.clear();
        randomAccessList.addAll(slangIndex.getAllEntries());
    }

    private void loadHistoryFromDisk() {
        searchHistory.clear();
        searchHistory.addAll(repository.loadHistory());
    }

    // ==================== TÌM KIẾM ====================

    /**
     * Tìm kiếm chính xác theo Slang Word với độ phức tạp O(1).
     * Tự động đo thời gian và ghi nhận vào Lịch sử.
     */
    public SearchResult searchBySlangWord(String word) {
        long start = System.nanoTime();
        if (word == null || word.isBlank()) {
            long elapsed = System.nanoTime() - start;
            return new SearchResult(SearchType.SLANG_WORD, word, Collections.emptyList(), elapsed);
        }

        String cleanWord = word.trim();
        SlangEntry entry = slangIndex.get(cleanWord);
        long elapsed = System.nanoTime() - start;

        List<SlangEntry> results = (entry != null) ? List.of(entry) : Collections.emptyList();
        recordHistory(SearchType.SLANG_WORD, cleanWord, results.size());

        return new SearchResult(SearchType.SLANG_WORD, cleanWord, results, elapsed);
    }

    /**
     * Tìm kiếm theo Definition chứa từ khóa (Inverted Index).
     * Tự động đo thời gian và ghi nhận vào Lịch sử.
     */
    public SearchResult searchByDefinition(String keyword) {
        long start = System.nanoTime();
        if (keyword == null || keyword.isBlank()) {
            long elapsed = System.nanoTime() - start;
            return new SearchResult(SearchType.DEFINITION, keyword, Collections.emptyList(), elapsed);
        }

        String cleanKeyword = keyword.trim();
        List<SlangEntry> matches = definitionIndex.search(cleanKeyword, slangIndex);
        long elapsed = System.nanoTime() - start;

        recordHistory(SearchType.DEFINITION, cleanKeyword, matches.size());

        return new SearchResult(SearchType.DEFINITION, cleanKeyword, matches, elapsed);
    }

    /**
     * Gợi ý danh sách Slang Word bắt đầu bằng tiền tố (Autocomplete Trie).
     * Tốc độ cực nhanh O(L) < 0.1ms.
     */
    public List<String> suggestSlangWords(String prefix, int limit) {
        int max = (limit > 0) ? limit : Constants.DEFAULT_AUTOCOMPLETE_LIMIT;
        return autocompleteTrie.suggest(prefix, max);
    }

    // ==================== QUẢN LÝ (CRUD) ====================

    public boolean containsWord(String word) {
        return slangIndex.containsKey(word);
    }

    public SlangEntry getEntry(String word) {
        return slangIndex.get(word);
    }

    /**
     * Thêm một Slang Word mới.
     * @param word Từ slang
     * @param meanings Danh sách định nghĩa
     * @param overwrite Nếu true: ghi đè toàn bộ nghĩa cũ; Nếu false: thêm nối tiếp (duplicate) vào nghĩa cũ
     */
    public synchronized void addSlangWord(String word, List<String> meanings, boolean overwrite) throws IOException {
        if (word == null || word.isBlank() || meanings == null || meanings.isEmpty()) {
            throw new IllegalArgumentException("Từ slang và định nghĩa không được để trống.");
        }

        String cleanWord = word.trim();
        SlangEntry existing = slangIndex.get(cleanWord);

        if (existing != null) {
            definitionIndex.removeEntry(existing);
            if (overwrite) {
                existing.setMeanings(meanings);
            } else {
                for (String m : meanings) {
                    existing.addMeaning(m);
                }
            }
            definitionIndex.indexEntry(existing);
            notifyEntryUpdated(existing);
        } else {
            SlangEntry newEntry = new SlangEntry(cleanWord, meanings);
            slangIndex.put(newEntry);
            definitionIndex.indexEntry(newEntry);
            autocompleteTrie.insert(cleanWord);
            randomAccessList.add(newEntry);
            notifyEntryAdded(newEntry);
        }

        persistWorkingData();
        notifyDictionaryChanged();
    }

    /**
     * Chỉnh sửa một Slang Word đã có.
     */
    public synchronized void editSlangWord(String oldWord, String newWord, List<String> newMeanings) throws IOException {
        if (oldWord == null || newWord == null || newMeanings == null || newMeanings.isEmpty()) {
            throw new IllegalArgumentException("Thông tin chỉnh sửa không hợp lệ.");
        }

        String cleanOld = oldWord.trim();
        String cleanNew = newWord.trim();

        SlangEntry existing = slangIndex.get(cleanOld);
        if (existing == null) {
            throw new NoSuchElementException("Không tìm thấy từ cần sửa: " + cleanOld);
        }

        definitionIndex.removeEntry(existing);

        if (!cleanOld.equalsIgnoreCase(cleanNew)) {
            slangIndex.remove(cleanOld);
            autocompleteTrie.delete(cleanOld);

            existing.setWord(cleanNew);
            existing.setMeanings(newMeanings);

            slangIndex.put(existing);
            autocompleteTrie.insert(cleanNew);
        } else {
            existing.setMeanings(newMeanings);
        }

        definitionIndex.indexEntry(existing);
        refreshRandomAccessList();
        persistWorkingData();
        notifyEntryUpdated(existing);
        notifyDictionaryChanged();
    }

    /**
     * Xóa một Slang Word khỏi từ điển.
     */
    public synchronized boolean deleteSlangWord(String word) throws IOException {
        if (word == null || word.isBlank()) {
            return false;
        }

        String cleanWord = word.trim();
        SlangEntry removed = slangIndex.remove(cleanWord);
        if (removed != null) {
            definitionIndex.removeEntry(removed);
            autocompleteTrie.delete(cleanWord);
            randomAccessList.remove(removed);

            persistWorkingData();
            notifyEntryDeleted(cleanWord);
            notifyDictionaryChanged();
            return true;
        }
        return false;
    }

    /**
     * Reset từ điển về dữ liệu gốc baseline (slang_base.txt).
     */
    public synchronized void resetToOriginal() throws IOException {
        repository.resetUserData();
        rebuildIndexesFromWorkingFile();
        notifyDictionaryReset();
        notifyDictionaryChanged();
    }

    /**
     * Lấy ngẫu nhiên 1 Slang Word (On this day slang word) với độ phức tạp O(1).
     */
    public SlangEntry getRandomSlang() {
        if (randomAccessList.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(randomAccessList.size());
        return randomAccessList.get(index);
    }

    public List<SlangEntry> getAllEntries() {
        return Collections.unmodifiableList(randomAccessList);
    }

    public int size() {
        return slangIndex.size();
    }

    // ==================== LỊCH SỬ TRA CỨU ====================

    private void recordHistory(SearchType type, String keyword, int count) {
        SearchHistoryEntry entry = new SearchHistoryEntry(type, keyword, count);
        searchHistory.add(0, entry); // Đưa lên đầu danh sách
        repository.appendHistory(entry);
        notifyHistoryUpdated();
    }

    public List<SearchHistoryEntry> getHistory() {
        return Collections.unmodifiableList(searchHistory);
    }

    public synchronized void clearHistory() {
        searchHistory.clear();
        repository.clearHistory();
        notifyHistoryUpdated();
    }

    // ==================== LƯU DỮ LIỆU AN TOÀN ====================

    private void persistWorkingData() throws IOException {
        repository.saveUserData(slangIndex.getAllEntries());
        String newSha256 = repository.getWorkingFileSha256();
        repository.saveIndexCache(slangIndex, definitionIndex, autocompleteTrie, newSha256);
    }

    // ==================== OBSERVER LISTENER ====================

    public void addListener(DictionaryModelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(DictionaryModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyDictionaryLoaded(int totalWords) {
        for (DictionaryModelListener listener : listeners) {
            listener.onDictionaryLoaded(totalWords);
        }
    }

    private void notifyDictionaryChanged() {
        for (DictionaryModelListener listener : listeners) {
            listener.onDictionaryChanged();
        }
    }

    private void notifyEntryAdded(SlangEntry entry) {
        for (DictionaryModelListener listener : listeners) {
            listener.onEntryAdded(entry);
        }
    }

    private void notifyEntryUpdated(SlangEntry entry) {
        for (DictionaryModelListener listener : listeners) {
            listener.onEntryUpdated(entry);
        }
    }

    private void notifyEntryDeleted(String word) {
        for (DictionaryModelListener listener : listeners) {
            listener.onEntryDeleted(word);
        }
    }

    private void notifyDictionaryReset() {
        for (DictionaryModelListener listener : listeners) {
            listener.onDictionaryReset();
        }
    }

    private void notifyHistoryUpdated() {
        for (DictionaryModelListener listener : listeners) {
            listener.onHistoryUpdated();
        }
    }
}
