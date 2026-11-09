package vn.edu.hcmus.fit.dictionary.view;

import vn.edu.hcmus.fit.dictionary.controller.QuizController;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizAnswerResult;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizQuestion;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizType;
import vn.edu.hcmus.fit.dictionary.view.component.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel trò chơi Đố vui (Quiz Game) với 2 chế độ:
 * 1. Hiển thị Slang Word -> Chọn 1 trong 4 Definition.
 * 2. Hiển thị Definition -> Chọn 1 trong 4 Slang Word.
 * Có hiệu ứng đổi màu đáp án trực quan (Xanh: Đúng, Đỏ: Sai), tính điểm và theo dõi chuỗi thắng (streak).
 */
public class QuizPanel extends JPanel {

    private final QuizController controller;

    private JComboBox<String> cmbQuizMode;
    private JLabel lblQuestionText;
    private JLabel lblQuestionTitle;
    private JButton[] btnOptions;
    private JLabel lblFeedback;
    private ModernButton btnNext;
    private ModernButton btnResetGame;

    // Bảng điểm (Scorecard)
    private JLabel lblScore;
    private JLabel lblStreak;
    private JLabel lblBestStreak;
    private JLabel lblAccuracy;

    private boolean answered = false;

    public QuizPanel(QuizController controller) {
        this.controller = controller;
        this.btnOptions = new JButton[4];
        initUI();
        loadNextQuestion();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setBackground(new Color(248, 249, 252));

        // 1. Top Panel: Header & Mode Selector & Scorecard
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JPanel titleAndMode = new JPanel(new BorderLayout(8, 8));
        titleAndMode.setOpaque(false);

        JLabel lblTitle = new JLabel("TRÒ CHƠI ĐỐ VUI TỪ LÓNG (SLANG QUIZ)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(24, 76, 120));
        titleAndMode.add(lblTitle, BorderLayout.WEST);

        cmbQuizMode = new JComboBox<>(new String[]{
                "1. Đoán Definition từ Slang Word",
                "2. Đoán Slang Word từ Definition"
        });
        cmbQuizMode.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleAndMode.add(cmbQuizMode, BorderLayout.EAST);
        topPanel.add(titleAndMode, BorderLayout.NORTH);

        // Scorecard Banner
        JPanel scorecard = new JPanel(new GridLayout(1, 4, 10, 5));
        scorecard.setBackground(Color.WHITE);
        scorecard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235), 1, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        lblScore = new JLabel("Điểm: 0 / 0", SwingConstants.CENTER);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblScore.setForeground(new Color(33, 115, 70));

        lblStreak = new JLabel("Chuỗi hiện tại: 0", SwingConstants.CENTER);
        lblStreak.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStreak.setForeground(new Color(230, 126, 34));

        lblBestStreak = new JLabel("Kỷ lục chuỗi: 0", SwingConstants.CENTER);
        lblBestStreak.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBestStreak.setForeground(new Color(142, 68, 173));

        lblAccuracy = new JLabel("Độ chính xác: 0.0%", SwingConstants.CENTER);
        lblAccuracy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAccuracy.setForeground(new Color(41, 128, 185));

        scorecard.add(lblScore);
        scorecard.add(lblStreak);
        scorecard.add(lblBestStreak);
        scorecard.add(lblAccuracy);

        topPanel.add(scorecard, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Panel: Question Card & 4 Options
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);

        // Khung câu hỏi
        JPanel questionCard = new JPanel(new BorderLayout(8, 8));
        questionCard.setBackground(Color.WHITE);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 235), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        lblQuestionTitle = new JLabel("CÂU HỎI:", SwingConstants.CENTER);
        lblQuestionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQuestionTitle.setForeground(new Color(120, 130, 140));

        lblQuestionText = new JLabel("...", SwingConstants.CENTER);
        lblQuestionText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblQuestionText.setForeground(new Color(18, 52, 86));

        questionCard.add(lblQuestionTitle, BorderLayout.NORTH);
        questionCard.add(lblQuestionText, BorderLayout.CENTER);
        centerPanel.add(questionCard, BorderLayout.NORTH);

