package vn.edu.hcmus.fit.dictionary.view;

import vn.edu.hcmus.fit.dictionary.controller.DictionaryController;
import vn.edu.hcmus.fit.dictionary.controller.DuplicateAction;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.model.event.DictionaryModelListener;
import vn.edu.hcmus.fit.dictionary.view.component.ModernButton;
import vn.edu.hcmus.fit.dictionary.view.component.SlangTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * Panel quản lý từ điển (CRUD): Thêm mới (Overwrite / Duplicate), Sửa, Xóa có confirm, Reset có confirm.
 * Tự động đồng bộ với Model thông qua Observer Pattern (DictionaryModelListener).
 */
public class ManagementPanel extends JPanel implements DictionaryModelListener {

    private final DictionaryController controller;
    private final SlangTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<SlangTableModel> sorter;
    private final JTextField txtFilter;
    private final JLabel lblCount;

    public ManagementPanel(DictionaryController controller) {
        this.controller = controller;
        this.tableModel = new SlangTableModel(controller.getAllEntries());
        this.sorter = new TableRowSorter<>(tableModel);
        this.table = new JTable(tableModel);
        this.txtFilter = new JTextField();
        this.lblCount = new JLabel();

        initUI();
        controller.getModel().addListener(this);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Header & Action Buttons Bar
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC TỪ LÓNG (CRUD)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(24, 76, 120));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        ModernButton btnAdd = new ModernButton("➕ Thêm từ mới",
                new Color(22, 163, 74), new Color(34, 197, 94), new Color(21, 128, 61),
                Color.WHITE, Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setPreferredSize(new Dimension(140, 36));

        ModernButton btnEdit = new ModernButton("✏️ Chỉnh sửa",
                new Color(2, 132, 199), new Color(14, 165, 233), new Color(3, 105, 161),
                Color.WHITE, Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEdit.setPreferredSize(new Dimension(120, 36));

        ModernButton btnDelete = new ModernButton("🗑️ Xóa",
                new Color(220, 38, 38), new Color(239, 68, 68), new Color(185, 28, 28),
                Color.WHITE, Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setPreferredSize(new Dimension(90, 36));

        ModernButton btnReset = new ModernButton("🔄 Khôi phục gốc (Reset)",
                new Color(153, 27, 27), new Color(185, 28, 28), new Color(127, 29, 29),
                new Color(254, 226, 226), Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReset.setPreferredSize(new Dimension(190, 36));

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(btnReset);

        topPanel.add(toolbar, BorderLayout.CENTER);

        // Filter Bar
        JPanel filterPanel = new JPanel(new BorderLayout(6, 6));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        JLabel lblSearch = new JLabel("Lọc nhanh từ khóa: ");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        filterPanel.add(lblSearch, BorderLayout.WEST);
        filterPanel.add(txtFilter, BorderLayout.CENTER);

        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCount.setForeground(new Color(90, 90, 90));
        updateCountLabel();
        filterPanel.add(lblCount, BorderLayout.EAST);

        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 2. Table
        table.setRowSorter(sorter);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setMaxWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(550);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Events
        txtFilter.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> handleDelete());
        btnReset.addActionListener(e -> handleReset());
    }

    private void filterTable() {
        String text = txtFilter.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
        updateCountLabel();
    }

    private void updateCountLabel() {
        int visible = (sorter.getRowFilter() == null) ? tableModel.getRowCount() : table.getRowCount();
        lblCount.setText(String.format("Tổng số: %d / %d từ", visible, controller.getTotalWordsCount()));
    }

    // ==================== CÁC HỘP THOẠI XỬ LÝ (DIALOGS) ====================

    private void showAddDialog() {
        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setPreferredSize(new Dimension(420, 150));

        JTextField txtWord = new JTextField();
        JTextArea txtMeanings = new JTextArea(4, 20);
        txtMeanings.setLineWrap(true);
        txtMeanings.setWrapStyleWord(true);

        JPanel pnlFields = new JPanel(new GridLayout(2, 1, 4, 4));
        JPanel pnlWord = new JPanel(new BorderLayout(4, 4));
        pnlWord.add(new JLabel("Slang Word:"), BorderLayout.NORTH);
        pnlWord.add(txtWord, BorderLayout.CENTER);

        JPanel pnlMeanings = new JPanel(new BorderLayout(4, 4));
        pnlMeanings.add(new JLabel("Định nghĩa (nhiều nghĩa ngăn cách bằng |):"), BorderLayout.NORTH);
        pnlMeanings.add(new JScrollPane(txtMeanings), BorderLayout.CENTER);

        form.add(pnlWord, BorderLayout.NORTH);
        form.add(pnlMeanings, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, form, "Thêm Slang Word mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String word = txtWord.getText().trim();
            String meanings = txtMeanings.getText().trim();

            try {
                String responseMsg = controller.addSlang(word, meanings, (existingWord, existingEntry) -> {
                    String[] options = {"Ghi đè (Overwrite)", "Nối thêm nghĩa (Duplicate)", "Hủy bỏ"};
                    String prompt = String.format(
                            "Từ lóng \"%s\" ĐÃ TỒN TẠI trong từ điển!\n\nNghĩa hiện tại:\n- %s\n\nNghĩa bạn muốn thêm:\n- %s\n\nBạn muốn làm gì?",
                            existingWord, existingEntry.getMeaningsFormatted(), meanings);

                    int choice = JOptionPane.showOptionDialog(
                            ManagementPanel.this,
                            prompt,
                            "Trùng từ lóng (Duplicate Warning)",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[1]
                    );

                    return switch (choice) {
                        case 0 -> DuplicateAction.OVERWRITE;
                        case 1 -> DuplicateAction.DUPLICATE;
                        default -> DuplicateAction.CANCEL;
                    };
                });

                JOptionPane.showMessageDialog(this, responseMsg, "Kết quả thêm từ", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng trên bảng để chỉnh sửa.", "Chưa chọn từ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        SlangEntry entry = tableModel.getEntryAt(modelRow);
        if (entry == null) return;

        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setPreferredSize(new Dimension(420, 150));

        JTextField txtWord = new JTextField(entry.getWord());
        JTextArea txtMeanings = new JTextArea(entry.getMeaningsFormatted(), 4, 20);
        txtMeanings.setLineWrap(true);
        txtMeanings.setWrapStyleWord(true);

        JPanel pnlWord = new JPanel(new BorderLayout(4, 4));
        pnlWord.add(new JLabel("Slang Word:"), BorderLayout.NORTH);
        pnlWord.add(txtWord, BorderLayout.CENTER);

        JPanel pnlMeanings = new JPanel(new BorderLayout(4, 4));
        pnlMeanings.add(new JLabel("Định nghĩa (ngăn cách bằng |):"), BorderLayout.NORTH);
        pnlMeanings.add(new JScrollPane(txtMeanings), BorderLayout.CENTER);

        form.add(pnlWord, BorderLayout.NORTH);
        form.add(pnlMeanings, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, form, "Chỉnh sửa Slang Word: " + entry.getWord(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String responseMsg = controller.editSlang(entry.getWord(), txtWord.getText().trim(), txtMeanings.getText().trim());
                JOptionPane.showMessageDialog(this, responseMsg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi chỉnh sửa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một từ trên bảng để xóa.", "Chưa chọn từ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        SlangEntry entry = tableModel.getEntryAt(modelRow);
        if (entry == null) return;

        try {
            boolean deleted = controller.deleteSlang(entry.getWord(), (message, title) -> {
                int choice = JOptionPane.showConfirmDialog(ManagementPanel.this, message, title,
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                return choice == JOptionPane.YES_OPTION;
            });

            if (deleted) {
                JOptionPane.showMessageDialog(this, String.format("Đã xóa từ \"%s\" thành công.", entry.getWord()),
                        "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi khi xóa", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReset() {
        try {
            boolean reset = controller.resetDictionary((message, title) -> {
                int choice = JOptionPane.showConfirmDialog(ManagementPanel.this, message, title,
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                return choice == JOptionPane.YES_OPTION;
            });

            if (reset) {
                JOptionPane.showMessageDialog(this, "Đã khôi phục toàn bộ từ điển về dữ liệu gốc ban đầu thành công!",
                        "Reset thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi khi Reset", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== OBSERVER LISTENER ====================

    @Override
    public void onDictionaryChanged() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setEntries(controller.getAllEntries());
            updateCountLabel();
        });
    }
}
