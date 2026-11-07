package vn.edu.hcmus.fit.dictionary.model.entity;

import vn.edu.hcmus.fit.dictionary.util.Constants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thực thể lưu trữ một lần tra cứu trong lịch sử tìm kiếm.
 */
public class SearchHistoryEntry implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime timestamp;
    private final SearchType searchType;
    private final String keyword;
    private final int matchCount;

    public SearchHistoryEntry(LocalDateTime timestamp, SearchType searchType, String keyword, int matchCount) {
        this.timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
        this.searchType = (searchType != null) ? searchType : SearchType.SLANG_WORD;
        this.keyword = (keyword != null) ? keyword : "";
        this.matchCount = Math.max(0, matchCount);
    }

    public SearchHistoryEntry(SearchType searchType, String keyword, int matchCount) {
        this(LocalDateTime.now(), searchType, keyword, matchCount);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DISPLAY_FORMATTER);
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getMatchCount() {
        return matchCount;
    }

    /**
     * Chuyển thành định dạng dòng lưu file text:
     * TIMESTAMP||SEARCH_TYPE||KEYWORD||MATCH_COUNT
     */
    public String toFileString() {
        return timestamp.toString() + Constants.HISTORY_DELIMITER +
                searchType.name() + Constants.HISTORY_DELIMITER +
                keyword + Constants.HISTORY_DELIMITER +
                matchCount;
    }

    /**
     * Đọc từ chuỗi file text.
     */
    public static SearchHistoryEntry fromFileString(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|\\|");
        if (parts.length < 4) {
            return null;
        }
        try {
            LocalDateTime ts = LocalDateTime.parse(parts[0].trim());
            SearchType type = SearchType.valueOf(parts[1].trim());
            String kw = parts[2].trim();
            int count = Integer.parseInt(parts[3].trim());
            return new SearchHistoryEntry(ts, type, kw, count);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "[" + getFormattedTimestamp() + "] [" + searchType.getDisplayName() + "] \"" + keyword + "\" -> " + matchCount + " kết quả";
    }
}
