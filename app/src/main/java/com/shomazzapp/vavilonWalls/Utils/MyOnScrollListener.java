package com.shomazzapp.vavilonWalls.Utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class MyOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = MyOnScrollListener.class.getSimpleName();
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.

    private GridLayoutManager layManager;

    public MyOnScrollListener(GridLayoutManager layManager) {
        this.layManager = layManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layManager.getItemCount();
        firstVisibleItem = layManager.findFirstVisibleItemPosition();
        if (loading && totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore();
            loading = true;
            Log.d(getClass().getSimpleName(), "onLoadMore() called!");
        }
    }

    public void reset(int previousTotal, boolean loading) {
        this.previousTotal = previousTotal;
        this.loading = loading;
    }

    public abstract void onLoadMore();
}