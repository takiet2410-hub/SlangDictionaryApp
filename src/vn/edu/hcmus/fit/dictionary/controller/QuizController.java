package vn.edu.hcmus.fit.dictionary.controller;

import vn.edu.hcmus.fit.dictionary.model.QuizModel;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizAnswerResult;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizQuestion;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizType;

/**
 * Controller điều phối trò chơi Đố vui (Quiz Game).
 * Quản lý khởi tạo câu hỏi 4 đáp án phân biệt, kiểm tra kết quả và theo dõi thành tích người chơi.
 */
public class QuizController {

    private final QuizModel quizModel;
    private QuizType currentType;

    public QuizController(QuizModel quizModel) {
        if (quizModel == null) {
            throw new IllegalArgumentException("QuizModel không được là null.");
        }
        this.quizModel = quizModel;
        this.currentType = QuizType.SLANG_TO_DEFINITION;
    }

    /**
     * Bắt đầu một câu hỏi mới theo chế độ được chỉ định.
     */
    public QuizQuestion nextQuestion(QuizType type) {
        this.currentType = (type != null) ? type : QuizType.SLANG_TO_DEFINITION;
        if (this.currentType == QuizType.SLANG_TO_DEFINITION) {
            return quizModel.generateSlangToDefQuestion();
        } else {
            return quizModel.generateDefToSlangQuestion();
        }
    }

    /**
     * Nộp câu trả lời cho câu hỏi hiện tại.
     * @param selectedOptionIndex Vị trí đáp án người dùng chọn (0 đến 3)
     */
    public QuizAnswerResult submitAnswer(int selectedOptionIndex) {
        QuizQuestion question = quizModel.getCurrentQuestion();
        if (question == null) {
            throw new IllegalStateException("Chưa có câu hỏi nào được khởi tạo.");
        }

        boolean isCorrect = quizModel.checkAnswer(selectedOptionIndex);

        return new QuizAnswerResult(
                isCorrect,
                question.getCorrectOptionIndex(),
                question.getCorrectAnswer(),
                quizModel.getScore(),
                quizModel.getTotalAnswered(),
                quizModel.getCurrentStreak(),
                quizModel.getBestStreak(),
                quizModel.getAccuracyPercentage()
        );
    }

    /**
     * Đặt lại toàn bộ điểm số và chuỗi thắng của phiên chơi.
     */
    public void resetGame() {
        quizModel.resetScore();
    }

    public QuizQuestion getCurrentQuestion() {
        return quizModel.getCurrentQuestion();
    }

    public QuizType getCurrentType() {
        return currentType;
    }

    public int getScore() {
        return quizModel.getScore();
    }

    public int getTotalAnswered() {
        return quizModel.getTotalAnswered();
    }

    public int getCurrentStreak() {
        return quizModel.getCurrentStreak();
    }

    public int getBestStreak() {
        return quizModel.getBestStreak();
    }

    public double getAccuracyPercentage() {
        return quizModel.getAccuracyPercentage();
    }
}
