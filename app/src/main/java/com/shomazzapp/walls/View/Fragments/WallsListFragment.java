package com.shomazzapp.walls.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shomazzapp.walls.Presenter.WallsListPresenter;
import com.shomazzapp.walls.R;
import com.shomazzapp.walls.Utils.FragmentChanger;
import com.shomazzapp.walls.View.Adapters.WallsViewAdapter;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WallsListFragment extends Fragment {

    private int albumID;

    private FragmentChanger fragmentChanger;

    @BindView(R.id.walls_view)
    RecyclerView recyclerView;
    private WallsViewAdapter adapter;
    private Context context;
    private WallsListPresenter presenter;
    private WallpaperFragment wallpaperFragment = new WallpaperFragment();
    private View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_walls_list, container, false);
            ButterKnife.bind(this, mainView);
            init();
        }
        return mainView;
    }

    public void setFragmentChanger(FragmentChanger changer) {
        this.fragmentChanger = changer;
    }

    public FragmentChanger getFragmentChanger() {
        return fragmentChanger;
    }

    public void openWallpaperFragment(ArrayList<VKApiPhoto> wallpapers, int position) {
        wallpaperFragment.setWalls(wallpapers);
        wallpaperFragment.setCurrentPosition(position);
        fragmentChanger.changeFragment(wallpaperFragment);
    }

    public void updateData(ArrayList<VKApiPhoto> walls) {
        adapter.updateData(walls);
        recyclerView.scrollToPosition(0);
    }

    public void loadAlbum(int albumID) {
        presenter.loadWallByCategory(albumID);
    }

    public void init() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);
        presenter = new WallsListPresenter(this);

        adapter = new WallsViewAdapter(this.context, presenter.getWallsByAlbumID(albumID), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setAlbumID(int id) {
        this.albumID = id;
    }

    public int getAlbumID() {
        return albumID;
    }
}
