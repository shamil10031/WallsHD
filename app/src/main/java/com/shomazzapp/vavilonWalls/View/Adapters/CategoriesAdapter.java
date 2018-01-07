package com.shomazzapp.vavilonWalls.View.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;


public class CategoriesAdapter extends BaseAdapter {

    private ArrayList<VKApiPhotoAlbum> albums;
    private LayoutInflater inflater;
    private Context context;
    private String log = "categoriesAdapter";


    public CategoriesAdapter(Context context, ArrayList<VKApiPhotoAlbum> albums) {
        this.albums = albums;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int i) {
        return albums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setAlbums(ArrayList<VKApiPhotoAlbum> albums) {
        this.albums = albums;
    }

    public ArrayList<VKApiPhotoAlbum> getAlbums() {
        return albums;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        View v = inflater.inflate(R.layout.nav_menu_item, null);
        holder.textView = (TextView) v.findViewById(R.id.category_item_tv);
        holder.imageView = (CircularImageView) v.findViewById(R.id.category_item_imv);
        holder.textView.setText(albums.get(i).title);
        Log.d(log, "album thumb id = " + albums.get(i).thumb_src);
        Glide.with(context)
                .load(albums.get(i).thumb_src)
                .apply(options)
                .into(holder.imageView);
        return v;
    }

    private RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.mipmap.icon)
            .error(R.mipmap.icon);

    private class Holder {
        CircularImageView imageView;
        TextView textView;
    }
}
