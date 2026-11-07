package vn.edu.hcmus.fit.dictionary.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Tiện ích chuẩn hóa chuỗi và tách từ khóa cho Slang Dictionary.
 */
public final class TextNormalizer {
    private static final Pattern TOKEN_SPLIT_PATTERN = Pattern.compile("[\\s,;:.!?'\"()\\[\\]{}|/\\\\<>+*~`@#$%^&_=-]+");

    private TextNormalizer() {
        // Chặn khởi tạo
    }

    /**
     * Chuẩn hóa từ slang: loại bỏ khoảng trắng thừa ở 2 đầu và chuyển thành chữ in hoa.
     */
    public static String normalizeWord(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Chuẩn hóa từ khóa tìm kiếm: loại bỏ khoảng trắng thừa.
     */
    public static String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }
        return keyword.trim();
    }

    /**
     * Tách chuỗi định nghĩa gộp (ngăn cách bởi dấu |) thành danh sách các nghĩa riêng biệt.
     */
    public static List<String> parseMeanings(String rawMeanings) {
        if (rawMeanings == null || rawMeanings.isBlank()) {
            return Collections.emptyList();
        }
        String[] parts = rawMeanings.split("\\" + Constants.MEANING_DELIMITER);
        List<String> list = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && !list.contains(trimmed)) {
                list.add(trimmed);
            }
        }
        return list;
    }

    /**
     * Nối danh sách các nghĩa thành 1 chuỗi theo chuẩn định dạng "| ".
     */
    public static String joinMeanings(List<String> meanings) {
        if (meanings == null || meanings.isEmpty()) {
            return "";
        }
        return String.join(Constants.MEANING_DISPLAY_DELIMITER, meanings);
    }

    /**
     * Tách văn bản định nghĩa thành các token chữ thường để đánh chỉ mục đảo (Inverted Index).
     */
    public static Set<String> extractTokens(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptySet();
        }
        String[] tokens = TOKEN_SPLIT_PATTERN.split(text.toLowerCase(Locale.ROOT));
        Set<String> tokenSet = new HashSet<>();
        for (String token : tokens) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                tokenSet.add(trimmed);
            }
        }
        return tokenSet;
    }

    /**
     * Kiểm tra chuỗi target có chứa chuỗi search không (không phân biệt hoa/thường).
     */
    public static boolean containsIgnoreCase(String target, String search) {
        if (target == null || search == null) {
            return false;
        }
        return target.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }
}
