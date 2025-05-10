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
import anhkien.myproject.vietnameseenglishdictionary.model.WordResponse;
import retrofit2.Call;

public class FragmentMenu extends Fragment {
    private String currentTab = "home";
    private String searchKeyword = "";
    private boolean isEngLishToVietnamese = true;
    private TextToSpeech tts;
    private DictionaryRepository dictionaryRepository;
    private TextView resultText;



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
        resultText = view.findViewById(R.id.txtResult);
        dictionaryRepository = new DictionaryRepository();
        resultText.setText(currentTab.equals("home")? "Hiển thị kết quả tìm kiếm cho: " + searchKeyword : "Danh sách từ yêu thích");

        if (currentTab.equals("home") && !searchKeyword.isEmpty()) {
            dictionaryRepository.searchWord(searchKeyword, new DictionaryRepository.DictionaryCallback() {
                @Override
                public void onSuccess(WordResponse wordResponse) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Từ: ").append(wordResponse.getWord()).append("\n\n");
                    if (!wordResponse.getMeanings().isEmpty()) {
                        builder.append("Loại từ: ")
                                .append(wordResponse.getMeanings().get(0).getPartOfSpeech()).append("\n");

                        if (!wordResponse.getMeanings().get(0).getDefinitions().isEmpty()) {
                            builder.append("Nghĩa: ")
                                    .append(wordResponse.getMeanings().get(0).getDefinitions().get(0).getDefinition())
                                    .append("\n");

                            String example = wordResponse.getMeanings().get(0).getDefinitions().get(0).getExample();
                            if (example != null) {
                                builder.append("Ví dụ: ").append(example);
                            }
                        }
                    }
                    resultText.setText(builder.toString());
                }

                @Override
                public void onFailure(String message) {
                    resultText.setText("Lỗi: " + message);
                }
            });
        }
        return view;
    }
}