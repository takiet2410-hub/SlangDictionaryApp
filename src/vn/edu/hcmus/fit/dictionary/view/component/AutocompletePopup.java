package vn.edu.hcmus.fit.dictionary.view.component;

import vn.edu.hcmus.fit.dictionary.controller.AutocompleteController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Component Autocomplete Popup thông minh gắn với ô tìm kiếm JTextField.
 * Hiển thị danh sách gợi ý mượt mà bằng JPopupMenu, hỗ trợ phím mũi tên LÊN/XUỐNG và ENTER,
 * không chiếm focus bàn phím khi người dùng đang nhập.
 */
public class AutocompletePopup {

    private final JTextField textField;
    private final AutocompleteController controller;
    private final JPopupMenu popupMenu;
    private final JList<String> suggestionList;
    private final DefaultListModel<String> listModel;
    private Consumer<String> onSelectListener;
    private boolean adjusting = false;

    public AutocompletePopup(JTextField textField, AutocompleteController controller) {
        this.textField = textField;
        this.controller = controller;
        this.popupMenu = new JPopupMenu();
        this.listModel = new DefaultListModel<>();
        this.suggestionList = new JList<>(listModel);

        initUI();
        initListeners();
    }

    private void initUI() {
        popupMenu.setFocusable(false);
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFont(textField.getFont().deriveFont(13f));
        suggestionList.setFocusable(false);
        suggestionList.setBackground(Color.WHITE);
        suggestionList.setSelectionBackground(new Color(220, 235, 252));
        suggestionList.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(null);
        scrollPane.setFocusable(false);

        popupMenu.add(scrollPane);
    }

    private void initListeners() {
        // Lắng nghe người dùng gõ phím vào JTextField
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });

        // Điều hướng bằng bàn phím trên ô JTextField
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!popupMenu.isVisible()) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> {
                        int index = suggestionList.getSelectedIndex() + 1;
                        if (index >= listModel.size()) {
                            index = 0;
                        }
                        suggestionList.setSelectedIndex(index);
                        suggestionList.ensureIndexIsVisible(index);
                        e.consume();
                    }
                    case KeyEvent.VK_UP -> {
                        int index = suggestionList.getSelectedIndex() - 1;
                        if (index < 0) {
                            index = listModel.size() - 1;
                        }
                        suggestionList.setSelectedIndex(index);
                        suggestionList.ensureIndexIsVisible(index);
                        e.consume();
                    }
                    case KeyEvent.VK_ENTER -> {
                        if (suggestionList.getSelectedIndex() != -1) {
                            selectCurrentItem();
                            e.consume();
                        }
                    }
                    case KeyEvent.VK_ESCAPE -> hidePopup();
                }
            }
        });

        // Chọn bằng chuột
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    selectCurrentItem();
                }
            }
        });
    }

    private void updateSuggestions() {
        if (adjusting) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                hidePopup();
                return;
            }

            List<String> suggestions = controller.getSuggestions(text);
            if (suggestions.isEmpty()) {
                hidePopup();
                return;
            }

            listModel.clear();
            for (String s : suggestions) {
                listModel.addElement(s);
            }

            int popupWidth = Math.max(textField.getWidth(), 250);
            int rowHeight = 26;
            int popupHeight = Math.min(220, listModel.size() * rowHeight + 10);
            popupMenu.setPreferredSize(new Dimension(popupWidth, popupHeight));

            if (!popupMenu.isVisible() && textField.isShowing()) {
                popupMenu.show(textField, 0, textField.getHeight());
            } else {
                popupMenu.pack();
            }
            textField.requestFocusInWindow();
        });
    }

    private void selectCurrentItem() {
        String selected = suggestionList.getSelectedValue();
        if (selected != null) {
            adjusting = true;
            textField.setText(selected);
            adjusting = false;
            hidePopup();
            if (onSelectListener != null) {
                onSelectListener.accept(selected);
            }
        }
    }

    public void hidePopup() {
        if (popupMenu.isVisible()) {
            popupMenu.setVisible(false);
        }
    }

    public void setOnSelectListener(Consumer<String> onSelectListener) {
        this.onSelectListener = onSelectListener;
    }
}
