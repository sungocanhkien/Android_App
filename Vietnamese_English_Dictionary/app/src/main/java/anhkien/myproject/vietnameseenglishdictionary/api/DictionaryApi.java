package anhkien.myproject.vietnameseenglishdictionary.api;

import java.util.List;

import anhkien.myproject.vietnameseenglishdictionary.model.WordResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;

    public interface DictionaryApi {
        @GET("{word}")
        Call<List<WordResponse>> getMeaning(@Path("word") String word);
    }

