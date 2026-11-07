package vn.edu.hcmus.fit.dictionary.model.entity;

import java.util.Collections;
import java.util.List;

/**
 * Kết quả tìm kiếm kèm thời gian thực thi (nano-giây / mili-giây) để đo lường hiệu năng.
 */
public class SearchResult {
    private final SearchType searchType;
    private final String keyword;
    private final List<SlangEntry> results;
    private final long elapsedNanos;

    public SearchResult(SearchType searchType, String keyword, List<SlangEntry> results, long elapsedNanos) {
        this.searchType = searchType;
        this.keyword = (keyword != null) ? keyword : "";
        this.results = (results != null) ? Collections.unmodifiableList(results) : Collections.emptyList();
        this.elapsedNanos = elapsedNanos;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<SlangEntry> getResults() {
        return results;
    }

    public int getCount() {
        return results.size();
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public long getElapsedNanos() {
        return elapsedNanos;
    }

    public double getElapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }

    /**
     * Kiểm tra tiêu chí tốc độ < 1 giây của đề bài (1s = 1.000.000.000 ns).
     */
    public boolean isUnderOneSecond() {
        return elapsedNanos < 1_000_000_000L;
    }

    @Override
    public String toString() {
        return String.format("Tìm kiếm '%s' (%s): %d kết quả (%.3f ms)",
                keyword, searchType.getDisplayName(), results.size(), getElapsedMillis());
    }
}
