package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import anhkien.myproject.vietnameseenglishdictionary.database.FavoriteRepository;

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

        // Hiển thị từ dựa trên ngôn ngữ gốc của nó trong DB
        // Ví dụ: nếu word.getLanguage() là "en", thì word.getWord() là tiếng Anh
        // và word.getTranslation() là tiếng Việt.
        // Bạn có thể điều chỉnh logic này nếu muốn hiển thị cố định Anh-Việt hoặc Việt-Anh.
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

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_word, parent, false);
        return new FavoriteViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Word word = favoriteWords.get(position);
        holder.txtWord.setText(word.getWord());
        holder.txtPhonetic.setText(word.getPhonetic());
        holder.txtMeaning.setText(word.getMeaning());

        //Ẩn hoặc hiển thị phần chi tiết
        boolean isExpanded = position == expandedPosition;
        holder.layoutDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        //Nhấn vào từ để mở hoặc thu gọn
        holder.txtWord.setOnClickListener(v -> {
            expandedPosition = isExpanded ? -1 : position;
            notifyDataSetChanged();
        });

        //Nút phát âm
        holder.btnPlayAudio.setOnClickListener(v -> {
            String text = word.getWord();
            if (textToSpeech != null && !text.isEmpty()){
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        //Xóa từ khỏi danh sách yêu thích.
        holder.btnDelete.setOnClickListener(v -> {
            FavoriteRepository repository = new FavoriteRepository(context);
            repository.removeFavorite(word.getWord());

            // Xoá khỏi danh sách trong adapter
            favoriteWords.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteWords.size());

            Toast.makeText(context, "Đã xoá khỏi mục yêu thích", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return favoriteWords.size();
    }
    public void shutdownTextToSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();       // Dừng nếu đang phát
            textToSpeech.shutdown();   // Giải phóng tài nguyên
        }
    }

}
