package com.shomazzapp.vavilonWalls.View.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.View.Fragments.WallsListFragment;
import com.shomazzapp.vavilonWalls.View.WallpaperActivity;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class WallsViewAdapter extends RecyclerView.Adapter<WallsViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<VKApiPhoto> wallpapers;
    private WallsListFragment wallsListFragment;

    private RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.vk_clear_shape);

    private RequestListener requestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target,
                                    boolean isFirstResource) {
            System.out.println("Exception ! Model : " + model);
            e.printStackTrace();
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target,
                                       DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };

    public WallsViewAdapter(Context context, ArrayList<VKApiPhoto> wallpapers, WallsListFragment fragment) {
        this.context = context;
        this.wallpapers = wallpapers;
        this.wallsListFragment = fragment;
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
        if (wallpapers != null)
            Glide.with(context)
                    .load(wallpapers.get(position).photo_604)
                    .transition(withCrossFade())
                    .thumbnail(0.17f)
                    .listener(requestListener)
                    .apply(options)
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return wallpapers == null ? 0 : wallpapers.size();
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
                Intent intent = new Intent(context, WallpaperActivity.class);
                intent.putExtra(Constants.EXTRA_WALLS, wallpapers);
                intent.putExtra(Constants.EXTRA_WALL_POSITION, position);
                context.startActivity(intent);
            }
        }
    }
}
