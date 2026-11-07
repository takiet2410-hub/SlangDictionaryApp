package test;

import vn.edu.hcmus.fit.dictionary.controller.AutocompleteController;
import vn.edu.hcmus.fit.dictionary.controller.DictionaryController;
import vn.edu.hcmus.fit.dictionary.controller.DuplicateAction;
import vn.edu.hcmus.fit.dictionary.controller.QuizController;
import vn.edu.hcmus.fit.dictionary.model.QuizModel;
import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.model.entity.*;

import java.util.List;

/**
 * Bộ kiểm thử toàn diện nghiệm thu Milestone 3:
 * Tầng Controller & Xử lý Nghiệp vụ (DictionaryController, QuizController, AutocompleteController).
 */
public class Milestone3Test {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("       KIỂM THỬ NGHIỆM THU MILESTONE 3 (TẦNG CONTROLLER)    ");
        System.out.println("============================================================");

        int passed = 0;
        int total = 10;

        try {
            // Khởi tạo Model và các Controller
            SlangDictionaryModel model = new SlangDictionaryModel();
            model.initialize();

            DictionaryController dictController = new DictionaryController(model);
            QuizModel quizModel = new QuizModel(model);
            QuizController quizController = new QuizController(quizModel);
            AutocompleteController autoController = new AutocompleteController(model, 10);

            // 1. Kiểm thử Tìm kiếm Slang Word qua Controller
            System.out.print("[Test 1/10] Controller - Tìm kiếm Slang Word & Validate... ");
            SearchResult rLol = dictController.searchSlang("LOL");
            assert !rLol.isEmpty() : "Không tìm thấy LOL";
            assert rLol.getResults().get(0).getMeaningsFormatted().toLowerCase().contains("laugh");

            // Kiểm tra validate rỗng
            boolean caughtBlankWord = false;
            try {
                dictController.searchSlang("   ");
            } catch (IllegalArgumentException e) {
                caughtBlankWord = true;
            }
            assert caughtBlankWord : "Không bắt lỗi khi truyền từ rỗng";
            System.out.println("PASSED");
            passed++;

            // 2. Kiểm thử Tìm kiếm Definition qua Controller
            System.out.print("[Test 2/10] Controller - Tìm kiếm Definition & Validate... ");
            SearchResult rMoney = dictController.searchDefinition("money");
            assert !rMoney.isEmpty() : "Không tìm thấy kết quả definition cho 'money'";

            boolean caughtBlankDef = false;
            try {
                dictController.searchDefinition("");
            } catch (IllegalArgumentException e) {
                caughtBlankDef = true;
            }
            assert caughtBlankDef : "Không bắt lỗi khi truyền keyword rỗng";
            System.out.println("PASSED");
            passed++;

            // 3. Kiểm thử Thêm mới Slang Word
            System.out.print("[Test 3/10] Controller - Thêm từ mới hoàn toàn... ");
            String testWord = "CTRL_TEST_01";
            String addMsg = dictController.addSlang(testWord, "Mean A| Mean B", null);
            assert dictController.getModel().containsWord(testWord) : "Từ mới chưa vào Model";
            assert addMsg.contains("Thêm thành công") : "Thông báo sai: " + addMsg;
            System.out.println("PASSED");
            passed++;

            // 4. Kiểm thử Thêm trùng với DuplicateResolver (OVERWRITE vs DUPLICATE vs CANCEL)
            System.out.print("[Test 4/10] Controller - Thêm trùng (Overwrite, Duplicate, Cancel)... ");
            // 4a. Cancel
            String cancelMsg = dictController.addSlang(testWord, "Mean C", (word, existing) -> DuplicateAction.CANCEL);
            assert cancelMsg.contains("Đã hủy") : "Không hủy đúng cách";
            assert dictController.getModel().getEntry(testWord).getMeanings().size() == 2;

            // 4b. Duplicate (nối thêm nghĩa)
            String dupMsg = dictController.addSlang(testWord, "Mean C", (word, existing) -> DuplicateAction.DUPLICATE);
            assert dupMsg.contains("Duplicate") : "Thông báo duplicate sai: " + dupMsg;
            assert dictController.getModel().getEntry(testWord).getMeanings().size() == 3;

            // 4c. Overwrite (ghi đè toàn bộ)
            String ovwMsg = dictController.addSlang(testWord, "Sole New Mean", (word, existing) -> DuplicateAction.OVERWRITE);
            assert ovwMsg.contains("Đã ghi đè") : "Thông báo overwrite sai: " + ovwMsg;
            assert dictController.getModel().getEntry(testWord).getMeanings().size() == 1;
            assert dictController.getModel().getEntry(testWord).getMeanings().get(0).equals("Sole New Mean");
            System.out.println("PASSED");
            passed++;

            // 5. Kiểm thử Chỉnh sửa (Edit) Slang Word
            System.out.print("[Test 5/10] Controller - Chỉnh sửa (Edit) Slang Word... ");
            String editedWord = "CTRL_TEST_01_EDITED";
            dictController.editSlang(testWord, editedWord, "Edited Meaning X| Edited Meaning Y");
            assert !dictController.getModel().containsWord(testWord) : "Từ cũ vẫn còn sau khi đổi tên";
            assert dictController.getModel().containsWord(editedWord) : "Từ mới không tồn tại";
            assert dictController.getModel().getEntry(editedWord).getMeanings().size() == 2;
            System.out.println("PASSED");
            passed++;

            // 6. Kiểm thử Xóa (Delete) có Confirm
            System.out.print("[Test 6/10] Controller - Xóa (Delete) có xác nhận... ");
            // Người dùng chọn Cancel (Không xóa)
            boolean canceledDelete = dictController.deleteSlang(editedWord, (msg, title) -> false);
            assert !canceledDelete : "Vẫn xóa khi người dùng chọn No";
            assert dictController.getModel().containsWord(editedWord) : "Từ bị mất khi chọn No";

            // Người dùng chọn Yes (Đồng ý xóa)
            boolean confirmedDelete = dictController.deleteSlang(editedWord, (msg, title) -> true);
            assert confirmedDelete : "Xóa thất bại khi người dùng chọn Yes";
            assert !dictController.getModel().containsWord(editedWord) : "Từ vẫn còn sau khi xóa";
            System.out.println("PASSED");
            passed++;

            // 7. Kiểm thử Khôi phục gốc (Reset) có Confirm
            System.out.print("[Test 7/10] Controller - Khôi phục gốc (Reset) có xác nhận... ");
            // Thêm 1 từ tạm để thử reset
            dictController.addSlang("TEMP_WORD", "Temp", null);
            assert dictController.getModel().containsWord("TEMP_WORD");

            // Chọn No -> không reset
            boolean resetCanceled = dictController.resetDictionary((msg, title) -> false);
            assert !resetCanceled : "Đã reset khi người dùng chọn No";
            assert dictController.getModel().containsWord("TEMP_WORD");

            // Chọn Yes -> reset thành công
            boolean resetConfirmed = dictController.resetDictionary((msg, title) -> true);
            assert resetConfirmed : "Reset thất bại khi chọn Yes";
            assert !dictController.getModel().containsWord("TEMP_WORD");
            assert dictController.getTotalWordsCount() == 7638;
            System.out.println("PASSED");
            passed++;

            // 8. Kiểm thử Lấy ngẫu nhiên & Lịch sử
            System.out.print("[Test 8/10] Controller - Random Word & Quản lý Lịch sử... ");
            SlangEntry randomWord = dictController.getRandomSlang();
            assert randomWord != null : "Random word null";

            List<SearchHistoryEntry> history = dictController.getHistory();
            assert !history.isEmpty() : "Lịch sử rỗng";

            boolean clearCanceled = dictController.clearHistory((msg, title) -> false);
            assert !clearCanceled : "Đã xóa lịch sử khi chọn No";

            boolean clearConfirmed = dictController.clearHistory((msg, title) -> true);
            assert clearConfirmed : "Không xóa được lịch sử khi chọn Yes";
            assert dictController.getHistory().isEmpty() : "Lịch sử vẫn còn sau khi clear";
            System.out.println("PASSED");
            passed++;

            // 9. Kiểm thử QuizController
            System.out.print("[Test 9/10] QuizController - Tạo câu hỏi & Chấm điểm... ");
            QuizQuestion qSlang = quizController.nextQuestion(QuizType.SLANG_TO_DEFINITION);
            assert qSlang.getOptions().size() == 4;

            // Nộp đáp án đúng
            QuizAnswerResult correctResult = quizController.submitAnswer(qSlang.getCorrectOptionIndex());
            assert correctResult.isCorrect() : "Chấm đáp án đúng thành sai";
            assert correctResult.currentScore() == 1;
            assert correctResult.currentStreak() == 1;

            // Tạo câu hỏi Def -> Slang
            QuizQuestion qDef = quizController.nextQuestion(QuizType.DEFINITION_TO_SLANG);
            assert qDef.getOptions().size() == 4;

            // Nộp đáp án sai cố tình (chọn vị trí khác correctIndex)
            int wrongIndex = (qDef.getCorrectOptionIndex() + 1) % 4;
            QuizAnswerResult wrongResult = quizController.submitAnswer(wrongIndex);
            assert !wrongResult.isCorrect() : "Chấm đáp án sai thành đúng";
            assert wrongResult.currentScore() == 1;
            assert wrongResult.currentStreak() == 0; // Streak bị reset

            quizController.resetGame();
            assert quizController.getScore() == 0;
            assert quizController.getTotalAnswered() == 0;
            System.out.println("PASSED");
            passed++;

            // 10. Kiểm thử AutocompleteController
            System.out.print("[Test 10/10] AutocompleteController - Gợi ý tiền tố... ");
            List<String> suggestions = autoController.getSuggestions("BR");
            assert !suggestions.isEmpty() : "Không tìm thấy gợi ý cho 'BR'";
            assert suggestions.size() <= 10 : "Vượt quá giới hạn 10 từ";

            final boolean[] callbackTriggered = {false};
            autoController.onTextChanged("LO", list -> {
                callbackTriggered[0] = true;
                assert !list.isEmpty() : "Callback list rỗng";
            });
            assert callbackTriggered[0] : "Callback onTextChanged không được kích hoạt";
            System.out.println("PASSED");
            passed++;

            System.out.println("============================================================");
            System.out.printf(" KẾT QUẢ: %d/%d TESTS PASSED (100%% HOÀN HẢO)%n", passed, total);
            System.out.println("============================================================");

        } catch (Throwable t) {
            System.err.println("\nFAIL TẠI BƯỚC KIỂM THỬ: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}
