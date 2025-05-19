package anhkien.myproject.vietnameseenglishdictionary;

public class Word {
    private int id;
    private String word;          // Từ gốc (tiếng Anh hoặc Việt, dựa vào cột 'language')
    private String translation;   // Nghĩa dịch tương ứng
    private String type;          // Loại từ (noun, verb, adj,...)
    private String example;       // Câu ví dụ
    private String pronunciation; // Phiên âm
    private String language;      // Ngôn ngữ của cột 'word' ('en' hoặc 'vi')
    private boolean isFavorite;   // Trạng thái yêu thích

    // Constructor
    public Word(int id, String word, String translation, String type, String example, String pronunciation, String language, boolean isFavorite) {
        this.id = id;
        this.word = word;
        this.translation = translation;
        this.type = type;
        this.example = example;
        this.pronunciation = pronunciation;
        this.language = language;
        this.isFavorite = isFavorite;
    }

    // Getters
    public int getId() { return id; }
    public String getWord() { return word; }
    public String getTranslation() { return translation; }
    public String getType() { return type; }
    public String getExample() { return example; }
    public String getPronunciation() { return pronunciation; }
    public String getLanguage() { return language; } // 'en' hoặc 'vi'
    public boolean isFavorite() { return isFavorite; }

    // Setter (có thể cần cho isFavorite)
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // Phương thức tiện ích để lấy từ tiếng Việt (dựa vào cột 'language')
    public String getVietnameseTerm() {
        if ("vi".equalsIgnoreCase(this.language)) {
            return this.word;
        } else if ("en".equalsIgnoreCase(this.language)) {
            return this.translation;
        }
        return null; // Trường hợp dữ liệu không nhất quán
    }

    // Phương thức tiện ích để lấy từ tiếng Anh (dựa vào cột 'language')
    public String getEnglishTerm() {
        if ("en".equalsIgnoreCase(this.language)) {
            return this.word;
        } else if ("vi".equalsIgnoreCase(this.language)) {
            return this.translation;
        }
        return null; // Trường hợp dữ liệu không nhất quán
    }

    // toString() để dễ debug (tùy chọn)
    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", translation='" + translation + '\'' +
                ", language='" + language + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
