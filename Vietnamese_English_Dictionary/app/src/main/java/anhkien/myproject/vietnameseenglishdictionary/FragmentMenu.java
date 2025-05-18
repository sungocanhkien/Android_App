package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import anhkien.myproject.vietnameseenglishdictionary.api.ApiClient;
import anhkien.myproject.vietnameseenglishdictionary.api.DictionaryApi;
import anhkien.myproject.vietnameseenglishdictionary.model.WordResponse;
import retrofit2.Call;
import anhkien.myproject.vietnameseenglishdictionary.database.FavoriteRepository;


public class FragmentMenu extends Fragment {
    private String currentTab = "home";
    private String searchKeyword = "";
    private boolean isEngLishToVietnamese = true;
    private DictionaryRepository dictionaryRepository;
    private TextView resultText;
    private TextToSpeech textToSpeech;
    private FavoriteRepository favoriteRepository;
    private FavoriteAdapter favoriteAdapter;




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
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });

        TextView txtWord = view.findViewById(R.id.txtWord);
        TextView txtPhonetic = view.findViewById(R.id.txtPhonetic);
        TextView txtType = view.findViewById(R.id.txtType);
        TextView txtMeaning = view.findViewById(R.id.txtMeaning);
        TextView txtExample = view.findViewById(R.id.txtExample);
        TextView txtError = view.findViewById(R.id.txtError);
        EditText txtKeyword = getActivity().findViewById(R.id.edtTextSearch);
        String inputKeyword = txtKeyword.getText().toString();

        // Nếu người dùng chưa nhập từ, hiển thị từ mặc định (ví dụ: "hello")
        if (inputKeyword == null || searchKeyword.isEmpty()) {
            searchKeyword = "hello"; // <-- từ mặc định
        }
        txtWord.setText("Từ: " + searchKeyword);

        resultText = view.findViewById(R.id.txtResult);
        RecyclerView listFavorite = view.findViewById(R.id.listFavorite);
        View layoutWordDetails = view.findViewById(R.id.layoutWordDetails);

        dictionaryRepository = new DictionaryRepository();
        favoriteRepository = new FavoriteRepository(getContext());


        if (currentTab.equals("home")) {
            layoutWordDetails.setVisibility(View.VISIBLE);
            listFavorite.setVisibility(View.GONE);
            dictionaryRepository.searchWord(isEngLishToVietnamese, searchKeyword, new DictionaryRepository.DictionaryCallback() {
                @Override
                public void onSuccess(WordResponse wordResponse) {
                    resultText.setText("");
                    txtWord.setText("Từ: " + wordResponse.getWord());

                    if (isEngLishToVietnamese) {
                        txtPhonetic.setText("Phát âm: " + wordResponse.getPhonetic());
                        txtType.setText("Loại từ: " + wordResponse.getMeanings().get(0).getPartOfSpeech());
                        txtMeaning.setText("Nghĩa: " + wordResponse.getMeanings().get(0).getDefinitions().get(0).getDefinition());
                        String example = wordResponse.getMeanings().get(0).getDefinitions().get(0).getExample();
                        txtExample.setText(example != null ? "Ví dụ: " + example : "Ví dụ: (không có)");

                        view.findViewById(R.id.btnFavorite).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.btnPlayAudio).setVisibility(View.VISIBLE);
                    } else {
                        // Việt → Anh (LibreTranslate)
                        txtPhonetic.setText("");
                        txtType.setText("");
                        txtMeaning.setText("Dịch: " + wordResponse.getMeanings().get(0).getDefinitions().get(0).getDefinition());
                        txtExample.setText("");

                        // Ẩn phát âm và favorite vì không phù hợp
                        view.findViewById(R.id.btnFavorite).setVisibility(View.GONE);
                        view.findViewById(R.id.btnPlayAudio).setVisibility(View.GONE);
                    }

                    layoutWordDetails.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(String message) {
                    layoutWordDetails.setVisibility(View.GONE);
                    resultText.setText("Lỗi: " + message);
                    resultText.setVisibility(View.VISIBLE);
                }
            });
        }
        else if (currentTab.equals("favorite")) {
            listFavorite = view.findViewById(R.id.listFavorite);
            resultText.setVisibility(View.GONE);
            layoutWordDetails.setVisibility(View.GONE);
            listFavorite.setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnFavorite).setVisibility(View.GONE);
            view.findViewById(R.id.btnPlayAudio).setVisibility(View.GONE);

            listFavorite.setVisibility(View.VISIBLE);

            List<FavoriteWord> favoriteWords = favoriteRepository.getAllFavorites();
            if (favoriteWords.isEmpty()) {
                resultText.setText("Danh sách yêu thích trống.");
                resultText.setVisibility(View.VISIBLE);
            } else {
                resultText.setVisibility(View.GONE);
            }
            listFavorite.setLayoutManager(new LinearLayoutManager(getContext()));
            FavoriteAdapter adapter = new FavoriteAdapter(getContext(), favoriteWords, textToSpeech);
            listFavorite.setAdapter(adapter);
            favoriteAdapter = adapter;
            adapter.setOnItemClickListener(word -> {
                dictionaryRepository.searchWord(isEngLishToVietnamese, word.getWord(), new DictionaryRepository.DictionaryCallback() {
                    @Override
                    public void onSuccess(WordResponse wordResponse) {
                        layoutWordDetails.setVisibility(View.VISIBLE);


                        txtWord.setText("Từ: " + wordResponse.getWord());
                        txtPhonetic.setText("Phát âm: " + wordResponse.getPhonetic());
                        txtType.setText("Loại từ: " + wordResponse.getMeanings().get(0).getPartOfSpeech());
                        txtMeaning.setText("Nghĩa: " + wordResponse.getMeanings().get(0).getDefinitions().get(0).getDefinition());
                        String example = wordResponse.getMeanings().get(0).getDefinitions().get(0).getExample();
                        txtExample.setText(example != null ? "Ví dụ: " + example : "Ví dụ: (không có)");
                    }

                    @Override
                    public void onFailure(String message) {
                        layoutWordDetails.setVisibility(View.GONE);
                        resultText.setText("Lỗi: " + message);
                    }
                });
            });

        }

        return view;
    }
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        // 🔥 Thêm đoạn này
        if (favoriteAdapter != null) {
            favoriteAdapter.shutdownTextToSpeech();
        }

        super.onDestroy();
    }


}