package vn.edu.hcmus.fit.dictionary.model.event;

import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;

/**
 * Interface Observer cho phép các View (hoặc Controller) đăng ký lắng nghe
 * các sự kiện thay đổi dữ liệu từ SlangDictionaryModel.
 * Đảm bảo các Panel tự động cập nhật đồng bộ mà không bị thắt chặt phụ thuộc (decoupled).
 */
public interface DictionaryModelListener {

    /**
     * Kích hoạt khi dữ liệu từ điển đã được nạp xong vào RAM.
     */
    default void onDictionaryLoaded(int totalWords) {}

    /**
     * Kích hoạt khi có bất kỳ thay đổi nào về dữ liệu từ điển.
     */
    default void onDictionaryChanged() {}

    /**
     * Kích hoạt khi một mục từ mới được thêm.
     */
    default void onEntryAdded(SlangEntry entry) {}

    /**
     * Kích hoạt khi một mục từ được chỉnh sửa.
     */
    default void onEntryUpdated(SlangEntry entry) {}

    /**
     * Kích hoạt khi một mục từ bị xóa.
     */
    default void onEntryDeleted(String word) {}

    /**
     * Kích hoạt khi từ điển được Reset về dữ liệu gốc.
     */
    default void onDictionaryReset() {}

    /**
     * Kích hoạt khi lịch sử tìm kiếm có cập nhật mới hoặc bị xóa.
     */
    default void onHistoryUpdated() {}
}
