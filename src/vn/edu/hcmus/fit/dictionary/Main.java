package vn.edu.hcmus.fit.dictionary;

import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.view.MainFrame;

import javax.swing.*;

/**
 * Điểm khởi chạy chính thức của ứng dụng Slang Dictionary.
 */
public class Main {

    public static void main(String[] args) {
        // Thiết lập Look and Feel của hệ điều hành (Native Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Khởi tạo Model trong RAM
                SlangDictionaryModel model = new SlangDictionaryModel();
                model.initialize();

                // Hiển thị Cửa sổ giao diện chính
                MainFrame frame = new MainFrame(model);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Không thể khởi chạy ứng dụng: " + e.getMessage(),
                        "Lỗi Khởi Động",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
