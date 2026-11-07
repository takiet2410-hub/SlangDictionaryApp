package vn.edu.hcmus.fit.dictionary.model.index;

import java.io.Serial;
import java.io.Serializable;

/**
 * Gói đóng gói dữ liệu Index Cache khi serialize ra file hoặc deserialize vào RAM.
 */
public class IndexCacheData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final SlangIndex slangIndex;
    private final InvertedDefinitionIndex definitionIndex;
    private final AutocompleteTrie autocompleteTrie;
    private final IndexMetadata metadata;

    public IndexCacheData(SlangIndex slangIndex, InvertedDefinitionIndex definitionIndex,
                          AutocompleteTrie autocompleteTrie, IndexMetadata metadata) {
        this.slangIndex = slangIndex;
        this.definitionIndex = definitionIndex;
        this.autocompleteTrie = autocompleteTrie;
        this.metadata = metadata;
    }

    public SlangIndex getSlangIndex() {
        return slangIndex;
    }

    public InvertedDefinitionIndex getDefinitionIndex() {
        return definitionIndex;
    }

    public AutocompleteTrie getAutocompleteTrie() {
        return autocompleteTrie;
    }

    public IndexMetadata getMetadata() {
        return metadata;
    }
}
