package anhkien.myproject.vietnameseenglishdictionary.model;

import java.util.List;

public class Meaning {
    public String partOfSpeech;
    public List<Definition> definitions;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }
}
