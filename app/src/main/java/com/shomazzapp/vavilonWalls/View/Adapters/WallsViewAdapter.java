package com.shomazzapp.vavilonWalls.View.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.WallsLoader;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class WallsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FragmentRegulator fragmentRegulator;
    public static final int TYPE_FOOTER = 222;
    public static final int TYPE_ITEM = 333;
    List<View> footers = new ArrayList<>();
    private Context context;
    private ArrayList<VKApiPhoto> wallpapers;
    private WallsLoader wallsLoader;

    private boolean loaded = false;
    private boolean fullAlbumLoaded = false;

    private RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
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

    public WallsViewAdapter(Context context, FragmentRegulator fragmentRegulator, WallsLoader wallsLoader) {
        this.context = context;
        this.wallsLoader = wallsLoader;
        this.fragmentRegulator = fragmentRegulator;
    }

    public void updateData(ArrayList<VKApiPhoto> walls) {
        this.wallpapers = walls;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View wallpapperView = inflater.inflate(R.layout.walls_view_item, parent, false);
            return new ImageViewHolder(wallpapperView);
        } else {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_item, parent, false);
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new FooterViewHolder(linearLayout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (wallpapers != null) {
            if (position >= wallpapers.size()) {
                View v = footers.get(position - wallpapers.size());
                prepareFooter((FooterViewHolder) holder, v);
            } else {
                Glide.with(context)
                        .load(wallpapers.get(position).photo_604)
                        .transition(withCrossFade())
                        .thumbnail(0.17f)
                        .listener(requestListener)
                        .apply(options)
                        .into(((ImageViewHolder) holder).imageView);
            }
           /* Log.d("WallsViewAdapter", "position == " + position + "  size == " + wallpapers.size()
                    + "\n loaded == " + loaded);*/
            if (position == wallpapers.size() - 6 && !loaded && !fullAlbumLoaded)
                new LoadMoreWallsAsyncTask(holder).execute();
        }
    }

    public int loadMore() {
        int size = wallpapers.size();
        wallpapers.addAll(wallsLoader.getWallsByCategory(wallsLoader.isNewCategory() ?
                        Constants.NEW_WALLS_ALBUM_ID : wallpapers.get(2).album_id,
                wallpapers.size()));
        size = wallpapers.size() - size;
        Log.d(this.getClass().getSimpleName(), "loadMore() called! size == " + size);
        return size;
    }

    @Override
    public int getItemCount() {
        return wallpapers == null ? 0 : wallpapers.size() + footers.size();
    }

    private void prepareFooter(FooterViewHolder vh, View view) {
        vh.base.removeAllViews();
        vh.base.addView(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= wallpapers.size())
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    public void addFooter(View footer) {
        if (!footers.contains(footer)) {
            footers.add(footer);
            notifyItemInserted(wallpapers.size() + footers.size() - 1);
        }
    }

    public void removeFooter(View footer) {
        if (footers.contains(footer)) {
            notifyItemRemoved(wallpapers.size() + footers.indexOf(footer));
            footers.remove(footer);
            if (footer.getParent() != null) {
                ((ViewGroup) footer.getParent()).removeView(footer);
            }
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout base;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.base = (LinearLayout) itemView.findViewById(R.id.loading_progress_bar);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.wallpaper_item_ivew);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                /*Intent intent = new Intent(context, WallpaperActivity.class);
                intent.putExtra(Constants.EXTRA_WALLS, wallpapers);
                intent.putExtra(Constants.EXTRA_WALL_POSITION, position);
                intent.putExtra(Constants.EXTRA_IS_FOR_SAVED_WALLS, false);
                intent.putExtra(Constants.EXTRA_IS_NEW_CATEGORY, wallsLoader.isNewCategory());
                context.startActivity(intent);*/
                wallsLoader.loadVKWallpaperFragment(wallpapers, position);
            }
        }
    }

    protected class LoadMoreWallsAsyncTask extends AsyncTask<Void, Void, Void> {
        RecyclerView.ViewHolder holder;

        protected LoadMoreWallsAsyncTask(RecyclerView.ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaded = true;
            Log.d("WallsAdapter", "Loading more walls...");
            //addFooter(v);
            if (fragmentRegulator == null) Log.d("WallsViewAdapter", "fragmentRegulator == null");
            else fragmentRegulator.setProgressVisible(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (loadMore() < 1) fullAlbumLoaded = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loaded = false;
            Log.d("WallsAdapter", "Loaded!");
            notifyDataSetChanged();
            if (fragmentRegulator != null) {
                fragmentRegulator.setProgressVisible(false);
                fragmentRegulator.notifyWallsUpdated();
            }
            //wallsLoader.cahngeProgressState(true);
            //removeFooter(v);
        }
    }

}