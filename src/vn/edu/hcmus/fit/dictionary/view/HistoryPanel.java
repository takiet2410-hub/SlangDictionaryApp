package vn.edu.hcmus.fit.dictionary.view;

import vn.edu.hcmus.fit.dictionary.controller.DictionaryController;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchHistoryEntry;
import vn.edu.hcmus.fit.dictionary.model.entity.SearchType;
import vn.edu.hcmus.fit.dictionary.model.event.DictionaryModelListener;
import vn.edu.hcmus.fit.dictionary.view.component.ModernButton;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Panel hiển thị và quản lý lịch sử tra cứu của người dùng.
 * Tự động cập nhật khi có tra cứu mới thông qua DictionaryModelListener.
 */
public class HistoryPanel extends JPanel implements DictionaryModelListener {

    private final DictionaryController controller;
    private final HistoryTableModel tableModel;
    private final JTable table;
    private final JLabel lblCount;
    private BiConsumer<String, Boolean> onResearchRequested;

    public HistoryPanel(DictionaryController controller) {
        this.controller = controller;
        this.tableModel = new HistoryTableModel(controller.getHistory());
        this.table = new JTable(tableModel);
        this.lblCount = new JLabel();

        initUI();
        controller.getModel().addListener(this);
    }

    public void setOnResearchRequested(BiConsumer<String, Boolean> onResearchRequested) {
        this.onResearchRequested = onResearchRequested;
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Header
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));

        JLabel lblTitle = new JLabel("LỊCH SỬ TRA CỨU SLANG DICTIONARY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(24, 76, 120));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        ModernButton btnResearch = new ModernButton("🔍 Tra cứu lại",
                new Color(37, 99, 235), new Color(59, 130, 246), new Color(29, 78, 216),
                Color.WHITE, Color.WHITE);
        btnResearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnResearch.setPreferredSize(new Dimension(130, 36));

        ModernButton btnClear = new ModernButton("🗑️ Xóa toàn bộ lịch sử",
                new Color(220, 38, 38), new Color(239, 68, 68), new Color(185, 28, 28),
                Color.WHITE, Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClear.setPreferredSize(new Dimension(170, 36));

        ModernButton btnRefresh = new ModernButton("🔄 Làm mới",
                new Color(100, 116, 139), new Color(148, 163, 184), new Color(71, 85, 105),
                Color.WHITE, Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setPreferredSize(new Dimension(110, 36));

        toolbar.add(btnResearch);
        toolbar.add(btnClear);
        toolbar.add(btnRefresh);

        topPanel.add(toolbar, BorderLayout.CENTER);

        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCount.setForeground(new Color(90, 90, 90));
        updateCountLabel();
        topPanel.add(lblCount, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 2. Table
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setMaxWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setMaxWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setMaxWidth(110);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Events
        btnResearch.addActionListener(e -> handleResearch());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleResearch();
                }
            }
        });

        btnClear.addActionListener(e -> {
            boolean cleared = controller.clearHistory((msg, title) -> {
                int choice = JOptionPane.showConfirmDialog(HistoryPanel.this, msg, title,
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                return choice == JOptionPane.YES_OPTION;
            });
            if (cleared) {
                JOptionPane.showMessageDialog(this, "Đã xóa toàn bộ lịch sử tra cứu thành công.", "Đã xóa", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRefresh.addActionListener(e -> onHistoryUpdated());
    }

    private void handleResearch() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng lịch sử để tra cứu lại.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SearchHistoryEntry entry = tableModel.getEntryAt(row);
        if (entry != null && onResearchRequested != null) {
            boolean isSlang = entry.getSearchType() == SearchType.SLANG_WORD;
            onResearchRequested.accept(entry.getKeyword(), isSlang);
        }
    }

    private void updateCountLabel() {
        lblCount.setText(String.format("Tổng số lượt tra cứu đã lưu: %d", tableModel.getRowCount()));
    }

    @Override
    public void onHistoryUpdated() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setEntries(controller.getHistory());
            updateCountLabel();
        });
    }

    // ==================== TABLE MODEL CHO HISTORY ====================

    private static class HistoryTableModel extends AbstractTableModel {
        private final String[] columns = {"STT", "Thời gian", "Loại tra cứu", "Từ khóa", "Kết quả"};
        private final List<SearchHistoryEntry> entries;

        public HistoryTableModel(List<SearchHistoryEntry> initial) {
            this.entries = new ArrayList<>(initial != null ? initial : List.of());
        }

        public void setEntries(List<SearchHistoryEntry> newEntries) {
            this.entries.clear();
            if (newEntries != null) {
                this.entries.addAll(newEntries);
            }
            fireTableDataChanged();
        }

        public SearchHistoryEntry getEntryAt(int row) {
            if (row >= 0 && row < entries.size()) {
                return entries.get(row);
            }
            return null;
        }

        @Override
        public int getRowCount() { return entries.size(); }
        @Override
        public int getColumnCount() { return columns.length; }
        @Override
        public String getColumnName(int c) { return columns[c]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= entries.size()) return null;
            SearchHistoryEntry entry = entries.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> rowIndex + 1;
                case 1 -> entry.getFormattedTimestamp();
                case 2 -> entry.getSearchType().getDisplayName();
                case 3 -> entry.getKeyword();
                case 4 -> entry.getMatchCount() + " từ";
                default -> null;
            };
        }
    }
}
