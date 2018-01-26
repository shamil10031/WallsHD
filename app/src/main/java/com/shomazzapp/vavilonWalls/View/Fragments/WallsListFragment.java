package com.shomazzapp.vavilonWalls.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shomazzapp.vavilonWalls.Presenter.WallsListPresenter;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.MyOnScrollListener;
import com.shomazzapp.vavilonWalls.Utils.NetworkHelper;
import com.shomazzapp.vavilonWalls.Utils.WallsLoader;
import com.shomazzapp.vavilonWalls.View.Adapters.SavedWallsViewAdapter;
import com.shomazzapp.vavilonWalls.View.Adapters.WallsViewAdapter;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WallsListFragment extends Fragment implements WallsLoader, SwipeRefreshLayout.OnRefreshListener {

    public static String log = "wallslist";
    @BindView(R.id.walls_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_to_refresh_walls)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.network_lay_saved_walls)
    RelativeLayout networkLay;
    @BindView(R.id.tView)
    TextView textView;
    private int albumID;
    private boolean isForSavedWalls;
    private FragmentRegulator fragmentRegulator;
    private WallsViewAdapter wallsViewAdapter;
    private SavedWallsViewAdapter savedWallsViewAdapter;
    private Context context;
    private WallsListPresenter presenter;
    private View mainView;
    private RecyclerView.LayoutManager layoutManager;
    private MyOnScrollListener onScrollListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_walls_list, container, false);
            ButterKnife.bind(this, mainView);
            init();
        }
        if (!isForSavedWalls)
            loadAlbum(albumID, 0);
        else loadSavedWalls();
        return mainView;
    }

    @Override
    public void setCurrentWalls(ArrayList<VKApiPhoto> walls) {
        //((MainActivity) getActivity()).
    }

    @Override
    public boolean isNewCategory() {
        return presenter.isNewCategory();
    }

    @Override
    public ArrayList<VKApiPhoto> getWallsByCategory(int albumID, int offset) {
        return presenter.getWallsByCategory(albumID, offset);
    }

    public void onNetworkChanged(boolean succes) {
        textView.setText(isForSavedWalls ? "You have no saved walls "
                : "Something wrong with network connection :(");
        if (succes) {
            networkLay.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            networkLay.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void setFragmentRegulator(FragmentRegulator changer) {
        this.fragmentRegulator = changer;
    }

    public void loadAlbum(int albumID, int offset) {
        if (presenter != null)
            presenter.loadWallByCategory(albumID, offset);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onScrollListener != null) {
            onScrollListener.reset(0, true);
            Log.d(log, "onResume()!");
        }
    }

    public void loadSavedWalls() {
        if (presenter != null)
            presenter.loadSavedWalls();
    }

    public void updateData(ArrayList<VKApiPhoto> walls) {
        if (NetworkHelper.isOnLine(this.context)) {
            wallsViewAdapter.updateData(walls);
            recyclerView.smoothScrollToPosition(0);
            onNetworkChanged(true);
        } else {
            onNetworkChanged(false);
            Toast.makeText(this.context, Constants.ERROR_NETWORK_MSG, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSavedWallsData(ArrayList<File> walls) {
        onNetworkChanged(true);
        if (walls.size() < 1) onNetworkChanged(false);
        savedWallsViewAdapter.updateData(walls);
        recyclerView.smoothScrollToPosition(0);
    }

    public void init() {
        layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        presenter = new WallsListPresenter(this);
        Log.d(log, "isForSavedWalls : " + isForSavedWalls);
        if (!isForSavedWalls) {
            wallsViewAdapter = new WallsViewAdapter(context, null, this);
            recyclerView.setAdapter(wallsViewAdapter);
            onScrollListener = new MyOnScrollListener((GridLayoutManager) layoutManager) {
                @Override
                public void onLoadMore() {
                    wallsViewAdapter.loadMore();
                }
            };
            //recyclerView.setOnScrollListener(onScrollListener);
        } else {
            savedWallsViewAdapter = new SavedWallsViewAdapter(context, null, this);
            recyclerView.setAdapter(savedWallsViewAdapter);
        }
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pink_color),
                getResources().getColor(R.color.blue_color));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.app_overlay);
    }

    public void changeToInternetWalls() {
        setForSavedWalls(false);
        if (mainView != null) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
            layoutManager.setAutoMeasureEnabled(true);
            recyclerView.setLayoutManager(layoutManager);
            presenter = new WallsListPresenter(this);
            wallsViewAdapter = new WallsViewAdapter(context, null, this);
            recyclerView.setAdapter(wallsViewAdapter);
        }
    }

    public void changeToSavedWalls() {
        setForSavedWalls(true);
        Log.d(log, "main view " + (mainView == null ? "=" : "not") + " null");
        if (mainView != null) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
            layoutManager.setAutoMeasureEnabled(true);
            recyclerView.setLayoutManager(layoutManager);
            presenter = new WallsListPresenter(this);
            savedWallsViewAdapter = new SavedWallsViewAdapter(context, null, this);
            recyclerView.setAdapter(savedWallsViewAdapter);
        }
    }

    public void setForSavedWalls(boolean b) {
        this.isForSavedWalls = b;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isForSavedWalls = false;
        Glide.get(context).clearMemory();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Glide.getPhotoCacheDir(context).delete();
                Glide.get(context).clearDiskCache();
                return null;
            }
        }.execute();
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int id) {
        this.albumID = id;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isForSavedWalls) loadAlbum(albumID, 0);
                else loadSavedWalls();
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        }, 500);
    }
}
