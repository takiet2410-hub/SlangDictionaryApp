package vn.edu.hcmus.fit.dictionary.model;

import vn.edu.hcmus.fit.dictionary.model.entity.QuizQuestion;
import vn.edu.hcmus.fit.dictionary.model.entity.QuizType;
import vn.edu.hcmus.fit.dictionary.model.entity.SlangEntry;
import vn.edu.hcmus.fit.dictionary.util.Constants;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Model quản lý nghiệp vụ và trạng thái trò chơi đố vui (Quiz Game).
 * Sinh ngẫu nhiên câu hỏi không bị trùng lặp đáp án và theo dõi điểm số, chuỗi thắng (streak).
 */
public class QuizModel {

    private final SlangDictionaryModel dictionaryModel;
    private int score;
    private int totalAnswered;
    private int currentStreak;
    private int bestStreak;
    private QuizQuestion currentQuestion;

    public QuizModel(SlangDictionaryModel dictionaryModel) {
        this.dictionaryModel = dictionaryModel;
        this.score = 0;
        this.totalAnswered = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
    }

    /**
     * Sinh câu hỏi Đố vui Chế độ 1: Cho Slang Word -> Đoán Definition (4 lựa chọn).
     */
    public QuizQuestion generateSlangToDefQuestion() {
        if (dictionaryModel.size() < Constants.QUIZ_OPTIONS_COUNT) {
            throw new IllegalStateException("Từ điển cần ít nhất 4 từ để chơi Quiz.");
        }

        SlangEntry target = dictionaryModel.getRandomSlang();
        String correctMeaning = target.getMeaningsFormatted();

        Set<String> optionsSet = new LinkedHashSet<>();
        optionsSet.add(correctMeaning);

        // Lấy 3 đáp án sai (distractors) phân biệt
        while (optionsSet.size() < Constants.QUIZ_OPTIONS_COUNT) {
            SlangEntry distractor = dictionaryModel.getRandomSlang();
            if (!distractor.equals(target)) {
                String distractorMeaning = distractor.getMeaningsFormatted();
                if (!distractorMeaning.isBlank()) {
                    optionsSet.add(distractorMeaning);
                }
            }
        }

        List<String> options = new ArrayList<>(optionsSet);
        Collections.shuffle(options, ThreadLocalRandom.current());
        int correctIndex = options.indexOf(correctMeaning);

        currentQuestion = new QuizQuestion(
                QuizType.SLANG_TO_DEFINITION,
                target.getWord(),
                options,
                correctIndex,
                target.getWord(),
                correctMeaning
        );
        return currentQuestion;
    }

    /**
     * Sinh câu hỏi Đố vui Chế độ 2: Cho Definition -> Đoán Slang Word (4 lựa chọn).
     */
    public QuizQuestion generateDefToSlangQuestion() {
        if (dictionaryModel.size() < Constants.QUIZ_OPTIONS_COUNT) {
            throw new IllegalStateException("Từ điển cần ít nhất 4 từ để chơi Quiz.");
        }

        SlangEntry target = dictionaryModel.getRandomSlang();
        String targetMeaning = target.getMeaningsFormatted();
        String correctWord = target.getWord();

        Set<String> optionsSet = new LinkedHashSet<>();
        optionsSet.add(correctWord);

        // Lấy 3 đáp án sai phân biệt
        while (optionsSet.size() < Constants.QUIZ_OPTIONS_COUNT) {
            SlangEntry distractor = dictionaryModel.getRandomSlang();
            if (!distractor.equals(target)) {
                optionsSet.add(distractor.getWord());
            }
        }

        List<String> options = new ArrayList<>(optionsSet);
        Collections.shuffle(options, ThreadLocalRandom.current());
        int correctIndex = options.indexOf(correctWord);

        currentQuestion = new QuizQuestion(
                QuizType.DEFINITION_TO_SLANG,
                targetMeaning,
                options,
                correctIndex,
                correctWord,
                targetMeaning
        );
        return currentQuestion;
    }

    /**
     * Ghi nhận câu trả lời và tính toán điểm số.
     */
    public boolean checkAnswer(int selectedOptionIndex) {
        if (currentQuestion == null) {
            return false;
        }

        totalAnswered++;
        boolean correct = currentQuestion.isCorrect(selectedOptionIndex);
        if (correct) {
            score++;
            currentStreak++;
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak;
            }
        } else {
            currentStreak = 0;
        }
        return correct;
    }

    public QuizQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public int getScore() {
        return score;
    }

    public int getTotalAnswered() {
        return totalAnswered;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public double getAccuracyPercentage() {
        if (totalAnswered == 0) return 0.0;
        return (double) score / totalAnswered * 100.0;
    }

    public void resetScore() {
        score = 0;
        totalAnswered = 0;
        currentStreak = 0;
        bestStreak = 0;
        currentQuestion = null;
    }
}
