package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ImageButton btnToggleFavorite;

    private EditText etSearchWord;
    private ImageButton btnSearch;
    private TextView tvSearchResultContent;
    private Spinner spinnerLanguageMode;
    private String currentSearchMode = DatabaseHelper.EN_VI_MODE;

    private DatabaseHelper dbHelper;
    private Word currentFoundWord = null;

    public HomeFragment() {
        Log.d(TAG, "Constructor HomeFragment called. HashCode: " + System.identityHashCode(this));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Fragment HashCode: " + System.identityHashCode(this));
        try {
            dbHelper = new DatabaseHelper(getActivity());
            Log.d(TAG, "onCreate: DatabaseHelper initialized.");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error initializing DatabaseHelper: ", e);
            Toast.makeText(getActivity(), "Lỗi khởi tạo cơ sở dữ liệu!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called. Fragment HashCode: " + System.identityHashCode(this));
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called. Fragment HashCode: " + System.identityHashCode(this));

        etSearchWord = view.findViewById(R.id.et_search_word);
        btnSearch = view.findViewById(R.id.btn_search);
        tvSearchResultContent = view.findViewById(R.id.tv_search_result_content);
        spinnerLanguageMode = view.findViewById(R.id.spinner_language_mode);
        btnToggleFavorite = view.findViewById(R.id.btn_toggle_favorite);

        setupLanguageModeSpinner();

        btnSearch.setOnClickListener(v -> performSearch());
        btnToggleFavorite.setOnClickListener(v -> toggleFavoriteStatus());
        updateFavoriteButtonVisibility(false);
    }
    private void setupLanguageModeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.language_modes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguageMode.setAdapter(adapter);

        spinnerLanguageMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedModeDisplay = parent.getItemAtPosition(position).toString();
                if (selectedModeDisplay.equals(getString(R.string.mode_en_vi))) {
                    currentSearchMode = DatabaseHelper.EN_VI_MODE;
                } else if (selectedModeDisplay.equals(getString(R.string.mode_vi_en))) {
                    currentSearchMode = DatabaseHelper.VI_EN_MODE;
                }
                Log.d(TAG, "currentSearchMode đã đặt thành: " + currentSearchMode);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        String[] modes = getResources().getStringArray(R.array.language_modes_array);
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(getString(R.string.mode_en_vi))) {
                spinnerLanguageMode.setSelection(i);
                break;
            }
        }
    }

    private void performSearch() {
        String keyword = etSearchWord.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper == null) {
            Toast.makeText(getActivity(), "Lỗi cơ sở dữ liệu", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Đang tìm kiếm: '" + keyword + "' với chế độ: " + currentSearchMode);
        currentFoundWord = dbHelper.searchWord(keyword, currentSearchMode);
        if (currentFoundWord != null) {
            // ... (hiển thị kết quả) ...
            String resultText = "Từ: " + currentFoundWord.getWord() + "\n" + "Nghĩa: " + currentFoundWord.getTranslation() + "\n" +
                    "Loại từ: " + (currentFoundWord.getType() != null ? currentFoundWord.getType() : "N/A") + "\n" +
                    "Ví dụ: " + (currentFoundWord.getExample() != null ? currentFoundWord.getExample() : "N/A") + "\n" +
                    "Phiên âm: " + (currentFoundWord.getPronunciation() != null ? currentFoundWord.getPronunciation() : "N/A") + "\n" +
                    "Ngôn ngữ gốc: " + currentFoundWord.getLanguage() + "\n" +
                    "Yêu thích: " + (currentFoundWord.isFavorite() ? "Có" : "Không");
            tvSearchResultContent.setText(resultText);
            updateFavoriteButtonVisibility(true);
            updateFavoriteButtonIcon();
        } else {
            tvSearchResultContent.setText("Không tìm thấy từ: '" + keyword + "'");
            updateFavoriteButtonVisibility(false);
            currentFoundWord = null;
        }
    }

    private void toggleFavoriteStatus() {
        if (currentFoundWord == null || dbHelper == null) {
            Log.w(TAG, "Không thể thay đổi trạng thái yêu thích: chưa có từ hoặc dbHelper null.");
            return;
        }
        boolean newFavoriteStatus = !currentFoundWord.isFavorite();
        boolean success = dbHelper.setFavoriteStatus(currentFoundWord.getId(), newFavoriteStatus);

        if (success) {
            currentFoundWord.setFavorite(newFavoriteStatus);
            updateFavoriteButtonIcon();
            String message = newFavoriteStatus ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
            //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);  // <-- chỉnh giữa màn hình
            toast.show();
            Log.d(TAG, "Từ ID " + currentFoundWord.getId() + " trạng thái yêu thích mới: " + newFavoriteStatus);

        } else {
            Toast.makeText(getActivity(), "Lỗi khi cập nhật yêu thích!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Lỗi khi cập nhật trạng thái yêu thích cho từ ID: " + currentFoundWord.getId());
        }
    }

    private void updateFavoriteButtonIcon() {
        if (currentFoundWord != null && btnToggleFavorite != null) {
            if (currentFoundWord.isFavorite()) {
                btnToggleFavorite.setImageResource(R.drawable.heart_fill);
            } else {
                btnToggleFavorite.setImageResource(R.drawable.heart);
            }
        }
    }

    private void updateFavoriteButtonVisibility(boolean visible) {

        if (btnToggleFavorite != null) {
            btnToggleFavorite.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}