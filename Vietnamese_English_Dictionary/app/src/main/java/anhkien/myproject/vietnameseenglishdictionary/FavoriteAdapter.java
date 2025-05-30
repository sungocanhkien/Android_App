package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private static final String TAG = "FavoriteAdapter";
    private List<Word> favoriteWords;
    private Context context;
    private OnFavoriteRemovedListener listener; // Interface để callback về Fragment

    // Interface để thông báo cho Fragment khi một từ được xóa
    public interface OnFavoriteRemovedListener {
        void onFavoriteRemoved(Word word, int position);
    }

    public FavoriteAdapter(Context context, List<Word> favoriteWords, OnFavoriteRemovedListener listener) {
        this.context = context;
        this.favoriteWords = favoriteWords;
        this.listener = listener;
    }
    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_word, parent, false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Word currentWord = favoriteWords.get(position);

        // Hiển thị từ dựa trên ngôn ngữ gốc trong DB
        // Nếu word.getLaguage() là "en", thì word.getWord() là tiếng Anh
        // và word.getTranslation() là tiếng Việt.
        if ("en".equalsIgnoreCase(currentWord.getLanguage())) {
            holder.tvOriginalWord.setText(currentWord.getWord()); // Tiếng Anh
            holder.tvTranslation.setText(currentWord.getTranslation()); // Tiếng Việt
        } else if ("vi".equalsIgnoreCase(currentWord.getLanguage())) {
            holder.tvOriginalWord.setText(currentWord.getWord()); // Tiếng Việt
            holder.tvTranslation.setText(currentWord.getTranslation()); // Tiếng Anh
        } else {
            // Trường hợp không xác định, hiển thị cả hai
            holder.tvOriginalWord.setText(currentWord.getWord());
            holder.tvTranslation.setText(currentWord.getTranslation());
        }
        holder.btnRemoveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Thông báo cho Fragment rằng một từ cần được xóa
                    // Fragment sẽ xử lý việc xóa khỏi DB và cập nhật danh sách
                    listener.onFavoriteRemoved(currentWord, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteWords != null ? favoriteWords.size() : 0;
    }

    // ViewHolder class
    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOriginalWord;
        public TextView tvTranslation;
        public ImageButton btnRemoveFavorite;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            tvOriginalWord = itemView.findViewById(R.id.tv_item_word_original);
            tvTranslation = itemView.findViewById(R.id.tv_item_word_translation);
            btnRemoveFavorite = itemView.findViewById(R.id.btn_item_remove_favorite);
        }
    }
}
