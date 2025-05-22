package anhkien.myproject.vietnameseenglishdictionary;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteAdapter.OnFavoriteRemovedListener {
    private static final String TAG = "FavoriteFragment";

    private RecyclerView rvFavoriteWords;
    private TextView tvNoFavorites;
    private FavoriteAdapter favoriteAdapter;
    private List<Word> favoriteWordsList; // List này sẽ được Adapter tham chiếu trực tiếp
    private DatabaseHelper dbHelper;
    public FavoriteFragment() {
        // Required empty public constructor
        Log.d(TAG, "Constructor FavoriteFragment called. HashCode: " + System.identityHashCode(this));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Fragment created. HashCode: " + System.identityHashCode(this));
        try {
            dbHelper = new DatabaseHelper(getActivity());
            Log.d(TAG, "onCreate: DatabaseHelper initialized.");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error initializing DatabaseHelper: ", e);
            // Cân nhắc việc hiển thị lỗi cho người dùng hoặc xử lý khác
        }
        favoriteWordsList = new ArrayList<>();
        Log.d(TAG, "onCreate: favoriteWordsList initialized. HashCode: " + System.identityHashCode(favoriteWordsList));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called. Fragment HashCode: " + System.identityHashCode(this));
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called. Fragment HashCode: " + System.identityHashCode(this));

        rvFavoriteWords = view.findViewById(R.id.rv_favorite_words);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);

        rvFavoriteWords.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Khởi tạo Adapter và truyền favoriteWordsList (mà Fragment sở hữu)
        favoriteAdapter = new FavoriteAdapter(getActivity(), favoriteWordsList, this);
        Log.d(TAG, "onViewCreated: FavoriteAdapter created. List HashCode passed to Adapter: " + System.identityHashCode(favoriteWordsList));
        rvFavoriteWords.setAdapter(favoriteAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume CALLED. Fragment HashCode: " + System.identityHashCode(this) + ". Attempting to load favorite words.");
        loadFavoriteWords();
    }
    private void loadFavoriteWords() {
        Log.d(TAG, "Bắt đầu loadFavoriteWords()...");
        if (dbHelper == null) {
            Log.e(TAG, "loadFavoriteWords: dbHelper is null. Cannot load favorites.");
            tvNoFavorites.setText("Lỗi cơ sở dữ liệu.");
            tvNoFavorites.setVisibility(View.VISIBLE);
            rvFavoriteWords.setVisibility(View.GONE);
            Log.d(TAG, "Kết thúc loadFavoriteWords() - dbHelper null.");
            return;
        }
        if (favoriteAdapter == null) {
            Log.e(TAG, "loadFavoriteWords: favoriteAdapter is null. Cannot update UI.");
            Log.d(TAG, "Kết thúc loadFavoriteWords() - adapter null.");
            return;
        }

        List<Word> newFavoritesFromDB = dbHelper.getFavoriteWords();
        Log.d(TAG, "loadFavoriteWords: Số lượng từ yêu thích lấy từ DB: " + (newFavoritesFromDB != null ? newFavoritesFromDB.size() : "null"));
        if (newFavoritesFromDB != null && newFavoritesFromDB.size() > 0) {
            Log.d(TAG, "loadFavoriteWords: Từ yêu thích đầu tiên từ DB: " + newFavoritesFromDB.get(0).getWord() + ", isFav: " + newFavoritesFromDB.get(0).isFavorite());
        }

        // Cập nhật trực tiếp vào favoriteWordsList mà adapter đang tham chiếu
        this.favoriteWordsList.clear();
        if (newFavoritesFromDB != null && !newFavoritesFromDB.isEmpty()) {
            this.favoriteWordsList.addAll(newFavoritesFromDB);
        }

        // Cập nhật UI hiển thị/ẩn dựa trên trạng thái cuối cùng của favoriteWordsList
        if (this.favoriteWordsList.isEmpty()) {
            tvNoFavorites.setVisibility(View.VISIBLE);
            rvFavoriteWords.setVisibility(View.GONE);
            tvNoFavorites.setText("     Chưa có từ yêu thích nào.");
        } else {
            tvNoFavorites.setVisibility(View.GONE);
            rvFavoriteWords.setVisibility(View.VISIBLE);
        }

        favoriteAdapter.notifyDataSetChanged(); // Thông báo cho adapter
        Log.d(TAG, "loadFavoriteWords: Adapter notified. getItemCount() từ adapter: " + favoriteAdapter.getItemCount());
        Log.d(TAG, "Kết thúc loadFavoriteWords().");
    }
    @Override
    public void onFavoriteRemoved(Word word, int position) {
        Log.d(TAG, "onFavoriteRemoved: Yêu cầu xóa từ: " + word.getWord() + " (ID: " + word.getId() + ") tại vị trí: " + position);
        if (dbHelper == null || word == null) {
            Log.e(TAG, "onFavoriteRemoved: Không thể xóa yêu thích: dbHelper hoặc word null.");
            return;
        }

        boolean success = dbHelper.setFavoriteStatus(word.getId(), false);

        if (success) {
            Log.d(TAG, "onFavoriteRemoved: Cập nhật DB thành công cho từ ID: " + word.getId());
            if (position >= 0 && position < favoriteWordsList.size()) {
                // Để chắc chắn hơn, kiểm tra ID trước khi xóa
                if (favoriteWordsList.get(position).getId() == word.getId()) {
                    favoriteWordsList.remove(position);
                    favoriteAdapter.notifyItemRemoved(position);
                    // Sau khi xóa, các item từ vị trí 'position' đến cuối list có thể thay đổi index
                    // Nên cần notify cho range đó.
                    if (favoriteWordsList.size() > position) { // Nếu còn item sau vị trí đã xóa
                        favoriteAdapter.notifyItemRangeChanged(position, favoriteWordsList.size() - position);
                    } else if (favoriteWordsList.isEmpty() && position == 0) {
                        // Nếu xóa item cuối cùng và list rỗng, không cần notifyItemRangeChanged
                    }
                    Log.d(TAG, "onFavoriteRemoved: Đã xóa từ khỏi list và cập nhật adapter. New list size: " + favoriteWordsList.size());
                } else {
                    Log.w(TAG, "onFavoriteRemoved: ID từ tại vị trí (" + position + ") không khớp với từ cần xóa. Load lại toàn bộ.");
                    loadFavoriteWords(); // Load lại để đảm bảo nhất quán
                }
            } else {
                Log.w(TAG, "onFavoriteRemoved: Vị trí (" + position + ") không hợp lệ để xóa. List size: " + favoriteWordsList.size() + ". Load lại toàn bộ.");
                loadFavoriteWords();
            }

            if (favoriteWordsList.isEmpty()) {
                tvNoFavorites.setVisibility(View.VISIBLE);
                rvFavoriteWords.setVisibility(View.GONE);
                tvNoFavorites.setText("     Chưa có từ yêu thích nào.");
            }

            Toast.makeText(getActivity(), "Đã xóa '" + word.getWord() + "' khỏi yêu thích.", Toast.LENGTH_SHORT).show();
           
        } else {
            Toast.makeText(getActivity(), "Lỗi khi xóa khỏi yêu thích!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onFavoriteRemoved: Lỗi khi xóa từ ID: " + word.getId() + " khỏi yêu thích trong DB.");
        }
    }

}
