package vn.edu.hcmus.fit.dictionary.controller;

import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchHistoryEntry;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchResult;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Controller chính điều phối các nghiệp vụ: Tìm kiếm (Word/Def), Quản lý CRUD (Add với
 * Overwrite/Duplicate, Edit, Delete có confirm, Reset có confirm), Lịch sử và Random.
 * Đóng vai trò cầu nối trung gian giữa View Swing và SlangDictionaryModel.
 */
public class DictionaryController {

    private final SlangDictionaryModel model;

    @FunctionalInterface
    public interface DuplicateResolver {
        DuplicateAction resolve(String word, SlangEntry existingEntry);
    }

    @FunctionalInterface
    public interface ConfirmationHandler {
        boolean confirm(String message, String title);
    }

    public DictionaryController(SlangDictionaryModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model không được là null.");
        }
        this.model = model;
    }

    public SlangDictionaryModel getModel() {
        return model;
    }

    // ==================== TÌM KIẾM ====================

    /**
     * Xử lý yêu cầu tìm kiếm theo Slang Word.
     */
    public SearchResult searchSlang(String word) {
        String cleanWord = TextNormalizer.normalizeKeyword(word);
        if (cleanWord.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập từ slang cần tìm.");
        }
        return model.searchBySlangWord(cleanWord);
    }

    /**
     * Xử lý yêu cầu tìm kiếm theo Definition chứa từ khóa.
     */
    public SearchResult searchDefinition(String keyword) {
        String cleanKeyword = TextNormalizer.normalizeKeyword(keyword);
        if (cleanKeyword.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập từ khóa định nghĩa cần tìm.");
        }
        return model.searchByDefinition(cleanKeyword);
    }

    // ==================== QUẢN LÝ (CRUD) ====================

    /**
     * Thêm một Slang Word mới.
     * Nếu từ đã tồn tại, kích hoạt duplicateResolver để người dùng chọn Overwrite hoặc Duplicate.
     */
    public String addSlang(String word, String rawMeanings, DuplicateResolver duplicateResolver) throws IOException {
        String cleanWord = TextNormalizer.normalizeKeyword(word);
        if (cleanWord.isEmpty()) {
            throw new IllegalArgumentException("Từ slang không được để trống.");
        }

        List<String> meanings = TextNormalizer.parseMeanings(rawMeanings);
        if (meanings.isEmpty()) {
            throw new IllegalArgumentException("Định nghĩa không được để trống (ngăn cách các nghĩa bằng dấu |).");
        }

        if (model.containsWord(cleanWord)) {
            SlangEntry existing = model.getEntry(cleanWord);
            DuplicateAction choice = (duplicateResolver != null)
                    ? duplicateResolver.resolve(cleanWord, existing)
                    : DuplicateAction.CANCEL;

            if (choice == DuplicateAction.OVERWRITE) {
                model.addSlangWord(cleanWord, meanings, true);
                return String.format("Đã ghi đè định nghĩa cho từ '%s'.", cleanWord);
            } else if (choice == DuplicateAction.DUPLICATE) {
                model.addSlangWord(cleanWord, meanings, false);
                return String.format("Đã thêm định nghĩa mới (Duplicate) cho từ '%s'.", cleanWord);
            } else {
                return "Đã hủy thao tác thêm từ.";
            }
        } else {
            model.addSlangWord(cleanWord, meanings, false);
            return String.format("Thêm thành công từ lóng mới: '%s'.", cleanWord);
        }
    }

    /**
     * Chỉnh sửa một Slang Word đã có trong từ điển.
     */
    public String editSlang(String oldWord, String newWord, String newMeaningsRaw) throws IOException {
        String cleanOld = TextNormalizer.normalizeKeyword(oldWord);
        String cleanNew = TextNormalizer.normalizeKeyword(newWord);

        if (cleanOld.isEmpty() || cleanNew.isEmpty()) {
            throw new IllegalArgumentException("Tên từ slang không được để trống.");
        }

        List<String> newMeanings = TextNormalizer.parseMeanings(newMeaningsRaw);
        if (newMeanings.isEmpty()) {
            throw new IllegalArgumentException("Định nghĩa mới không được để trống.");
        }

        model.editSlangWord(cleanOld, cleanNew, newMeanings);
        return String.format("Cập nhật thành công từ '%s'.", cleanNew);
    }

    /**
     * Xóa một Slang Word khỏi từ điển (bắt buộc xác nhận trước khi xóa).
     */
    public boolean deleteSlang(String word, ConfirmationHandler confirmationHandler) throws IOException {
        String cleanWord = TextNormalizer.normalizeKeyword(word);
        if (cleanWord.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn từ cần xóa.");
        }

        if (!model.containsWord(cleanWord)) {
            throw new IllegalArgumentException(String.format("Từ '%s' không tồn tại trong từ điển.", cleanWord));
        }

        boolean confirmed = (confirmationHandler != null)
                && confirmationHandler.confirm(
                String.format("Bạn có chắc chắn muốn xóa từ '%s' khỏi từ điển không?", cleanWord),
                "Xác nhận xóa từ lóng"
        );

        if (confirmed) {
            return model.deleteSlangWord(cleanWord);
        }
        return false;
    }

    /**
     * Khôi phục toàn bộ từ điển về dữ liệu gốc baseline (bắt buộc xác nhận trước khi reset).
     */
    public boolean resetDictionary(ConfirmationHandler confirmationHandler) throws IOException {
        boolean confirmed = (confirmationHandler != null)
                && confirmationHandler.confirm(
                "Bạn có chắc chắn muốn khôi phục từ điển về dữ liệu gốc ban đầu?\n" +
                        "Mọi thay đổi thêm, sửa, xóa trước đó sẽ bị hủy bỏ.",
                "Xác nhận Reset từ điển"
        );

        if (confirmed) {
            model.resetToOriginal();
            return true;
        }
        return false;
    }

    // ==================== RANDOM & HISTORY ====================

    /**
     * Lấy 1 Slang Word ngẫu nhiên ("On this day slang word") với độ phức tạp O(1).
     */
    public SlangEntry getRandomSlang() {
        return model.getRandomSlang();
    }

    /**
     * Lấy toàn bộ lịch sử tra cứu.
     */
    public List<SearchHistoryEntry> getHistory() {
        return model.getHistory();
    }

    /**
     * Xóa toàn bộ lịch sử tra cứu (có xác nhận).
     */
    public boolean clearHistory(ConfirmationHandler confirmationHandler) {
        boolean confirmed = (confirmationHandler != null)
                && confirmationHandler.confirm("Bạn có chắc chắn muốn xóa toàn bộ lịch sử tra cứu?", "Xác nhận xóa lịch sử");
        if (confirmed) {
            model.clearHistory();
            return true;
        }
        return false;
    }

    public Collection<SlangEntry> getAllEntries() {
        return model.getAllEntries();
    }

    public int getTotalWordsCount() {
        return model.size();
    }
}
