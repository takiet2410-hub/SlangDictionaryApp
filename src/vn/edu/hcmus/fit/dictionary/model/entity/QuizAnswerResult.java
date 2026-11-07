package vn.edu.hcmus.fit.dictionary.model.entity;

/**
 * Kết quả trả về sau khi người dùng nộp câu trả lời cho một câu hỏi Quiz.
 */
public record QuizAnswerResult(
        boolean isCorrect,
        int correctOptionIndex,
        String correctAnswer,
        int currentScore,
        int totalAnswered,
        int currentStreak,
        int bestStreak,
        double accuracyPercentage
) {
    public String getFeedbackMessage() {
        if (isCorrect) {
            return String.format("CHÍNH XÁC! (+1 điểm) | Chuỗi thắng: %d", currentStreak);
        } else {
            return String.format("CHƯA CHÍNH XÁC! Đáp án đúng: \"%s\"", correctAnswer);
        }
    }
}
