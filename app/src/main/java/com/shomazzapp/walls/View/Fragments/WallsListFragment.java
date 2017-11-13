package com.shomazzapp.walls.View.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shomazzapp.walls.Presenter.WallsListPresenter;
import com.shomazzapp.walls.R;
import com.shomazzapp.walls.View.Adapters.WallsViewAdapter;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WallsListFragment extends Fragment {

    private int albumID;

    @BindView(R.id.walls_view)
    RecyclerView recyclerView;
    private WallsViewAdapter adapter;
    private Context context;
    private WallsListPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walls_list, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
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

        adapter = new WallsViewAdapter(this.context, presenter.getWallsByAlbumID(albumID));
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
