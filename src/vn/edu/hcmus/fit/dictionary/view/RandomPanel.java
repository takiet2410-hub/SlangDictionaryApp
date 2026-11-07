package vn.edu.hcmus.fit.dictionary.view;

import vn.edu.hcmus.fit.dictionary.controller.DictionaryController;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel hiển thị chức năng "On this day slang word" (Lấy ngẫu nhiên 1 Slang Word mỗi ngày).
 * Thiết kế giao diện dạng thẻ (Card) hiện đại, bắt mắt, trực quan.
 */
public class RandomPanel extends JPanel {

    private final DictionaryController controller;
    private JLabel lblSlangWord;
    private JTextArea txtMeanings;
    private SlangEntry currentEntry;
    private Consumer<String> onSearchRedirect;

    public RandomPanel(DictionaryController controller) {
        this.controller = controller;
        initUI();
        displayNewRandomSlang();
    }

    public void setOnSearchRedirect(Consumer<String> onSearchRedirect) {
        this.onSearchRedirect = onSearchRedirect;
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setBackground(new Color(245, 247, 250));

        // 1. Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("✨ ON THIS DAY SLANG WORD ✨", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(20, 50, 90));

        JLabel lblSub = new JLabel("Mỗi ngày khám phá một từ lóng thú vị và ý nghĩa sử dụng!", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSub.setForeground(new Color(100, 110, 120));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Card
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(550, 260));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 235), 2, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        // Label hiển thị từ Slang
        lblSlangWord = new JLabel("...", SwingConstants.CENTER);
        lblSlangWord.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblSlangWord.setForeground(new Color(24, 76, 120));
        card.add(lblSlangWord, BorderLayout.NORTH);

        // Vùng hiển thị các nghĩa
        txtMeanings = new JTextArea();
        txtMeanings.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtMeanings.setEditable(false);
        txtMeanings.setLineWrap(true);
        txtMeanings.setWrapStyleWord(true);
        txtMeanings.setOpaque(false);
        txtMeanings.setMargin(new Insets(10, 15, 10, 15));

        JScrollPane scrollMeanings = new JScrollPane(txtMeanings);
        scrollMeanings.setBorder(BorderFactory.createTitledBorder("Ý nghĩa & Định nghĩa"));
        scrollMeanings.setOpaque(false);
        scrollMeanings.getViewport().setOpaque(false);

        card.add(scrollMeanings, BorderLayout.CENTER);

        cardWrapper.add(card);
        add(cardWrapper, BorderLayout.CENTER);

        // 3. Bottom Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setOpaque(false);

        JButton btnNext = new JButton("🎲 Lấy một từ ngẫu nhiên khác");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext.setBackground(new Color(33, 115, 70));
        btnNext.setForeground(Color.WHITE);
        btnNext.setPreferredSize(new Dimension(240, 42));
        btnNext.setFocusPainted(false);
        btnNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnSearchThis = new JButton("🔍 Tra cứu từ này");
        btnSearchThis.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearchThis.setPreferredSize(new Dimension(170, 42));
        btnSearchThis.setFocusPainted(false);
        btnSearchThis.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        bottomPanel.add(btnNext);
        bottomPanel.add(btnSearchThis);
        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        btnNext.addActionListener(e -> displayNewRandomSlang());

        btnSearchThis.addActionListener(e -> {
            if (currentEntry != null && onSearchRedirect != null) {
                onSearchRedirect.accept(currentEntry.getWord());
            }
        });
    }

    public void displayNewRandomSlang() {
        SlangEntry entry = controller.getRandomSlang();
        if (entry != null) {
            this.currentEntry = entry;
            lblSlangWord.setText(entry.getWord());

            StringBuilder sb = new StringBuilder();
            List<String> meanings = entry.getMeanings();
            for (int i = 0; i < meanings.size(); i++) {
                if (meanings.size() > 1) {
                    sb.append(i + 1).append(". ");
                }
                sb.append(meanings.get(i)).append("\n\n");
            }
            txtMeanings.setText(sb.toString().trim());
        }
    }
}
