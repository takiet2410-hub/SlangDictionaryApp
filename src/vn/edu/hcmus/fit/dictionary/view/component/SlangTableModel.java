package vn.edu.hcmus.fit.dictionary.view.component;

import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TableModel hiển thị danh sách SlangEntry lên JTable một cách mượt mà và tối ưu bộ nhớ.
 */
public class SlangTableModel extends AbstractTableModel {

    private final String[] columnNames = {"STT", "Slang Word", "Định nghĩa (Meanings)"};
    private final List<SlangEntry> entries;

    public SlangTableModel() {
        this.entries = new ArrayList<>();
    }

    public SlangTableModel(Collection<SlangEntry> initialData) {
        this.entries = new ArrayList<>(initialData != null ? initialData : List.of());
    }

    public void setEntries(Collection<SlangEntry> newEntries) {
        this.entries.clear();
        if (newEntries != null) {
            this.entries.addAll(newEntries);
        }
        fireTableDataChanged();
    }

    public SlangEntry getEntryAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < entries.size()) {
            return entries.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            case 1, 2 -> String.class;
            default -> Object.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= entries.size()) {
            return null;
        }
        SlangEntry entry = entries.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> entry.getWord();
            case 2 -> entry.getMeaningsFormatted();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
