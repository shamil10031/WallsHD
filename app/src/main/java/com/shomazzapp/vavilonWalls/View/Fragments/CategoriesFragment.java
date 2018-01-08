package com.shomazzapp.vavilonWalls.View.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class CategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.categories_listview)
    ListView categoriesListView;
    @BindView(R.id.swipe_to_refresh_categories)
    SwipeRefreshLayout swipeRefreshLayout;

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
        if (fragmentRegulator != null)
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
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pink_color),
                getResources().getColor(R.color.blue_color));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.app_overlay);
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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateData();
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
