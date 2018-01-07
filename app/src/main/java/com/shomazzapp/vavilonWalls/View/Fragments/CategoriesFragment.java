package com.shomazzapp.vavilonWalls.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shomazzapp.vavilonWalls.Requests.AlbumsRequest;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.View.Adapters.CategoriesAdapter;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment {

    @BindView(R.id.categories_listview)
    ListView categoriesListView;

    private CategoriesAdapter adapter;
    private Context context;
    private View mainView;
    public ArrayList<VKApiPhotoAlbum> albums;

    private FragmentRegulator fragmentRegulator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRegulator.setToolbarTitle("Categories");
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_categories, container, false);
            ButterKnife.bind(this, mainView);
            init();
        }
        return mainView;
    }

    public void loadAlbums() {
        this.albums = new AlbumsRequest().getAlbums();
    }

    public void init() {
        albums = new AlbumsRequest().getAlbums();
        adapter = new CategoriesAdapter(getActivity(), albums);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("CategoriesFragment omClick! " + adapter.getAlbums().get(i).title);
                fragmentRegulator.loadWallsListFragment(adapter.getAlbums().get(i).id,
                        adapter.getAlbums().get(i).title);
            }
        });
    }

    public void setFragmentRegulator(FragmentRegulator changer) {
        this.fragmentRegulator = changer;
    }

    public void updateData() {
        loadAlbums();
        adapter.setAlbums(albums);
        adapter.notifyDataSetChanged();
        categoriesListView.smoothScrollToPosition(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
