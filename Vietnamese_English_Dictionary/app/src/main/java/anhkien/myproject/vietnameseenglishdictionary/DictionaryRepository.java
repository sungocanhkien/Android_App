package anhkien.myproject.vietnameseenglishdictionary;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class DictionaryRepository {
    public interface DictionaryCallback {
        void onSuccess(WordResponse wordResponse);
        void onFailure(String message);
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
