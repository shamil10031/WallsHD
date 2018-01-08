package com.shomazzapp.vavilonWalls.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shomazzapp.vavilonWalls.Presenter.WallsListPresenter;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.View.Adapters.WallsViewAdapter;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WallsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private int albumID;

    private FragmentRegulator fragmentRegulator;

    @BindView(R.id.walls_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_to_refresh_walls)
    SwipeRefreshLayout swipeRefreshLayout;
    private WallsViewAdapter adapter;
    private Context context;
    private WallsListPresenter presenter;
    private View mainView;

    public static String log = "wallslist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(log, "---------- onCreated() called");
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(log, "---------- onCreateView() called");
        if (mainView == null) {
            Log.d(log, "                mianView == null");
            mainView = inflater.inflate(R.layout.fragment_walls_list, container, false);
            ButterKnife.bind(this, mainView);
            init();
        }
        loadAlbum(albumID);
        return mainView;
    }

    public void setFragmentRegulator(FragmentRegulator changer) {
        this.fragmentRegulator = changer;
    }

    /*public void openWallpaperFragment(ArrayList<VKApiPhoto> wallpapers, int position) {
        wallpaperFragment.setWalls(wallpapers);
        wallpaperFragment.setCurrentPosition(position);
        fragmentRegulator.changeFragment(wallpaperFragment);
    }*/

    public void loadAlbum(int albumID) {
        Log.d(log, "---------- loadAlbum() (presenter " + (presenter == null ? "is" : "not") + " null)");
        if (presenter != null)
            presenter.loadWallByCategory(albumID);
    }

    public void updateData(ArrayList<VKApiPhoto> walls) {
        Log.d(log, "---------- updateData() called");
        adapter.updateData(walls);
        recyclerView.smoothScrollToPosition(0);
    }

    public void init() {
        Log.d(log, "---------- init() called");
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        presenter = new WallsListPresenter(this);

        // adapter = new WallsViewAdapter(getActivity(), presenter.getWallsByAlbumID(albumID), this);
        adapter = new WallsViewAdapter(context, null, this);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pink_color),
                getResources().getColor(R.color.blue_color));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.app_overlay);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(log, "---------- onAttach() called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(log, "---------- onDetach() called");
    }

    public void setAlbumID(int id) {
        this.albumID = id;
    }

    public int getAlbumID() {
        return albumID;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAlbum(albumID);
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
