package vn.edu.hcmus.fit.dictionary.controller;

import vn.edu.hcmus.fit.dictionary.model.SlangDictionaryModel;
import vn.edu.hcmus.fit.dictionary.util.Constants;
import vn.edu.hcmus.fit.dictionary.util.TextNormalizer;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller điều phối tính năng gợi ý từ khóa (Autocomplete).
 * Lắng nghe các thay đổi ký tự nhập từ ô tìm kiếm, truy vấn cây tiền tố AutocompleteTrie
 * và chuyển danh sách gợi ý về cho View hiển thị.
 */
public class AutocompleteController {

    private final SlangDictionaryModel model;
    private int maxSuggestions;

    public AutocompleteController(SlangDictionaryModel model) {
        this(model, Constants.DEFAULT_AUTOCOMPLETE_LIMIT);
    }

    public AutocompleteController(SlangDictionaryModel model, int maxSuggestions) {
        if (model == null) {
            throw new IllegalArgumentException("Model không được là null.");
        }
        this.model = model;
        this.maxSuggestions = Math.max(1, maxSuggestions);
    }

    /**
     * Lấy danh sách các từ gợi ý theo tiền tố (Prefix).
     * @param prefix Chuỗi người dùng đang gõ
     * @return Danh sách tối đa maxSuggestions từ lóng bắt đầu bằng prefix
     */
    public List<String> getSuggestions(String prefix) {
        String cleanPrefix = TextNormalizer.normalizeKeyword(prefix);
        if (cleanPrefix.isEmpty()) {
            return Collections.emptyList();
        }
        return model.suggestSlangWords(cleanPrefix, maxSuggestions);
    }

    /**
     * Bắt sự kiện khi người dùng gõ phím và bắn kết quả về callback cho View.
     */
    public void onTextChanged(String currentText, Consumer<List<String>> suggestionsConsumer) {
        if (suggestionsConsumer == null) {
            return;
        }
        List<String> suggestions = getSuggestions(currentText);
        suggestionsConsumer.accept(suggestions);
    }

    public int getMaxSuggestions() {
        return maxSuggestions;
    }

    public void setMaxSuggestions(int maxSuggestions) {
        this.maxSuggestions = Math.max(1, maxSuggestions);
    }
}
