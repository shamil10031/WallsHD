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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shomazzapp.vavilonWalls.Requests.AlbumsRequest;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.NetworkHelper;
import com.shomazzapp.vavilonWalls.View.Adapters.CategoriesAdapter;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.categories_listview)
    ListView categoriesListView;
    @BindView(R.id.swipe_to_refresh_categories)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.network_lay)
    RelativeLayout network_lay;

    private CategoriesAdapter adapter;
    private Context context;
    private View mainView;
    public ArrayList<VKApiPhotoAlbum> albums;

    private FragmentRegulator fragmentRegulator;
    private String log = "CategoriesFragment";

    private VKApiPhotoAlbum newAlbum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        try {
            newAlbum = new VKApiPhotoAlbum(new JSONObject(
                    "{ \n\"id\": " + Constants.NEW_WALLS_ALBUM_ID + ", \n\"title\": \"New\"}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentRegulator != null)
            fragmentRegulator.setToolbarTitle("Categories");
        if (NetworkHelper.isOnLine(context)) {
            if (mainView == null) {
                initMainView(inflater, container);
                init();
            }
            onNetworkChanged(true);
        } else {
            initMainView(inflater, container);
            onNetworkChanged(false);
        }
        return mainView;
    }

    public void initMainView(LayoutInflater inflater, ViewGroup container) {
        mainView = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, mainView);
        network_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkHelper.isOnLine(context)) {
                    onNetworkChanged(true);
                    init();
                } else
                    Toast.makeText(context, Constants.ERROR_NETWORK_MSG, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onNetworkChanged(boolean succes) {
        if (succes) {
            network_lay.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            network_lay.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    public void loadAlbums() {
        this.albums = new AlbumsRequest().getAlbums();
        albums.add(0, newAlbum);
    }

    public void init() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pink_color),
                getResources().getColor(R.color.blue_color));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.app_overlay);

        loadAlbums();
        adapter = new CategoriesAdapter(getActivity(), albums);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fragmentRegulator.loadWallsListFragment(adapter.getAlbums().get(i).id,
                        adapter.getAlbums().get(i).title);
            }
        });
    }

    public void setFragmentRegulator(FragmentRegulator changer) {
        this.fragmentRegulator = changer;
    }

    public void updateData() {
        if (NetworkHelper.isOnLine(context)) {
            onNetworkChanged(true);
            loadAlbums();
            adapter.setAlbums(albums);
            adapter.notifyDataSetChanged();
            categoriesListView.smoothScrollToPosition(0);
        } else onNetworkChanged(false);
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
