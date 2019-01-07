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
import android.widget.TextView;

import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.NetworkHelper;
import com.shomazzapp.vavilonWalls.View.Adapters.CategoriesAdapter;
import com.shomazzapp.walls.R;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public ArrayList<VKApiPhotoAlbum> albums;
    public boolean isLoading;
    @BindView(R.id.categories_listview)
    ListView categoriesListView;
    @BindView(R.id.swipe_to_refresh_categories)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.network_lay)
    RelativeLayout network_lay;
    @BindView(R.id.splash_screen_progress)
    View progressView;
    @BindView(R.id.swipe_to_reload_tv)
    TextView swipeToReloadTV;
    @BindView(R.id.error_tv)
    TextView errorTV;
    private CategoriesAdapter adapter;
    private Context context;
    private View mainView;
    private FragmentRegulator fragmentRegulator;
    private String log = getClass().getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkHelper.isOnLine(context) && adapter != null) adapter.generateNewWallsAmounts();
        getActivity().findViewById(R.id.appodealBannerView).setBackgroundColor(
                getResources().getColor(R.color.app_background));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentRegulator != null)
            fragmentRegulator.setToolbarTitle(getResources().getString(R.string.categories));
        initMainView(inflater, container);
        showProgress();
        if (NetworkHelper.isOnLine(context)) {
            isLoading = true;
            init();
            onNetworkChanged(true);
            isLoading = false;
        } else {
            isLoading = false;
            onNetworkChanged(false);
            initOnRefresher();
        }
        return mainView;
    }

    private void hideProgress() {
        progressView.setVisibility(View.GONE);
    }

    private void showProgress() {
        progressView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isLoading) ;
                hideProgress();
            }
        }).run();
    }

    public void initMainView(LayoutInflater inflater, ViewGroup container) {
        mainView = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, mainView);
    }

    public void onNetworkChanged(boolean succes) {
        if (VKSdk.isLoggedIn())
            if (succes) {
                network_lay.setVisibility(View.GONE);
                categoriesListView.setVisibility(View.VISIBLE);
            } else {
                swipeToReloadTV.setText(R.string.swipe_to_reload);
                errorTV.setText(R.string.error_network_msg);
                network_lay.setVisibility(View.VISIBLE);
                categoriesListView.setVisibility(View.GONE);
            }
        else {
            swipeToReloadTV.setText(R.string.auth_in_menu_msg);
            errorTV.setText(R.string.access_denied_msg);
            network_lay.setVisibility(View.VISIBLE);
            categoriesListView.setVisibility(View.GONE);
        }
    }

    public void loadAlbums() {
        albums = fragmentRegulator.getAlbums();
        if (!NetworkHelper.isOnLine(context))
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNetworkChanged(false);
                }
            }, 100);
    }

    public void initOnRefresher() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pink_color),
                getResources().getColor(R.color.blue_color));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.app_overlay);
    }

    public void setListView() {
        if (adapter == null) adapter = new CategoriesAdapter(getActivity(), albums);
        else adapter.setAlbums(albums);
        adapter.notifyDataSetChanged();
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fragmentRegulator.loadWallsListFragment(adapter.getAlbums().get(i).id,
                        adapter.getAlbums().get(i).title);
                adapter.writeNewSizeToPref(adapter.getAlbums().get(i));
            }
        });
        categoriesListView.smoothScrollToPosition(0);
    }

    public void init() {
        initOnRefresher();
        loadAlbums();
        setListView();
    }

    public void setFragmentRegulator(FragmentRegulator changer) {
        this.fragmentRegulator = changer;
    }

    public void updateData() {
        if (NetworkHelper.isOnLine(context)) {
            fragmentRegulator.loadAlbums();
            albums = fragmentRegulator.getAlbums();
            onNetworkChanged(true);
            setListView();
            adapter.generateNewWallsAmounts();
            fragmentRegulator.reloadHeader();
        } else onNetworkChanged(false);
    }

    @Override
    public void onRefresh() {
        if (NetworkHelper.isOnLine(context))
            onNetworkChanged(true);
        else onNetworkChanged(false);
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
