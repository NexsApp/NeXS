package in.co.nexs.nexsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import in.co.nexs.nexsapp.FeedActivity;
import in.co.nexs.nexsapp.models.NewCard;
import in.co.nexs.nexsapp.R;

import java.util.ArrayList;
import java.util.List;


public class SportsNewsAdapter extends RecyclerView.Adapter<SportsNewsAdapter.SportsNewsViewHolder> {
    private final List<NewCard> data;
    private final Context context;

    public SportsNewsAdapter(ArrayList<NewCard> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public SportsNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_item, parent, false);
        SportsNewsViewHolder vh = new SportsNewsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SportsNewsViewHolder holder, int position) {
        NewCard currentItem = data.get(position);
        Glide.with(context).load(currentItem.getImgResource()).into(holder.newsTitleImg);
        holder.newsTitleTv.setText(currentItem.getNewsHeadLine());
        holder.newsTitleTv.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SportsNewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitleTv;
        ImageView newsTitleImg;

        SportsNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitleTv = itemView.findViewById(R.id.news_headline_tv);
            newsTitleImg = itemView.findViewById(R.id.news_front_pic);
            newsTitleImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FeedActivity.class);
                    intent.putExtra("articleId", data.get(getAdapterPosition()).getId());
                    intent.putExtra("showById", true);
                    context.startActivity(intent);
                }
            });
        }
    }
}
