package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class FragmentMenu extends Fragment {
    private String currentTab = "home";
    private String searchKeyword = "";
    private boolean isEngLishToVietnamese = true;
    private TextToSpeech tts;

    public FragmentMenu(String tab) {
        this.currentTab = tab;
    }

    public void setSearchKeyword(String searchKeyword, boolean isEngLishToVietnamese) {
        this.searchKeyword = searchKeyword;
        this.isEngLishToVietnamese = isEngLishToVietnamese;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        TextView resultText = view.findViewById(R.id.txtResult);
        resultText.setText(currentTab.equals("home")? "Hiển thị kết quả tìm kiếm cho: " + searchKeyword : "Danh sách từ yêu thích");

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