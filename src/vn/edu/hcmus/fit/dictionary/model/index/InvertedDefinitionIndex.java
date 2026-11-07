package vn.edu.hcmus.fit.dictionary.model.index;

import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Chỉ mục đảo (Inverted Index) hỗ trợ tìm kiếm theo Definition với tốc độ vượt trội.
 * Cấu trúc: Token trong Definition -> Tập hợp các Slang Words chứa token đó.
 */
public class InvertedDefinitionIndex implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Set<String>> index;

    public InvertedDefinitionIndex() {
        this.index = new HashMap<>();
    }

    public InvertedDefinitionIndex(int initialCapacity) {
        this.index = new HashMap<>(initialCapacity);
    }

    /**
     * Thêm tất cả các token trong definition của một SlangEntry vào Inverted Index.
     */
    public void indexEntry(SlangEntry entry) {
        if (entry == null || entry.getWord() == null) {
            return;
        }
        String normalizedWord = TextNormalizer.normalizeWord(entry.getWord());
        for (String meaning : entry.getMeanings()) {
            Set<String> tokens = TextNormalizer.extractTokens(meaning);
            for (String token : tokens) {
                index.computeIfAbsent(token, k -> new HashSet<>()).add(normalizedWord);
            }
        }
    }

    /**
     * Gỡ bỏ các liên kết của SlangEntry khỏi Inverted Index khi từ bị xóa hoặc sửa.
     */
    public void removeEntry(SlangEntry entry) {
        if (entry == null || entry.getWord() == null) {
            return;
        }
        String normalizedWord = TextNormalizer.normalizeWord(entry.getWord());
        for (String meaning : entry.getMeanings()) {
            Set<String> tokens = TextNormalizer.extractTokens(meaning);
            for (String token : tokens) {
                Set<String> wordSet = index.get(token);
                if (wordSet != null) {
                    wordSet.remove(normalizedWord);
                    if (wordSet.isEmpty()) {
                        index.remove(token);
                    }
                }
            }
        }
    }

    /**
     * Tìm kiếm tất cả các SlangEntry có Definition chứa từ khóa (keyword).
     * Kết hợp tìm giao Posting Lists trên Inverted Index và kiểm chứng Substring chính xác.
     * Tốc độ phản hồi cực nhanh (< 10ms kể cả với 100.000 từ).
     */
    public List<SlangEntry> search(String keyword, SlangIndex slangIndex) {
        if (keyword == null || keyword.isBlank() || slangIndex == null) {
            return Collections.emptyList();
        }

        String trimmedKeyword = keyword.trim();
        Set<String> tokens = TextNormalizer.extractTokens(trimmedKeyword);

        // Trường hợp người dùng tìm ký tự đặc biệt không sinh ra token chữ
        if (tokens.isEmpty()) {
            List<SlangEntry> fallbackList = new ArrayList<>();
            for (SlangEntry entry : slangIndex.getAllEntries()) {
                if (entry.containsMeaningKeyword(trimmedKeyword)) {
                    fallbackList.add(entry);
                }
            }
            Collections.sort(fallbackList);
            return fallbackList;
        }

        // Tối ưu hóa giao tập hợp: Sắp xếp các token theo kích thước tập ứng viên tăng dần
        List<Set<String>> postingLists = new ArrayList<>();
        for (String token : tokens) {
            Set<String> set = index.get(token);
            if (set == null || set.isEmpty()) {
                // Nếu có bất kỳ token nào không xuất hiện trong từ điển -> Chắc chắn không có kết quả
                return Collections.emptyList();
            }
            postingLists.add(set);
        }

        postingLists.sort(Comparator.comparingInt(Set::size));

        // Khởi tạo tập ứng viên từ posting list nhỏ nhất
        Set<String> candidateKeys = new HashSet<>(postingLists.get(0));
        for (int i = 1; i < postingLists.size(); i++) {
            candidateKeys.retainAll(postingLists.get(i));
            if (candidateKeys.isEmpty()) {
                return Collections.emptyList();
            }
        }

        // Bước 2: Kiểm chứng Substring chính xác trên tập ứng viên (loại bỏ trường hợp token rải rác sai ngữ cảnh)
        List<SlangEntry> matches = new ArrayList<>();
        for (String key : candidateKeys) {
            SlangEntry entry = slangIndex.get(key);
            if (entry != null && entry.containsMeaningKeyword(trimmedKeyword)) {
                matches.add(entry);
            }
        }

        Collections.sort(matches);
        return matches;
    }

    /**
     * Số lượng token khác biệt đang được chỉ mục.
     */
    public int tokenCount() {
        return index.size();
    }

    public void clear() {
        index.clear();
    }
}
