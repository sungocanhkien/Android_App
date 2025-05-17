package anhkien.myproject.vietnameseenglishdictionary;

public class FavoriteWord {
    private String word;
    private String phonetic;
    private String type;
    private String meaning;
    private String example;
    private String audio;

    public FavoriteWord(String word, String phonetic, String type, String meaning, String example, String audio) {
        this.word = word;
        this.phonetic = phonetic;
        this.type = type;
        this.meaning = meaning;
        this.example = example;
        this.audio = audio;
    }

    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getType() {
        return type;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getExample() {
        return example;
    }

    public String getAudio() {
        return audio;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
