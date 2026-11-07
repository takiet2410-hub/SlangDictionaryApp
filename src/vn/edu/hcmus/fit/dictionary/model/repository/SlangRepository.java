package vn.edu.hcmus.fit.dictionary.model.repository;

import vn.edu.hcmus.fit.dictionary.model.entity.SearchHistoryEntry;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.model.index.AutocompleteTrie;
import vn.edu.hcmus.fit.dictionary.model.index.IndexCacheData;
import vn.edu.hcmus.fit.dictionary.model.index.InvertedDefinitionIndex;
import vn.edu.hcmus.fit.dictionary.model.index.SlangIndex;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface Repository quản lý nạp và lưu trữ dữ liệu Slang, Lịch sử và Index Cache.
 */
public interface SlangRepository {

    /**
     * Nạp dữ liệu gốc ban đầu từ slang_base.txt.
     */
    Map<String, SlangEntry> loadBaseData() throws IOException;

    /**
     * Nạp dữ liệu làm việc từ slang_user.txt.
     * Nếu file slang_user.txt chưa tồn tại, tự động sao chép từ slang_base.txt.
     */
    Map<String, SlangEntry> loadUserData() throws IOException;

    /**
     * Lưu danh sách các mục từ làm việc xuống file slang_user.txt bằng cơ chế Safe Atomic Write.
     */
    void saveUserData(Collection<SlangEntry> entries) throws IOException;

    /**
     * Khôi phục slang_user.txt về nguyên bản từ slang_base.txt.
     */
    void resetUserData() throws IOException;

    /**
     * Tính mã băm SHA-256 của file slang_user.txt hiện tại.
     */
    String getWorkingFileSha256() throws IOException;

    /**
     * Kiểm tra xem Index Cache đã lưu có còn hợp lệ so với mã SHA-256 của slang_user.txt không.
     */
    boolean hasValidIndexCache(String currentSha256);

    /**
     * Nạp Index Cache đã serialize từ đĩa vào RAM.
     */
    IndexCacheData loadIndexCache();

    /**
     * Serialize và lưu Index Cache cùng Metadata xuống đĩa.
     */
    void saveIndexCache(SlangIndex slangIndex, InvertedDefinitionIndex defIndex,
                        AutocompleteTrie trie, String sourceSha256);

    /**
     * Nạp toàn bộ lịch sử tìm kiếm từ search_history.txt.
     */
    List<SearchHistoryEntry> loadHistory();

    /**
     * Ghi thêm một dòng vào file search_history.txt.
     */
    void appendHistory(SearchHistoryEntry entry);

    /**
     * Xóa toàn bộ lịch sử tìm kiếm.
     */
    void clearHistory();
}
