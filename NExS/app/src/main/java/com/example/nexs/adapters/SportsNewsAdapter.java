package com.example.nexs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexs.NewCard;
import com.example.nexs.R;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SportsNewsAdapter extends RecyclerView.Adapter<SportsNewsAdapter.SportsNewsViewHolder> {
    private ArrayList<NewCard> data ;

    public SportsNewsAdapter(ArrayList<NewCard> data){
        this.data = data;
    }

    @NonNull
    @Override
    public SportsNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card_item,parent,false);
        SportsNewsViewHolder vh = new SportsNewsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SportsNewsViewHolder holder, int position) {
        NewCard currentItem = data.get(position);
        holder.newsTitleImg.setImageResource(currentItem.getImgResourceId());
        holder.newsTitleTv.setText(currentItem.getNewsHeadLine());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class SportsNewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitleTv;
        ImageView newsTitleImg;
         SportsNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitleTv = itemView.findViewById(R.id.news_headline_tv);
            newsTitleImg = itemView.findViewById(R.id.news_front_pic);
        }
    }
}
