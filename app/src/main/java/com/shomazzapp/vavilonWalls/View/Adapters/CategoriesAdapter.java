package com.shomazzapp.vavilonWalls.View.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;

public class CategoriesAdapter extends BaseAdapter {

    private SharedPreferences sharedPreferences;
    private ArrayList<VKApiPhotoAlbum> albums;
    private LayoutInflater inflater;
    private Context context;
    private String log = "categoriesAdapter";
    private int[] newWallsAmountArr;
    private RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(R.mipmap.icon)
            .error(R.mipmap.icon);

    public CategoriesAdapter(Context context, ArrayList<VKApiPhotoAlbum> albums) {
        this.albums = albums;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_FILENAME, Context.MODE_PRIVATE);
        generateNewWallsAmounts();
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

    public ArrayList<VKApiPhotoAlbum> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<VKApiPhotoAlbum> albums) {
        this.albums = albums;
    }

    public void generateNewWallsAmounts() {
        newWallsAmountArr = new int[albums.size()];
        for (int i = 0; i < albums.size(); i++) {
            newWallsAmountArr[i] = getNewWallsAmount(albums.get(i).id, albums.get(i).size);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int newWallsAmount = -1;
        if (newWallsAmountArr != null)
            newWallsAmount = newWallsAmountArr[i];
        Holder holder = new Holder();
        View v = inflater.inflate(R.layout.category_item, null);
        holder.categoryTextView = (TextView) v.findViewById(R.id.category_item_tv);
        holder.imageView = (CircularImageView) v.findViewById(R.id.category_item_imv);
        holder.newWallsTextView = (TextView) v.findViewById(R.id.new_walls_amount_tv);
        holder.categoryTextView.setText(albums.get(i).title);
        if (newWallsAmount > 0) {
            holder.newWallsTextView.setVisibility(View.VISIBLE);
            holder.newWallsTextView.setText("+" + newWallsAmount);
        } else holder.newWallsTextView.setVisibility(View.INVISIBLE);
        Glide.with(context)
                .load(albums.get(i).thumb_src)
                .apply(options)
                .into(holder.imageView);
        return v;
    }

    public int getNewWallsAmount(int id, int currentSize) {
        int oldSize = sharedPreferences.getInt("" + id, 0);
        return currentSize - oldSize;
    }

    public void writeNewSizeToPref(VKApiPhotoAlbum album) {
        writeNewSizeToPref(album.id, album.size);
    }

    public void writeNewSizeToPref(int id, int size) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt("" + id, size);
        ed.apply();
    }

    private class Holder {
        CircularImageView imageView;
        TextView categoryTextView;
        TextView newWallsTextView;

    }
}
