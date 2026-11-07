package vn.edu.hcmus.fit.dictionary.model.entity;

import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Thực thể đại diện cho một mục từ lóng (Slang Entry) trong từ điển.
 * Một slang word có thể chứa nhiều nghĩa (definitions).
 */
public class SlangEntry implements Serializable, Comparable<SlangEntry> {
    @Serial
    private static final long serialVersionUID = 1L;

    private String word;
    private final List<String> meanings;

    public SlangEntry(String word, List<String> meanings) {
        this.word = (word != null) ? word.trim() : "";
        this.meanings = new ArrayList<>();
        if (meanings != null) {
            for (String m : meanings) {
                if (m != null && !m.isBlank()) {
                    String clean = m.trim();
                    if (!this.meanings.contains(clean)) {
                        this.meanings.add(clean);
                    }
                }
            }
        }
    }

    public SlangEntry(String word, String singleMeaning) {
        this(word, singleMeaning != null ? Collections.singletonList(singleMeaning) : Collections.emptyList());
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = (word != null) ? word.trim() : "";
    }

    /**
     * Trả về danh sách các nghĩa (bảo đảm an toàn bất biến từ bên ngoài).
     */
    public List<String> getMeanings() {
        return Collections.unmodifiableList(meanings);
    }

    /**
     * Thêm một nghĩa mới (không thêm nếu đã tồn tại).
     */
    public boolean addMeaning(String meaning) {
        if (meaning == null || meaning.isBlank()) {
            return false;
        }
        String clean = meaning.trim();
        if (!meanings.contains(clean)) {
            return meanings.add(clean);
        }
        return false;
    }

    /**
     * Ghi đè toàn bộ danh sách nghĩa bằng danh sách mới.
     */
    public void setMeanings(List<String> newMeanings) {
        this.meanings.clear();
        if (newMeanings != null) {
            for (String m : newMeanings) {
                if (m != null && !m.isBlank()) {
                    String clean = m.trim();
                    if (!this.meanings.contains(clean)) {
                        this.meanings.add(clean);
                    }
                }
            }
        }
    }

    /**
     * Trả về chuỗi các nghĩa nối nhau bởi "| ".
     */
    public String getMeaningsFormatted() {
        return TextNormalizer.joinMeanings(meanings);
    }

    /**
     * Kiểm tra xem định nghĩa của từ có chứa keyword không (không phân biệt hoa thường).
     */
    public boolean containsMeaningKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        for (String m : meanings) {
            if (TextNormalizer.containsIgnoreCase(m, keyword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlangEntry that = (SlangEntry) o;
        return word.equalsIgnoreCase(that.word);
    }

    @Override
    public int hashCode() {
        return word.toUpperCase(Locale.ROOT).hashCode();
    }

    @Override
    public int compareTo(SlangEntry other) {
        if (other == null) return 1;
        return this.word.compareToIgnoreCase(other.word);
    }

    @Override
    public String toString() {
        return word + "`" + getMeaningsFormatted();
    }
}
