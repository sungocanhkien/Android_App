package anhkien.myproject.vietnameseenglishdictionary;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<FavoriteWord> favoriteList;
    private Context context;

    public FavoriteAdapter(Context context, List<FavoriteWord> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvPhonetic, tvType, tvMeaning, tvExample;
        ImageButton btnPlayAudio;
        LinearLayout layoutDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.txtFavoriteWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvType = itemView.findViewById(R.id.tvType);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvExample = itemView.findViewById(R.id.tvExample);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
        }
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        FavoriteWord word = favoriteList.get(position);
        holder.tvWord.setText(word.getWord());
        holder.tvPhonetic.setText("Phonetic: " + word.getPhonetic());
        holder.tvType.setText("Type: " + word.getType());
        holder.tvMeaning.setText("Meaning: " + word.getMeaning());
        holder.tvExample.setText("Example: " + word.getExample());

        holder.tvWord.setOnClickListener(v -> {
            if (holder.layoutDetails.getVisibility() == View.GONE) {
                holder.layoutDetails.setVisibility(View.VISIBLE);
            } else {
                holder.layoutDetails.setVisibility(View.GONE);
            }
        });

        holder.btnPlayAudio.setOnClickListener(v -> {
            String audioUrl = word.getAudio();
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Toast.makeText(context, "Không phát được âm thanh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }
}

