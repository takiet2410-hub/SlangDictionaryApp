package vn.edu.hcmus.fit.dictionary.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hằng số toàn cục cho ứng dụng Slang Dictionary.
 */
public final class Constants {
    private Constants() {
        // Chặn khởi tạo instance
    }

    // Đường dẫn thư mục dữ liệu
    public static final Path DATA_DIR = Paths.get("data");
    public static final Path SLANG_RAW_FILE = DATA_DIR.resolve("slang.txt");
    public static final Path SLANG_BASE_FILE = DATA_DIR.resolve("slang_base.txt");
    public static final Path SLANG_USER_FILE = DATA_DIR.resolve("slang_user.txt");
    public static final Path SEARCH_HISTORY_FILE = DATA_DIR.resolve("search_history.txt");
    
    // Thư mục lưu trữ Index Serialization Cache
    public static final Path INDEX_CACHE_DIR = DATA_DIR.resolve("index");
    public static final Path SLANG_INDEX_CACHE = INDEX_CACHE_DIR.resolve("slang_index.ser");
    public static final Path DEF_INDEX_CACHE = INDEX_CACHE_DIR.resolve("def_index.ser");
    public static final Path METADATA_CACHE = INDEX_CACHE_DIR.resolve("metadata.properties");

    // Ký tự phân cách
    public static final String ENTRY_DELIMITER = "`";
    public static final String MEANING_DELIMITER = "|";
    public static final String MEANING_DISPLAY_DELIMITER = "| ";
    public static final String HISTORY_DELIMITER = "||";

    // Tiêu đề file dữ liệu
    public static final String DATA_FILE_HEADER = "Slang`Meaning";

    // Cấu hình Autocomplete & Tìm kiếm
    public static final int DEFAULT_AUTOCOMPLETE_LIMIT = 10;
    public static final int QUIZ_OPTIONS_COUNT = 4;
}
