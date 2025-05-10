package anhkien.myproject.vietnameseenglishdictionary.model;

import java.util.List;

public class WordResponse {
    public String word;
    public String phonetic;
    public List<Meaning> meanings;

    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }
}
