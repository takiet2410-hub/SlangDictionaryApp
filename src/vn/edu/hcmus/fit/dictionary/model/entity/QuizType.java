package vn.edu.hcmus.fit.dictionary.model.entity;

/**
 * Loại câu hỏi đố vui Quiz.
 */
public enum QuizType {
    SLANG_TO_DEFINITION("Đoán Definition từ Slang Word"),
    DEFINITION_TO_SLANG("Đoán Slang Word từ Definition");

    private final String description;

    QuizType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
