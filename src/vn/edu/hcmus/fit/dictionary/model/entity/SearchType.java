package vn.edu.hcmus.fit.dictionary.model.entity;

/**
 * Loại hình tra cứu trong từ điển.
 */
public enum SearchType {
    SLANG_WORD("Slang Word"),
    DEFINITION("Definition");

    private final String displayName;

    SearchType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
