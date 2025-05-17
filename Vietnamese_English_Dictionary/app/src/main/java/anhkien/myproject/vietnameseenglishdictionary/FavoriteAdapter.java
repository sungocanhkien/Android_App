package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import anhkien.myproject.vietnameseenglishdictionary.database.FavoriteDatabaseHelper;
import anhkien.myproject.vietnameseenglishdictionary.database.FavoriteRepository;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<FavoriteWord> favoriteWords;
    private OnItemClickListener listener;
    private TextToSpeech textToSpeech;
    private int expandedPosition = -1;

    public FavoriteAdapter(Context context, List<FavoriteWord> favoriteWords, TextToSpeech textToSpeech) {
        this.context = context;
        this.favoriteWords = favoriteWords;
        this.textToSpeech = textToSpeech;
    }

    public interface OnItemClickListener {
        void onItemClick(FavoriteWord word);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView txtWord, txtPhonetic, txtMeaning;
        ImageButton btnPlayAudio, btnDelete;
        View layoutDetails;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWord = itemView.findViewById(R.id.tvWord);
            txtPhonetic = itemView.findViewById(R.id.tvPhonetic);
            txtMeaning = itemView.findViewById(R.id.tvMeaning);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);

            layoutDetails = itemView.findViewById(R.id.layoutDetails);
        }
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteWord word = favoriteWords.get(position);
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
