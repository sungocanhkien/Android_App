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
        ImageView imgFavorite;
        ImageButton btnPlayAudio;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWord = itemView.findViewById(R.id.tvWord);
            txtPhonetic = itemView.findViewById(R.id.tvPhonetic);
            txtMeaning = itemView.findViewById(R.id.tvMeaning);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
        }

        public void bind(FavoriteWord word, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(word);
                }
            });
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
        holder.bind(word, listener);

        holder.imgFavorite.setOnClickListener(v -> {
            FavoriteWord favoriteWord = new FavoriteWord(
                    word.getWord(),
                    word.getPhonetic(),
                    word.getType(),
                    word.getMeaning(),
                    word.getExample(),
                    word.getAudio()
            );
            FavoriteRepository repository = new FavoriteRepository(context);
            repository.addFavorite(favoriteWord);
            Toast.makeText(context, "Đã thêm vào mục yêu thích", Toast.LENGTH_SHORT).show();
            holder.imgFavorite.setImageResource(R.drawable.ic_favorite);
        });

        holder.btnPlayAudio.setOnClickListener(v -> {
            String text = word.getWord();
            if (textToSpeech != null && !text.isEmpty()) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
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
