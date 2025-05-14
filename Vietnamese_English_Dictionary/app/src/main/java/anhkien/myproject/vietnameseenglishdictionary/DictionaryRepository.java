package anhkien.myproject.vietnameseenglishdictionary;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import anhkien.myproject.vietnameseenglishdictionary.api.ApiClient;
import anhkien.myproject.vietnameseenglishdictionary.api.DictionaryApi;
import anhkien.myproject.vietnameseenglishdictionary.model.WordResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DictionaryRepository {
    public interface DictionaryCallback {
        void onSuccess(WordResponse wordResponse);
        void onFailure(String message);
    }

    public void searchWord(String word, DictionaryCallback callback) {
        DictionaryApi api = ApiClient.getRetrofit().create(DictionaryApi.class);
        api.getMeaning(word).enqueue(new Callback<List<WordResponse>>() {
            @Override
            public void onResponse(Call<List<WordResponse>> call, Response<List<WordResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    WordResponse wordResponse = response.body().get(0);

                    if (!wordResponse.getMeanings().isEmpty() &&
                            !wordResponse.getMeanings().get(0).getDefinitions().isEmpty()) {
                        callback.onSuccess(wordResponse);
                    } else {
                        callback.onFailure("Không tìm thấy nghĩa của từ.");
                    }
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
    }
}
