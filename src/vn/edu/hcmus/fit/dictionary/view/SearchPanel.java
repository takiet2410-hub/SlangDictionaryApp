package vn.edu.hcmus.fit.dictionary.view;

import vn.edu.hcmus.fit.dictionary.controller.AutocompleteController;
import vn.edu.hcmus.fit.dictionary.controller.DictionaryController;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchResult;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchType;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.view.component.AutocompletePopup;
import vn.edu.hcmus.fit.dictionary.view.component.SlangTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

/**
 * Panel tìm kiếm hỗ trợ tra cứu theo Slang Word (kèm Autocomplete gợi ý tức thì)
 * và tra cứu theo Definition (Inverted Index). Hiển thị chi tiết thời gian đo lường (< 1s).
 */
public class SearchPanel extends JPanel {

    private final DictionaryController controller;
    private final AutocompleteController autocompleteController;

    private JComboBox<String> cmbSearchType;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnClear;
    private JLabel lblStatus;
    private JTable tblResults;
    private SlangTableModel tableModel;
    private AutocompletePopup autocompletePopup;

    private JTextArea txtDetailMeaning;
    private JLabel lblDetailWord;

    public SearchPanel(DictionaryController controller, AutocompleteController autocompleteController) {
        this.controller = controller;
        this.autocompleteController = autocompleteController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Header & Search Input Bar
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));

        JLabel lblTitle = new JLabel("TRA CỨU TỪ LÓNG (SLANG DICTIONARY)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(24, 76, 120));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel inputBar = new JPanel(new GridBagLayout());
        inputBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        inputBar.setBackground(new Color(248, 250, 253));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;

        // ComboBox chọn kiểu tra cứu
        cmbSearchType = new JComboBox<>(new String[]{"Tìm theo Slang Word", "Tìm theo Definition"});
        cmbSearchType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        inputBar.add(cmbSearchType, gbc);

        // Ô nhập từ khóa
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        inputBar.add(txtSearch, gbc);

        // Nút Tìm kiếm
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(33, 115, 70));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        inputBar.add(btnSearch, gbc);

        // Nút Xóa trắng
        btnClear = new JButton("Xóa");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setFocusPainted(false);
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        inputBar.add(btnClear, gbc);

        topPanel.add(inputBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 2. Results Table & Metrics
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        lblStatus = new JLabel("Nhập từ khóa và nhấn Tìm kiếm để tra cứu...");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(80, 80, 80));
        centerPanel.add(lblStatus, BorderLayout.NORTH);

        tableModel = new SlangTableModel();
        tblResults = new JTable(tableModel);
        tblResults.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblResults.setRowHeight(26);
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblResults.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblResults.getColumnModel().getColumn(0).setMaxWidth(70);
        tblResults.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblResults.getColumnModel().getColumn(1).setMaxWidth(220);
        tblResults.getColumnModel().getColumn(2).setPreferredWidth(500);

        JScrollPane scrollTable = new JScrollPane(tblResults);
        centerPanel.add(scrollTable, BorderLayout.CENTER);

        // 3. Bottom Preview Panel
        JPanel detailPanel = new JPanel(new BorderLayout(5, 5));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết mục từ được chọn"));
        detailPanel.setPreferredSize(new Dimension(100, 110));

        lblDetailWord = new JLabel(" ");
        lblDetailWord.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDetailWord.setForeground(new Color(18, 52, 86));

        txtDetailMeaning = new JTextArea();
        txtDetailMeaning.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDetailMeaning.setEditable(false);
        txtDetailMeaning.setLineWrap(true);
        txtDetailMeaning.setWrapStyleWord(true);
        txtDetailMeaning.setBackground(new Color(245, 247, 250));

        detailPanel.add(lblDetailWord, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(txtDetailMeaning), BorderLayout.CENTER);

        centerPanel.add(detailPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Khởi tạo Autocomplete Popup cho ô tìm kiếm
        initAutocomplete();

        // Đăng ký sự kiện
        initEvents();
    }

    private void initAutocomplete() {
        autocompletePopup = new AutocompletePopup(txtSearch, autocompleteController);
        autocompletePopup.setOnSelectListener(selectedWord -> performSearch());
    }

    private void initEvents() {
        btnSearch.addActionListener(e -> performSearch());
        txtSearch.addActionListener(e -> performSearch());

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            autocompletePopup.hidePopup();
            tableModel.setEntries(Collections.emptyList());
            lblStatus.setText("Đã xóa dữ liệu tìm kiếm.");
            lblDetailWord.setText(" ");
            txtDetailMeaning.setText("");
            txtSearch.requestFocusInWindow();
        });

        // Thay đổi loại tìm kiếm
        cmbSearchType.addActionListener(e -> {
            boolean isSlangSearch = cmbSearchType.getSelectedIndex() == 0;
            if (!isSlangSearch) {
                autocompletePopup.hidePopup();
            }
            lblStatus.setText(isSlangSearch ? "Chế độ tìm theo Slang Word (gợi ý tự động)." : "Chế độ tìm theo Definition.");
        });

        // Chọn hàng trên bảng để xem chi tiết
        tblResults.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblResults.getSelectedRow();
                if (selectedRow != -1) {
                    SlangEntry entry = tableModel.getEntryAt(selectedRow);
                    if (entry != null) {
                        lblDetailWord.setText(entry.getWord());
                        txtDetailMeaning.setText(entry.getMeaningsFormatted());
                    }
                }
            }
        });
    }

    public void performSearch() {
        autocompletePopup.hidePopup();
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean isSlangSearch = cmbSearchType.getSelectedIndex() == 0;
            SearchResult result;

            if (isSlangSearch) {
                result = controller.searchSlang(keyword);
            } else {
                result = controller.searchDefinition(keyword);
            }

            tableModel.setEntries(result.getResults());

            if (result.isEmpty()) {
                lblStatus.setText(String.format("Không tìm thấy kết quả nào cho \"%s\" (Thời gian: %.3f ms)",
                        result.getKeyword(), result.getElapsedMillis()));
                lblDetailWord.setText(" ");
                txtDetailMeaning.setText("");
            } else {
                lblStatus.setText(String.format("Tìm thấy %d kết quả trong %.3f ms (< 1s đạt chuẩn đề bài).",
                        result.getCount(), result.getElapsedMillis()));
                tblResults.setRowSelectionInterval(0, 0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi tìm kiếm", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hỗ trợ điền từ và tìm kiếm từ màn hình khác (ví dụ từ RandomPanel hoặc HistoryPanel).
     */
    public void setSearchTarget(String word, boolean isSlang) {
        cmbSearchType.setSelectedIndex(isSlang ? 0 : 1);
        txtSearch.setText(word);
        performSearch();
    }
}
