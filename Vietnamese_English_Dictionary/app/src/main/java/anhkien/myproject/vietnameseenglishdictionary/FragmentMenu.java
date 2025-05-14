package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        TextView txtWord = view.findViewById(R.id.txtWord);
        TextView txtPhonetic = view.findViewById(R.id.txtPhonetic);
        TextView txtType = view.findViewById(R.id.txtType);
        TextView txtMeaning = view.findViewById(R.id.txtMeaning);
        TextView txtExample = view.findViewById(R.id.txtExample);
        TextView txtError = view.findViewById(R.id.txtError);

        resultText = view.findViewById(R.id.txtResult);
        ListView listFavorite = view.findViewById(R.id.listFavorite);
        View layoutWordDetails = view.findViewById(R.id.layoutWordDetails);

        dictionaryRepository = new DictionaryRepository();
        favoriteRepository = new FavoriteRepository(getContext());

        if (currentTab.equals("home")) {
            resultText.setText("Hiển thị kết quả tìm kiếm cho: " + searchKeyword);
        } else {
            resultText.setText("Danh sách từ yêu thích");
        }


        if (currentTab.equals("home") && !searchKeyword.isEmpty()) {
            layoutWordDetails.setVisibility(View.VISIBLE);
            listFavorite.setVisibility(View.GONE);
            dictionaryRepository.searchWord(searchKeyword, new DictionaryRepository.DictionaryCallback() {
                @Override
                public void onSuccess(WordResponse wordResponse) {
                    if (wordResponse.getMeanings() == null ||
                            wordResponse.getMeanings().isEmpty() ||
                            wordResponse.getMeanings().get(0).getDefinitions() == null ||
                            wordResponse.getMeanings().get(0).getDefinitions().isEmpty()) {

                        resultText.setText("Không tìm thấy nghĩa của từ.");
                        layoutWordDetails.setVisibility(View.GONE);
                        return;
                    }

                    String word = wordResponse.getWord();
                    String example = wordResponse.getMeanings().get(0).getDefinitions().get(0).getExample();

                    txtWord.setText("Từ: " + word);
                    txtPhonetic.setText("Phát âm: " + wordResponse.getPhonetic());
                    txtType.setText("Loại từ: " + wordResponse.getMeanings().get(0).getPartOfSpeech());
                    txtMeaning.setText("Nghĩa: " + wordResponse.getMeanings().get(0).getDefinitions().get(0).getDefinition());
                    txtExample.setText(example != null ? "Ví dụ: " + example : "Ví dụ: (không có)");

                    layoutWordDetails.setVisibility(View.VISIBLE);


                    //Favorite button xử lý
                    ImageButton buttonFavorite = view.findViewById(R.id.btnFavorite);
                    buttonFavorite.setVisibility(View.VISIBLE);
                    buttonFavorite.setImageResource(favoriteRepository.isFavorite(word)? android.R.drawable.btn_star_big_on
                            : android.R.drawable.btn_star);
                    buttonFavorite.setOnClickListener(v -> {
                        if (favoriteRepository.isFavorite(word)){
                            favoriteRepository.removeFavorite(word);
                            buttonFavorite.setImageResource(android.R.drawable.btn_star);
                        } else {
                            favoriteRepository.addFavorite(word);
                            buttonFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                        }
                    });

                    //TextToSpeech
                    textToSpeech = new TextToSpeech(getContext(), status -> {
                        if (status == TextToSpeech.SUCCESS){
                            textToSpeech.setLanguage(Locale.UK);
                        }
                    });

                    ImageButton buttonPlayAudio = view.findViewById(R.id.btnPlayAudio);
                    buttonPlayAudio.setVisibility(View.VISIBLE);
                    buttonPlayAudio.setOnClickListener(v -> {
                        String wordToSpeak = txtWord.getText().toString().replace("Từ: ", "").trim();
                        if (!wordToSpeak.isEmpty()){
                            textToSpeech.speak(wordToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    });
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
            layoutWordDetails.setVisibility(View.GONE);
            listFavorite.setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnFavorite).setVisibility(View.GONE);
            view.findViewById(R.id.btnPlayAudio).setVisibility(View.GONE);

            listFavorite.setVisibility(View.VISIBLE);

            List<String> favoriteWords = favoriteRepository.getAllFavorites();
            if (favoriteWords.isEmpty()){
                resultText.setText("Danh sách yêu thích trống.");
                resultText.setVisibility(View.VISIBLE);
            } else {
                resultText.setVisibility(View.GONE);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    R.layout.item_favorite,
                    R.id.txtFavoriteWord,
                    favoriteWords
            );
            listFavorite.setAdapter(adapter);

            listFavorite.setOnItemClickListener((parent, view1, position, id) -> {
                String selectedWord = favoriteWords.get(position);
                dictionaryRepository.searchWord(selectedWord, new DictionaryRepository.DictionaryCallback() {
                    @Override
                    public void onSuccess(WordResponse wordResponse) {
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
        super.onDestroy();
    }

}