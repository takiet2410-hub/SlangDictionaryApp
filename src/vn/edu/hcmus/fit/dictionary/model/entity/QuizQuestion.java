package vn.edu.hcmus.fit.dictionary.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Thực thể biểu diễn một câu hỏi đố vui trắc nghiệm 4 lựa chọn.
 */
public class QuizQuestion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final QuizType quizType;
    private final String questionText;
    private final List<String> options;
    private final int correctOptionIndex;
    private final String correctWord;
    private final String correctMeaning;

    public QuizQuestion(QuizType quizType, String questionText, List<String> options,
                        int correctOptionIndex, String correctWord, String correctMeaning) {
        this.quizType = quizType;
        this.questionText = questionText;
        this.options = (options != null) ? List.copyOf(options) : Collections.emptyList();
        this.correctOptionIndex = correctOptionIndex;
        this.correctWord = correctWord;
        this.correctMeaning = correctMeaning;
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getCorrectAnswer() {
        if (correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
            return options.get(correctOptionIndex);
        }
        return "";
    }

    public String getCorrectWord() {
        return correctWord;
    }

    public String getCorrectMeaning() {
        return correctMeaning;
    }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctOptionIndex;
    }

    @Override
    public String toString() {
        return "QuizQuestion{" +
                "type=" + quizType +
                ", question='" + questionText + '\'' +
                ", options=" + options +
                ", correctIndex=" + correctOptionIndex +
                '}';
    }
}
