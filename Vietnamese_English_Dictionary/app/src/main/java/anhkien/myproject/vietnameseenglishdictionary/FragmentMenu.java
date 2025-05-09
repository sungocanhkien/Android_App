package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import anhkien.myproject.vietnameseenglishdictionary.api.ApiClient;
import anhkien.myproject.vietnameseenglishdictionary.api.DictionaryApi;
import retrofit2.Call;

public class FragmentMenu extends Fragment {
    private String currentTab = "home";
    private String searchKeyword = "";
    private boolean isEngLishToVietnamese = true;
    private TextToSpeech tts;


    public static FragmentMenu newInstance(String tab, String searchKeyword, boolean isEngLishToVietnamese) {
        FragmentMenu fragmentMenu = new FragmentMenu();
        Bundle args = new Bundle();
        args.putString("tab", tab);
        args.putString("keyword", searchKeyword);
        args.putBoolean("isEnglishToVietnamese",isEngLishToVietnamese);
        fragmentMenu.setArguments(args);
        return fragmentMenu;
    }

    public void setSearchKeyword(String searchKeyword, boolean isEngLishToVietnamese) {
        this.searchKeyword = searchKeyword;
        this.isEngLishToVietnamese = isEngLishToVietnamese;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            currentTab = getArguments().getString("tab", "home");
            searchKeyword = getArguments().getString("keyword", "");
            isEngLishToVietnamese = getArguments().getBoolean("isEnglishToVietnamese", true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        TextView resultText = view.findViewById(R.id.txtResult);
        resultText.setText(currentTab.equals("home")? "Hiển thị kết quả tìm kiếm cho: " + searchKeyword : "Danh sách từ yêu thích");

        if (currentTab.equals("home") && !searchKeyword.isEmpty()) {
            DictionaryApi dictionaryApi = ApiClient.getRetrofit().create(DictionaryApi.class);
            Call<List<DictionaryResponse>> call = dictionaryApi.getMeaning(searchKeyword);

            call.enqueue(new retrofit2.Callback<List<DictionaryResponse>>() {
                @Override
                public void onResponse(Call<List<DictionaryResponse>> call, retrofit2.Response<List<DictionaryResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        StringBuilder resultBuilder = new StringBuilder();
                        for (DictionaryResponse entry : response.body()) {
                            resultBuilder.append("Từ: ").append(entry.getWord()).append("\n\n");
                            for (DictionaryResponse.Meaning meaning : entry.getMeanings()) {
                                resultBuilder.append("* ").append(meaning.getPartOfSpeech()).append("\n");
                                for (DictionaryResponse.Definition def : meaning.getDefinitions()) {
                                    resultBuilder.append("  - ").append(def.getDefinition()).append("\n");
                                }
                                resultBuilder.append("\n");
                            }
                        }
                        resultText.setText(resultBuilder.toString());
                    } else {
                        resultText.setText("Không tìm thấy kết quả.");
                    }
                }

                @Override
                public void onFailure(Call<List<DictionaryResponse>> call, Throwable t) {
                    resultText.setText("Lỗi kết nối: " + t.getMessage());
                }
            });
        }


        //Khởi tạo TextTospeech
        tts = new TextToSpeech(getContext(), status -> {
            if(status == TextToSpeech.SUCCESS){
                tts.setLanguage(Locale.US);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
    }
}