package com.shomazzapp.walls.View;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shomazzapp.walls.R;
import com.shomazzapp.walls.Requests.AlbumsRequest;
import com.shomazzapp.walls.Utils.Constants;
import com.shomazzapp.walls.View.Adapters.CategoriesAdapter;
import com.shomazzapp.walls.View.Fragments.WallsListFragment;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private WallsListFragment wallsListFragment = new WallsListFragment();

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.main_activity_drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ArrayList<VKApiPhotoAlbum> albums;
    private String CURRENT_CATEGORY = "Newest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermissionIfNeed();
        albums = new AlbumsRequest().getAlbums();
        setUpNavigationView();

        wallsListFragment.setAlbumID(albums.get(Constants.ALBUM_POSITION).id);
        loadCurrentFragment();
        CURRENT_CATEGORY = albums.get(Constants.ALBUM_POSITION).title;
        setToolbarTitle();
    }


    private void setUpNavigationView() {
        setSupportActionBar(toolbar);
        ListView l = (ListView) findViewById(R.id.list_vieww);
        l.setOnItemClickListener(new DrawerItemClickListener());
        l.setAdapter(new CategoriesAdapter(this, getCategoriesArray()));
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public ArrayList<String> getCategoriesArray() {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < albums.size(); i++)
            arr.add(i, albums.get(i).title);
        return arr;
    }

    public void loadCategoryToFragment(int position) {
        CURRENT_CATEGORY = albums.get(position).title;
        setToolbarTitle();
        wallsListFragment.loadAlbum(albums.get(position).id);
        drawer.closeDrawers();
    }

    public void loadCurrentFragment() {
        final android.support.v4.app.FragmentTransaction transaction
                = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.frame, wallsListFragment);
        transaction.commit();
    }

    public void setToolbarTitle() {
        toolbar_title.setText(CURRENT_CATEGORY);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loadCategoryToFragment(position);
        }
    }

    // Permission settings

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    private void requestPermissionIfNeed() {
        if (!hasPermission(Constants.WRITE_STORAGE_PERMISSION))
            requestPermissions(Constants.PERMISSIONS, 200);
    }

    @TargetApi(23)
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }
}

// comment = new CommentRequset(photos.get(0).getId()).getComment();
// address = new DocumentRequest(comment.attachments.get(0).toAttachmentString().toString()).getAddress();