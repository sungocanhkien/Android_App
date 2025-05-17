package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    public interface OnItemClickListener {
        void onItemClick(FavoriteWord word);
    }

    public FavoriteAdapter(Context context, List<FavoriteWord> favoriteWords) {
        this.context = context;
        this.favoriteWords = favoriteWords;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView txtWord, txtPhonetic, txtMeaning;
        ImageView imgFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWord = itemView.findViewById(R.id.tvWord);
            txtPhonetic = itemView.findViewById(R.id.tvPhonetic);
            txtMeaning = itemView.findViewById(R.id.tvMeaning);

            imgFavorite = itemView.findViewById(R.id.imgFavorite);
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
        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wordText = word.getWord();
                String phonetic = word.getPhonetic();
                String type = word.getType();
                String meaning = word.getMeaning();
                String example = word.getExample();
                String audio = word.getAudio();

                FavoriteWord favoriteWord = new FavoriteWord(wordText, phonetic, type, meaning, example, audio);
                FavoriteRepository repository = new FavoriteRepository(context);
                repository.addFavorite(favoriteWord); // Gọi class đã sửa

                Toast.makeText(context, "Đã thêm vào mục yêu thích", Toast.LENGTH_SHORT).show();

                // Đổi icon thành đã thích (nếu bạn có hình ảnh khác)
                holder.imgFavorite.setImageResource(R.drawable.ic_favorite); // Bạn cần thêm hình này vào drawable
            }
        });

        holder.txtPhonetic.setText(word.getPhonetic());
        holder.txtMeaning.setText(word.getMeaning());
        holder.bind(word, listener);
    }

    @Override
    public int getItemCount() {
        return favoriteWords.size();
    }
}
