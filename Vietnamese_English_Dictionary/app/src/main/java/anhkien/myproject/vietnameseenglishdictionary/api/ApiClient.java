package anhkien.myproject.vietnameseenglishdictionary.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Retrofit translationRetrofit = null;
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    // Dùng cho LibreTranslate
    public static Retrofit getTranslationRetrofit() {
        if (translationRetrofit == null) {
            translationRetrofit = new Retrofit.Builder()
                    .baseUrl("https://libretranslate.de")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return translationRetrofit;
    }
}
