package anhkien.myproject.vietnameseenglishdictionary.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TranslationApi {
    @Headers("Content-Type: application/json")
    @POST("/translate")
    Call<Map<String, String>> translate(@Body Map<String, Object> body);
}
