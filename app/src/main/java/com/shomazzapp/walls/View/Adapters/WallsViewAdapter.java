package com.shomazzapp.walls.View.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shomazzapp.walls.R;
import com.shomazzapp.walls.Utils.Constants;
import com.shomazzapp.walls.View.WallpaperActivity;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

public class WallsViewAdapter extends RecyclerView.Adapter<WallsViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<VKApiPhoto> wallpapers;

    public WallsViewAdapter(Context context, ArrayList<VKApiPhoto> wallpapers) {
        this.context = context;
        this.wallpapers = wallpapers;
    }

    public void updateData(ArrayList<VKApiPhoto> walls) {
        this.wallpapers = walls;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View wallpapperView = inflater.inflate(R.layout.walls_view_item, parent, false);
        ViewHolder holder = new ViewHolder(wallpapperView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .load(wallpapers.get(position).photo_604)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        System.out.println("Exception ! Model : " + model);
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.vk_clear_shape)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return (wallpapers.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.wallpaper_item_ivew);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                VKApiPhoto wallpaper = wallpapers.get(position);
                Intent intent = new Intent(context, WallpaperActivity.class);
                intent.putExtra(Constants.EXTRA_WALL, wallpaper);
                context.startActivity(intent);
            }
        }
    }
}
