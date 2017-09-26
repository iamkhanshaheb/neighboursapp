package com.neighbours.neighbours.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neighbours.neighbours.R;
import com.neighbours.neighbours.Util.TimeUtil;
import com.neighbours.neighbours.config.AppConfig;
import com.neighbours.neighbours.models.FeedResponse;
import com.neighbours.neighbours.models.Post;
import com.neighbours.neighbours.ui.PostDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by Joker on 9/22/16.
 */

public class FeedAdapter extends  RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private ArrayList<FeedResponse> itemModel;
    private Context mContext;

    public FeedAdapter(ArrayList<FeedResponse> item, Context context) {
        itemModel = item;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtExcerpt;
        public TextView txtName;
        public TextView txtTime;
        public ImageView image, img_pic;

        public ViewHolder(View v) {
            super(v);
            txtExcerpt = (TextView)v.findViewById(R.id.newsExcerpt);
            image = (ImageView)v.findViewById(R.id.newsImage);
            txtName = (TextView)v.findViewById(R.id.tvName);
            txtTime = (TextView)v.findViewById(R.id.tvTime);
            img_pic = (ImageView)v.findViewById(R.id.img_pic);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public Post getPostItem(int position) {
        return itemModel.get(position).getPost();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txtName.setText(getPostItem(position).getUserName().toString().replace("\"", ""));
        holder.txtTime.setText(TimeUtil.getHumanReadableTime(getPostItem(position).getCreated()));
        holder.txtExcerpt.setText(getPostItem(position).getText().toString().replace("\"", ""));
        Picasso.with(holder.image.getContext()).load(AppConfig.IMAGE_HOST+getPostItem(position).getPhoto()).into(holder.image);
        Log.d(TAG, "onBindViewHolder: tada" + getPostItem(position).getUserPhotoUrl());
        Picasso.with(holder.image.getContext()).load(getPostItem(position).getUserPhotoUrl().toString().replace("\"", "")).into(holder.img_pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("post_id", getPostItem(position).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemModel.size();
    }
}
