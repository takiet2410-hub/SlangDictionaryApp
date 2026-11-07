package test;

import vn.edu.hcmus.fit.dictionary.controller.AutocompleteController;
import vn.edu.hcmus.fit.dictionary.controller.DictionaryController;
import vn.edu.hcmus.fit.dictionary.controller.QuizController;
import vn.edu.hcmus.fit.dictionary.model.QuizModel;
import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.view.*;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Bộ kiểm thử nghiệm thu Milestone 4: Tầng View Java Swing và các Panel chức năng.
 */
public class Milestone4Test {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        System.out.println("============================================================");
        System.out.println("       KIỂM THỬ NGHIỆM THU MILESTONE 4 (TẦNG VIEW SWING)    ");
        System.out.println("============================================================");

        int[] passed = {0};
        int total = 7;

        SwingUtilities.invokeAndWait(() -> {
            try {
                // Khởi tạo Model
                SlangDictionaryModel model = new SlangDictionaryModel();
                model.initialize();

                DictionaryController dictController = new DictionaryController(model);
                QuizController quizController = new QuizController(new QuizModel(model));
                AutocompleteController autoController = new AutocompleteController(model);

                // 1. Kiểm thử SearchPanel
                System.out.print("[Test 1/7] Khởi tạo & Kiểm thử SearchPanel... ");
                SearchPanel searchPanel = new SearchPanel(dictController, autoController);
                searchPanel.setSearchTarget("LOL", true);
                System.out.println("PASSED");
                passed[0]++;

                // 2. Kiểm thử SearchPanel chế độ Definition
                System.out.print("[Test 2/7] SearchPanel tra cứu Definition... ");
                searchPanel.setSearchTarget("money", false);
                System.out.println("PASSED");
                passed[0]++;

                // 3. Kiểm thử ManagementPanel
                System.out.print("[Test 3/7] Khởi tạo & Kiểm thử ManagementPanel... ");
                ManagementPanel managementPanel = new ManagementPanel(dictController);
                assert managementPanel.getComponentCount() > 0;
                System.out.println("PASSED");
                passed[0]++;

                // 4. Kiểm thử HistoryPanel
                System.out.print("[Test 4/7] Khởi tạo & Kiểm thử HistoryPanel... ");
                HistoryPanel historyPanel = new HistoryPanel(dictController);
                assert historyPanel.getComponentCount() > 0;
                System.out.println("PASSED");
                passed[0]++;

                // 5. Kiểm thử RandomPanel ("On this day slang word")
                System.out.print("[Test 5/7] Khởi tạo & Kiểm thử RandomPanel... ");
                RandomPanel randomPanel = new RandomPanel(dictController);
                randomPanel.displayNewRandomSlang();
                System.out.println("PASSED");
                passed[0]++;

                // 6. Kiểm thử QuizPanel
                System.out.print("[Test 6/7] Khởi tạo & Kiểm thử QuizPanel... ");
                QuizPanel quizPanel = new QuizPanel(quizController);
                assert quizPanel.getComponentCount() > 0;
                System.out.println("PASSED");
                passed[0]++;

                // 7. Kiểm thử MainFrame và CardLayout
                System.out.print("[Test 7/7] Khởi tạo MainFrame & Navigation CardLayout... ");
                MainFrame mainFrame = new MainFrame(model);
                assert mainFrame.getTitle().contains("Slang Dictionary");
                assert mainFrame.getWidth() >= 850;
                mainFrame.dispose();
                System.out.println("PASSED");
                passed[0]++;

                System.out.println("============================================================");
                System.out.printf(" KẾT QUẢ: %d/%d TESTS PASSED (100%% HOÀN HẢO)%n", passed[0], total);
                System.out.println("============================================================");

            } catch (Throwable t) {
                System.err.println("\nFAIL TẠI BƯỚC KIỂM THỬ VIEW: " + t.getMessage());
                t.printStackTrace();
                System.exit(1);
            }
        });
    }
}
