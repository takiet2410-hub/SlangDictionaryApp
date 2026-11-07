package vn.edu.hcmus.fit.dictionary.model.index;

import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Cấu trúc Index tra cứu Slang Word trực tiếp với độ phức tạp O(1).
 * Sử dụng HashMap với Key là từ slang chuẩn hóa in hoa.
 */
public class SlangIndex implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, SlangEntry> entries;

    public SlangIndex() {
        this.entries = new HashMap<>();
    }

    public SlangIndex(int initialCapacity) {
        this.entries = new HashMap<>(initialCapacity);
    }

    /**
     * Thêm hoặc cập nhật một SlangEntry vào Index.
     */
    public void put(SlangEntry entry) {
        if (entry != null && entry.getWord() != null) {
            String key = TextNormalizer.normalizeWord(entry.getWord());
            entries.put(key, entry);
        }
    }

    /**
     * Tra cứu SlangEntry theo từ slang với độ phức tạp O(1).
     * Không phân biệt chữ hoa/thường.
     */
    public SlangEntry get(String word) {
        if (word == null) {
            return null;
        }
        String key = TextNormalizer.normalizeWord(word);
        return entries.get(key);
    }

    /**
     * Kiểm tra sự tồn tại của từ trong Index O(1).
     */
    public boolean containsKey(String word) {
        if (word == null) {
            return false;
        }
        return entries.containsKey(TextNormalizer.normalizeWord(word));
    }

    /**
     * Xóa từ khỏi Index O(1).
     */
    public SlangEntry remove(String word) {
        if (word == null) {
            return null;
        }
        return entries.remove(TextNormalizer.normalizeWord(word));
    }

    /**
     * Số lượng từ hiện có.
     */
    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Trả về danh sách tất cả các mục từ hiện có.
     */
    public Collection<SlangEntry> getAllEntries() {
        return entries.values();
    }

    /**
     * Trả về danh sách tất cả các từ slang gốc.
     */
    public List<String> getAllWords() {
        List<String> words = new ArrayList<>(entries.size());
        for (SlangEntry entry : entries.values()) {
            words.add(entry.getWord());
        }
        return words;
    }

    /**
     * Xóa toàn bộ dữ liệu.
     */
    public void clear() {
        entries.clear();
    }
}
