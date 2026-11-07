package vn.edu.hcmus.fit.dictionary.model.repository;

import vn.edu.hcmus.fit.dictionary.model.entity.SearchHistoryEntry;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.model.index.*;
import vn.edu.hcmus.fit.dictionary.util.Constants;
import vn.edu.hcmus.fit.dictionary.util.FileUtils;
import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Cài đặt SlangRepository thao tác trực tiếp với File System an toàn.
 * Đảm bảo: Safe Atomic Write, UTF-8 chuẩn xác, Index Serialization, Tự phục hồi khi cache hỏng.
 */
public class FileSlangRepository implements SlangRepository {

    private final Path baseFile;
    private final Path userFile;
    private final Path historyFile;
    private final Path indexDir;
    private final Path cacheFile;
    private final Path metadataFile;

    public FileSlangRepository() {
        this(Constants.SLANG_BASE_FILE, Constants.SLANG_USER_FILE,
                Constants.SEARCH_HISTORY_FILE, Constants.INDEX_CACHE_DIR);
    }

    public FileSlangRepository(Path baseFile, Path userFile, Path historyFile, Path indexDir) {
        this.baseFile = baseFile;
        this.userFile = userFile;
        this.historyFile = historyFile;
        this.indexDir = indexDir;
        this.cacheFile = indexDir.resolve("index_cache.ser");
        this.metadataFile = indexDir.resolve("metadata.properties");
    }

    @Override
    public Map<String, SlangEntry> loadBaseData() throws IOException {
        return readSlangFile(baseFile);
    }

    @Override
    public Map<String, SlangEntry> loadUserData() throws IOException {
        if (!Files.exists(userFile)) {
            // Nếu chưa có file làm việc của user, sao chép từ baseline đã chuẩn hóa
            if (Files.exists(baseFile)) {
                FileUtils.copyFile(baseFile, userFile);
            } else {
                throw new FileNotFoundException("Không tìm thấy file baseline: " + baseFile);
            }
        }
        return readSlangFile(userFile);
    }

    @Override
    public void saveUserData(Collection<SlangEntry> entries) throws IOException {
        List<String> lines = new ArrayList<>(entries.size() + 1);
        lines.add(Constants.DATA_FILE_HEADER);

        List<SlangEntry> sortedList = new ArrayList<>(entries);
        Collections.sort(sortedList);

        for (SlangEntry entry : sortedList) {
            lines.add(entry.getWord() + Constants.ENTRY_DELIMITER + entry.getMeaningsFormatted());
        }

        FileUtils.safeWriteLines(userFile, lines);
    }

    @Override
    public void resetUserData() throws IOException {
        if (!Files.exists(baseFile)) {
            throw new FileNotFoundException("Không tìm thấy file baseline: " + baseFile);
        }
        FileUtils.copyFile(baseFile, userFile);
    }

    @Override
    public String getWorkingFileSha256() throws IOException {
        return FileUtils.calculateSha256(userFile);
    }

    @Override
    public boolean hasValidIndexCache(String currentSha256) {
        if (!Files.exists(cacheFile) || !Files.exists(metadataFile) || currentSha256 == null || currentSha256.isBlank()) {
            return false;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(metadataFile)) {
            props.load(in);
            String cachedSha = props.getProperty("sourceSha256", "");
            return currentSha256.equalsIgnoreCase(cachedSha.trim());
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public IndexCacheData loadIndexCache() {
        if (!Files.exists(cacheFile)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(cacheFile)))) {
            Object obj = ois.readObject();
            if (obj instanceof IndexCacheData data) {
                return data;
            }
        } catch (Exception e) {
            System.err.println("Cảnh báo: Không thể nạp Index Cache (.ser), hệ thống sẽ tự động rebuild: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void saveIndexCache(SlangIndex slangIndex, InvertedDefinitionIndex defIndex,
                               AutocompleteTrie trie, String sourceSha256) {
        try {
            FileUtils.ensureDirectory(indexDir);
            IndexMetadata metadata = new IndexMetadata("1.0", sourceSha256, slangIndex.size());
            IndexCacheData cacheData = new IndexCacheData(slangIndex, defIndex, trie, metadata);

            Path tempCache = cacheFile.resolveSibling(cacheFile.getFileName().toString() + ".tmp");
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(tempCache)))) {
                oos.writeObject(cacheData);
                oos.flush();
            }
            Files.move(tempCache, cacheFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Lưu file metadata.properties
            Properties props = new Properties();
            props.setProperty("version", metadata.getVersion());
            props.setProperty("sourceSha256", metadata.getSourceSha256());
            props.setProperty("entryCount", String.valueOf(metadata.getEntryCount()));
            props.setProperty("timestamp", String.valueOf(metadata.getCreatedTimestamp()));

            try (OutputStream out = Files.newOutputStream(metadataFile)) {
                props.store(out, "Slang Dictionary Index Cache Metadata");
            }
        } catch (Exception e) {
            System.err.println("Cảnh báo: Không thể ghi Index Cache: " + e.getMessage());
        }
    }

    @Override
    public List<SearchHistoryEntry> loadHistory() {
        if (!Files.exists(historyFile)) {
            return new ArrayList<>();
        }

        List<SearchHistoryEntry> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(historyFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                SearchHistoryEntry entry = SearchHistoryEntry.fromFileString(line);
                if (entry != null) {
                    list.add(entry);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file lịch sử: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void appendHistory(SearchHistoryEntry entry) {
        if (entry == null) {
            return;
        }
        try {
            if (historyFile.getParent() != null) {
                FileUtils.ensureDirectory(historyFile.getParent());
            }
            String line = entry.toFileString() + System.lineSeparator();
            Files.writeString(historyFile, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi lịch sử: " + e.getMessage());
        }
    }

    @Override
    public void clearHistory() {
        try {
            if (Files.exists(historyFile)) {
                Files.delete(historyFile);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa lịch sử: " + e.getMessage());
        }
    }

    /**
     * Phương thức đọc file slang dạng văn bản UTF-8 thành Map các SlangEntry.
     */
    private Map<String, SlangEntry> readSlangFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File không tồn tại: " + filePath);
        }

        Map<String, SlangEntry> resultMap = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    // Bỏ qua dòng header nếu có (Slang`Meaning hoặc Slag`Meaning)
                    if (line.startsWith("Slang`") || line.startsWith("Slag`")) {
                        continue;
                    }
                }

                if (line.isBlank()) {
                    continue;
                }

                int delimiterIndex = line.indexOf(Constants.ENTRY_DELIMITER);
                if (delimiterIndex == -1) {
                    continue; // Bỏ qua dòng không đúng định dạng
                }

                String word = line.substring(0, delimiterIndex).trim();
                String meaningPart = line.substring(delimiterIndex + 1).trim();

                if (word.isEmpty()) {
                    continue;
                }

                List<String> meanings = TextNormalizer.parseMeanings(meaningPart);
                String normalizedKey = TextNormalizer.normalizeWord(word);

                if (resultMap.containsKey(normalizedKey)) {
                    // Nếu từ đã tồn tại, nối thêm nghĩa mới
                    SlangEntry existing = resultMap.get(normalizedKey);
                    for (String m : meanings) {
                        existing.addMeaning(m);
                    }
                } else {
                    resultMap.put(normalizedKey, new SlangEntry(word, meanings));
                }
            }
        }

        return resultMap;
    }
}