        // Lưới 4 đáp án (Grid 2x2)
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 12, 12));
        optionsGrid.setOpaque(false);

        String[] prefixes = {"A. ", "B. ", "C. ", "D. "};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            JButton btn = new JButton(prefixes[i] + "...");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(40, 40, 40));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 210, 225), 2, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));

            btn.addActionListener(e -> handleOptionSelected(index));
            btnOptions[i] = btn;
            optionsGrid.add(btn);
        }

        centerPanel.add(optionsGrid, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Panel: Feedback Banner & Action Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setOpaque(false);

        lblFeedback = new JLabel("Hãy chọn 1 trong 4 đáp án ở trên!", SwingConstants.CENTER);
        lblFeedback.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFeedback.setForeground(new Color(60, 60, 60));
        bottomPanel.add(lblFeedback, BorderLayout.NORTH);

        JPanel btnActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        btnActions.setOpaque(false);

        btnNext = new ModernButton("➡️ Câu hỏi tiếp theo",
                new Color(5, 150, 105), new Color(16, 185, 129), new Color(4, 120, 87),
                Color.WHITE, Color.WHITE);
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNext.setBackground(new Color(33, 115, 70));
        btnNext.setForeground(Color.WHITE);
        btnNext.setPreferredSize(new Dimension(190, 40));
        btnNext.setEnabled(false);
        btnNext.setFocusPainted(false);
        btnNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//        ModernButton btnNext = new ModernButton("🎲 Lấy một từ ngẫu nhiên khác",
//                new Color(5, 150, 105), new Color(16, 185, 129), new Color(4, 120, 87),
//                Color.WHITE, Color.WHITE);
//        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        btnNext.setPreferredSize(new Dimension(250, 42));

        btnResetGame = new ModernButton("🔄 Chơi lại từ đầu",
                new Color(153, 27, 27), new Color(185, 28, 28), new Color(127, 29, 29),
                new Color(254, 226, 226), Color.WHITE);
        btnResetGame.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnResetGame.setPreferredSize(new Dimension(160, 40));
        btnResetGame.setFocusPainted(false);
        btnResetGame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnActions.add(btnNext);
        btnActions.add(btnResetGame);
        bottomPanel.add(btnActions, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        cmbQuizMode.addActionListener(e -> {
            controller.resetGame();
            updateScorecard();
            loadNextQuestion();
        });

        btnNext.addActionListener(e -> loadNextQuestion());

        btnResetGame.addActionListener(e -> {
            controller.resetGame();
            updateScorecard();
            loadNextQuestion();
        });
    }

    private void loadNextQuestion() {
        answered = false;
        btnNext.setEnabled(false);
        lblFeedback.setText("Hãy chọn 1 trong 4 đáp án ở trên!");
        lblFeedback.setForeground(new Color(60, 60, 60));

        QuizType type = (cmbQuizMode.getSelectedIndex() == 0)
                ? QuizType.SLANG_TO_DEFINITION
                : QuizType.DEFINITION_TO_SLANG;

        QuizQuestion question = controller.nextQuestion(type);
        if (question == null) return;

        if (type == QuizType.SLANG_TO_DEFINITION) {
            lblQuestionTitle.setText("Từ lóng sau đây có ý nghĩa là gì?");
            lblQuestionText.setText("\"" + question.getQuestionText() + "\"");
        } else {
            lblQuestionTitle.setText("Định nghĩa sau đây thuộc về từ lóng nào?");
            lblQuestionText.setText("\"" + question.getQuestionText() + "\"");
        }

        String[] prefixes = {"A. ", "B. ", "C. ", "D. "};
        List<String> options = question.getOptions();
        for (int i = 0; i < 4; i++) {
            btnOptions[i].setText(prefixes[i] + options.get(i));
            btnOptions[i].setEnabled(true);
            btnOptions[i].setBackground(Color.WHITE);
            btnOptions[i].setForeground(new Color(40, 40, 40));
            btnOptions[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 210, 225), 2, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
        }
    }

    private void handleOptionSelected(int selectedIndex) {
        if (answered) return;
        answered = true;

        QuizAnswerResult result = controller.submitAnswer(selectedIndex);

        // Vô hiệu hóa các nút
        for (JButton btn : btnOptions) {
            btn.setEnabled(false);
        }

        // Đổi màu đáp án đúng sang XANH LÁ
        int correctIndex = result.correctOptionIndex();
        btnOptions[correctIndex].setBackground(new Color(230, 250, 235));
        btnOptions[correctIndex].setForeground(new Color(20, 100, 40));
        btnOptions[correctIndex].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 3, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // Nếu người dùng chọn sai, đổi màu nút người dùng đã bấm sang ĐỎ
        if (!result.isCorrect()) {
            btnOptions[selectedIndex].setBackground(new Color(253, 237, 236));
            btnOptions[selectedIndex].setForeground(new Color(192, 57, 43));
            btnOptions[selectedIndex].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(231, 76, 60), 3, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            lblFeedback.setText(result.getFeedbackMessage());
            lblFeedback.setForeground(new Color(192, 57, 43));
        } else {
            lblFeedback.setText(result.getFeedbackMessage());
            lblFeedback.setForeground(new Color(33, 115, 70));
        }

        updateScorecard();
        btnNext.setEnabled(true);
        btnNext.requestFocusInWindow();
    }

    private void updateScorecard() {
        lblScore.setText(String.format("Điểm: %d / %d", controller.getScore(), controller.getTotalAnswered()));
        lblStreak.setText(String.format("Chuỗi hiện tại: %d ", controller.getCurrentStreak()));
        lblBestStreak.setText(String.format("Kỷ lục chuỗi: %d ", controller.getBestStreak()));
        lblAccuracy.setText(String.format("Độ chính xác: %.1f%%", controller.getAccuracyPercentage()));
    }
}
