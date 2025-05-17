package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWord = itemView.findViewById(R.id.tvWord);
            txtPhonetic = itemView.findViewById(R.id.tvPhonetic);
            txtMeaning = itemView.findViewById(R.id.tvMeaning);
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
    }

    @Override
    public int getItemCount() {
        return favoriteWords.size();
    }
}
