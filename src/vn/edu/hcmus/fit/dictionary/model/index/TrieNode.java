package vn.edu.hcmus.fit.dictionary.model.index;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Nút trong cây tiền tố (Prefix Trie) phục vụ tính năng Autocomplete.
 */
public class TrieNode implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Sử dụng TreeMap để các nhánh con tự động sắp xếp theo thứ tự bảng chữ cái A-Z
    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private String originalWord;

    public TrieNode() {
        this.children = new TreeMap<>();
        this.isEndOfWord = false;
        this.originalWord = null;
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }
}
