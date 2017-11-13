package com.shomazzapp.walls.View.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shomazzapp.walls.R;

import java.util.ArrayList;

public class CategoriesAdapter extends BaseAdapter {

    private ArrayList<String> categories;
    private Context context;
    private LayoutInflater inflater;

    public CategoriesAdapter(Context context, ArrayList<String> categories) {
        this.categories = categories;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        View v = inflater.inflate(R.layout.nav_menu_item, null);
        holder.textView = (TextView) v.findViewById(R.id.category_item_tv);
        holder.textView.setText(categories.get(i));
        return v;
    }

    private class Holder {
        TextView textView;
    }

}
