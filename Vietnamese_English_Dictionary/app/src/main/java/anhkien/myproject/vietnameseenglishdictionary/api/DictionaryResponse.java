package anhkien.myproject.vietnameseenglishdictionary.api;

import android.provider.ContactsContract;

import java.util.List;

public class DictionaryResponse {
    private String word;
    private List<Phonetic> phonetics;
    private List<Meaning> meanings;

    public String getWord() { return word; }
    public List<Phonetic> getPhonetics() { return phonetics; }
    public List<Meaning> getMeanings() { return meanings; }

    public static class Phonetic {
        private String text;
        private String audio;

        public String getText() { return text; }
        public String getAudio() { return audio; }
    }

    public static class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;

        public String getPartOfSpeech() { return partOfSpeech; }
        public List<Definition> getDefinitions() { return definitions; }
    }

    public static class Definition {
        private String definition;

        public String getDefinition() { return definition; }
    }
}
