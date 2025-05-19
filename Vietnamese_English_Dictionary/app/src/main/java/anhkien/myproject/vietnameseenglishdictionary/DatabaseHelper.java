package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anhkien.myproject.vietnameseenglishdictionary.api.ApiClient;
import anhkien.myproject.vietnameseenglishdictionary.api.DictionaryApi;
import anhkien.myproject.vietnameseenglishdictionary.api.TranslationApi;
import anhkien.myproject.vietnameseenglishdictionary.model.WordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseHelper {
    public interface DictionaryCallback {
        private static final String TAG = "DatabaseHelper";

        private static String DB_PATH = "";
        public static final String DATABASE_NAME = "mydatabase.db";
        public static final int DATABASE_VERSION = 1; // Hằng số cho chế độ tìm kiếm


        private final Context myContext;

        public static final String TABLE_WORDS = "databaseme";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANSLATION = "translation";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_EXAMPLE = "example";
        public static final String COLUMN_PRONUNCIATION = "pronunciation";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_IS_FAVORITE = "isFavorite";
        private static final String TABLE_CREATE_WORDS_REFERENCE =
                "CREATE TABLE " + TABLE_WORDS + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY, " + 
                        COLUMN_WORD + " TEXT NOT NULL, " +
                        COLUMN_TRANSLATION + " TEXT NOT NULL, " +
                        COLUMN_TYPE + " TEXT, " +
                        COLUMN_EXAMPLE + " TEXT, " +
                        COLUMN_PRONUNCIATION + " TEXT, " +
                        COLUMN_LANGUAGE + " TEXT, " +
                        COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0" +
                        ");";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.myContext = context;

            if (android.os.Build.VERSION.SDK_INT >= 17) {
                DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            } else {
                DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            }
            Log.d(TAG, "DB_PATH: " + DB_PATH);

            File dbDir = new File(DB_PATH);
            if (!dbDir.exists()){
                boolean dirCreated = dbDir.mkdirs();
                Log.d(TAG, "Database directory created: " + dirCreated);
            }

            try {
                createDataBase();
            } catch (IOException e) {
                Log.e(TAG, "Error creating database", e);
                throw new RuntimeException("Error creating database", e);
            }
        }


    public void searchWord(boolean isEnglishToVietnamese, String word, DictionaryCallback callback) {
        if (isEnglishToVietnamese){
        DictionaryApi api = ApiClient.getRetrofit().create(DictionaryApi.class);
        api.getMeaning(word).enqueue(new Callback<List<WordResponse>>() {
            @Override
            public void onResponse(Call<List<WordResponse>> call, Response<List<WordResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onFailure("Không tìm thấy từ phù hợp. Hãy nhập lại một từ khác!");
                }
            }

            @Override
            public void onFailure(Call<List<WordResponse>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage(), t);
                callback.onFailure("Lỗi mạng: " + t.getMessage());
            }
        });
    } else {
            // Bước 1: Dịch từ tiếng Việt sang tiếng Anh bằng LibreTranslate
            TranslationApi translationApi = ApiClient.getTranslationRetrofit().create(TranslationApi.class);


            Map<String, Object> body = new HashMap<>();
            body.put("q", word);
            body.put("source", "vi");
            body.put("target", "en");
            body.put("format", "text");

            translationApi.translate(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().containsKey("translatedText")) {
                        String translated = response.body().get("translatedText");

                        // Gọi lại API dictionary với từ đã dịch
                        DictionaryApi dictionaryApi = ApiClient.getRetrofit().create(DictionaryApi.class);
                        dictionaryApi.getMeaning(translated).enqueue(new Callback<List<WordResponse>>() {
                            @Override
                            public void onResponse(Call<List<WordResponse>> call, Response<List<WordResponse>> response) {
                                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                    callback.onSuccess(response.body().get(0));
                                } else {
                                    callback.onFailure("Không tìm thấy nghĩa cho từ đã dịch: " + translated);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<WordResponse>> call, Throwable t) {
                                callback.onFailure("Lỗi mạng khi tra từ đã dịch: " + t.getMessage());
                            }
                        });
                    } else {
                        callback.onFailure("Không thể dịch từ tiếng Việt. Vui lòng thử lại.");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    callback.onFailure("Lỗi khi gọi API dịch: " + t.getMessage());
                }
            });
        }
    }
}
