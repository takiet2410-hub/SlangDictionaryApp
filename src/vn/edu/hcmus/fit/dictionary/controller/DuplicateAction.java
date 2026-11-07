package vn.edu.hcmus.fit.dictionary.controller;

/**
 * Lựa chọn xử lý khi người dùng thêm một Slang Word đã tồn tại trong từ điển:
 * - OVERWRITE: Ghi đè toàn bộ định nghĩa cũ bằng định nghĩa mới.
 * - DUPLICATE: Giữ nguyên định nghĩa cũ và nối thêm định nghĩa mới.
 * - CANCEL: Hủy thao tác thêm từ.
 */
public enum DuplicateAction {
    OVERWRITE("Ghi đè định nghĩa cũ"),
    DUPLICATE("Thêm vào danh sách nghĩa (Duplicate)"),
    CANCEL("Hủy bỏ");

    private final String description;

    DuplicateAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
