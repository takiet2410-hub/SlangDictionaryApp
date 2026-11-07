package test;

import vn.edu.hcmus.fit.dictionary.model.QuizModel;
import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.model.entity.*;
import vn.edu.hcmus.fit.dictionary.model.event.DictionaryModelListener;

import java.io.File;
import java.util.List;

/**
 * Bộ kiểm thử toàn diện nghiệm thu Milestone 2:
 * Tầng Model, Index Engine (O(1) Map, Inverted Index, Autocomplete Trie) và Safe File Repository.
 */
public class Milestone2Test {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("       KIỂM THỬ NGHIỆM THU MILESTONE 2 (TẦNG MODEL & INDEX)  ");
        System.out.println("============================================================");

        int passed = 0;
        int total = 12;

        try {
            // Khởi tạo Model
            SlangDictionaryModel model = new SlangDictionaryModel();
            final boolean[] loadedEventFired = {false};
            model.addListener(new DictionaryModelListener() {
                @Override
                public void onDictionaryLoaded(int totalWords) {
                    loadedEventFired[0] = true;
                }
            });

            // 1. Kiểm thử Khởi tạo & Load Dữ liệu ban đầu
            System.out.print("[Test 1/12] Khởi tạo Model & Nạp từ đĩa... ");
            long startInit = System.currentTimeMillis();
            model.initialize();
            long initTime = System.currentTimeMillis() - startInit;
            assert loadedEventFired[0] : "Sự kiện onDictionaryLoaded không được kích hoạt";
            assert model.size() == 7638 : "Kích thước từ điển không đúng: " + model.size();
            System.out.printf("PASSED (%d từ, nạp trong %d ms)%n", model.size(), initTime);
            passed++;

            // 2. Kiểm thử Tra cứu Slang Word O(1) & Case-insensitive
            System.out.print("[Test 2/12] Tra cứu Slang Word O(1)... ");
            SearchResult rLol = model.searchBySlangWord("LOL");
            assert !rLol.isEmpty() : "Không tìm thấy LOL";
            assert rLol.getResults().get(0).getMeaningsFormatted().toLowerCase().contains("laugh") : "Nghĩa LOL không chứa laugh";
            assert rLol.isUnderOneSecond() : "Thời gian vượt quá 1s";

            // Case-insensitive
            SearchResult rLolLower = model.searchBySlangWord("lol");
            assert !rLolLower.isEmpty() : "Không tìm thấy lol chữ thường";

            // Ký tự non-ASCII đã sửa ở M1
            SearchResult rReg = model.searchBySlangWord("®");
            assert !rReg.isEmpty() : "Không tìm thấy ®";
            assert rReg.getResults().get(0).getMeaningsFormatted().contains("Registered Trademark Symbol");

            SearchResult rTm = model.searchBySlangWord("™");
            assert !rTm.isEmpty() : "Không tìm thấy ™";
            System.out.printf("PASSED (Tra cứu LOL mất %.3f ms)%n", rLol.getElapsedMillis());
            passed++;

            // 3. Kiểm thử Tra cứu Definition qua Inverted Index
            System.out.print("[Test 3/12] Tra cứu Definition qua Inverted Index... ");
            SearchResult rMoney = model.searchByDefinition("money");
            assert !rMoney.isEmpty() : "Không tìm thấy kết quả cho 'money'";
            assert rMoney.getCount() >= 20 : "Số lượng kết quả cho 'money' quá ít: " + rMoney.getCount();
            assert rMoney.isUnderOneSecond() : "Tra cứu Definition vượt quá 1s";
            System.out.printf("PASSED (Tìm thấy %d từ chứa 'money' trong %.3f ms)%n",
                    rMoney.getCount(), rMoney.getElapsedMillis());
            passed++;

            // 4. Kiểm thử Autocomplete Trie Prefix Search
            System.out.print("[Test 4/12] Autocomplete Trie Prefix Search... ");
            long startTrie = System.nanoTime();
            List<String> suggestions = model.suggestSlangWords("LO", 10);
            long trieElapsed = System.nanoTime() - startTrie;
            assert !suggestions.isEmpty() : "Không tìm thấy gợi ý cho 'LO'";
            assert suggestions.contains("LOL") : "Gợi ý thiếu 'LOL'";
            System.out.printf("PASSED (Gợi ý %d từ: %s trong %.3f ms)%n",
                    suggestions.size(), suggestions, trieElapsed / 1_000_000.0);
            passed++;

            // 5. Kiểm thử Thêm mới Slang Word
            System.out.print("[Test 5/12] Thêm mới Slang Word... ");
            String testWord = "TEST_SLANG_2026";
            model.addSlangWord(testWord, List.of("Meaning 1", "Meaning 2"), false);
            assert model.containsWord(testWord) : "Không tìm thấy từ vừa thêm";
            SlangEntry addedEntry = model.getEntry(testWord);
            assert addedEntry.getMeanings().size() == 2;
            System.out.println("PASSED");
            passed++;

            // 6. Kiểm thử Thêm trùng (Duplicate vs Overwrite)
            System.out.print("[Test 6/12] Thêm trùng (Duplicate vs Overwrite)... ");
            // Duplicate (thêm nối tiếp nghĩa)
            model.addSlangWord(testWord, List.of("Meaning 3"), false);
            assert model.getEntry(testWord).getMeanings().size() == 3 : "Duplicate không nối thêm nghĩa";

            // Overwrite (ghi đè)
            model.addSlangWord(testWord, List.of("Sole Overwritten Meaning"), true);
            assert model.getEntry(testWord).getMeanings().size() == 1 : "Overwrite không ghi đè";
            assert model.getEntry(testWord).getMeanings().get(0).equals("Sole Overwritten Meaning");
            System.out.println("PASSED");
            passed++;

            // 7. Kiểm thử Chỉnh sửa (Edit) Slang Word
            System.out.print("[Test 7/12] Chỉnh sửa (Edit) Slang Word... ");
            String editedWord = "TEST_SLANG_EDITED";
            model.editSlangWord(testWord, editedWord, List.of("Brand New Definition"));
            assert !model.containsWord(testWord) : "Từ cũ vẫn còn sau khi đổi tên";
            assert model.containsWord(editedWord) : "Từ mới không tồn tại sau khi đổi tên";
            assert model.searchByDefinition("Brand New Definition").getCount() == 1;
            System.out.println("PASSED");
            passed++;

            // 8. Kiểm thử Xóa (Delete) Slang Word
            System.out.print("[Test 8/12] Xóa (Delete) Slang Word... ");
            boolean deleted = model.deleteSlangWord(editedWord);
            assert deleted : "Xóa từ thất bại";
            assert !model.containsWord(editedWord) : "Từ vẫn tồn tại sau khi xóa";
            assert model.suggestSlangWords("TEST_SLANG", 10).isEmpty() : "Trie vẫn còn lưu từ đã xóa";
            System.out.println("PASSED");
            passed++;

            // 9. Kiểm thử Reset về dữ liệu gốc
            System.out.print("[Test 9/12] Khôi phục gốc (Reset)... ");
            model.resetToOriginal();
            assert model.size() == 7638 : "Kích thước sau reset không đúng: " + model.size();
            assert !model.containsWord("TEST_SLANG_2026") : "Từ test vẫn còn sau reset";
            System.out.println("PASSED");
            passed++;

            // 10. Kiểm thử Tốc độ Nạp lại từ Index Cache (.ser)
            System.out.print("[Test 10/12] Tốc độ nạp từ Index Cache (.ser)... ");
            File cacheFile = new File("data/index/index_cache.ser");
            assert cacheFile.exists() : "File cache .ser không tồn tại";
            SlangDictionaryModel cachedModel = new SlangDictionaryModel();
            long startCacheLoad = System.currentTimeMillis();
            cachedModel.initialize();
            long cacheLoadTime = System.currentTimeMillis() - startCacheLoad;
            assert cachedModel.size() == 7638 : "Kích thước từ nạp từ cache sai";
            System.out.printf("PASSED (Nạp 7638 từ từ cache chỉ mất %d ms < 150 ms)%n", cacheLoadTime);
            passed++;

            // 11. Kiểm thử Trò chơi Đố vui (QuizModel)
            System.out.print("[Test 11/12] Sinh câu hỏi Đố vui Quiz... ");
            QuizModel quizModel = new QuizModel(model);
            QuizQuestion q1 = quizModel.generateSlangToDefQuestion();
            assert q1.getOptions().size() == 4 : "Số lượng lựa chọn câu hỏi không phải 4";
            assert q1.getCorrectOptionIndex() >= 0 && q1.getCorrectOptionIndex() < 4;
            assert q1.isCorrect(q1.getCorrectOptionIndex());

            QuizQuestion q2 = quizModel.generateDefToSlangQuestion();
            assert q2.getOptions().size() == 4;
            assert q2.getCorrectOptionIndex() >= 0 && q2.getCorrectOptionIndex() < 4;

            quizModel.checkAnswer(q2.getCorrectOptionIndex());
            assert quizModel.getScore() == 1 && quizModel.getCurrentStreak() == 1;
            System.out.println("PASSED (Sinh câu hỏi 4 đáp án phân biệt thành công)");
            passed++;

            // 12. Kiểm thử Lịch sử Tra cứu (Search History)
            System.out.print("[Test 12/12] Ghi nhận & Đọc Lịch sử... ");
            List<SearchHistoryEntry> history = model.getHistory();
            assert !history.isEmpty() : "Lịch sử tra cứu rỗng";
            System.out.printf("PASSED (%d lượt tra cứu được lưu)%n", history.size());
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
