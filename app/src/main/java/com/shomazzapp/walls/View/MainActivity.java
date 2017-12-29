package com.shomazzapp.walls.View;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import com.shomazzapp.walls.Utils.FragmentChanger;
import com.shomazzapp.walls.View.Adapters.CategoriesAdapter;
import com.shomazzapp.walls.View.Fragments.WallpaperFragment;
import com.shomazzapp.walls.View.Fragments.WallsListFragment;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FragmentChanger {

    private WallsListFragment wallsListFragment = new WallsListFragment();
    private WallpaperFragment wallpaperFragment = new WallpaperFragment();

    private Fragment currentFragment;

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.main_activity_drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ArrayList<VKApiPhotoAlbum> albums;
    private String currentCategoryTitle = "Random";
    private int currentCategory = Constants.ALBUM_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermissionIfNeed();
        albums = new AlbumsRequest().getAlbums();
        setUpNavigationView();

        wallsListFragment.setAlbumID(albums.get(currentCategory).id);
        wallsListFragment.setFragmentChanger(this);
        loadFragment(wallsListFragment);
        currentCategoryTitle = albums.get(currentCategory).title;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToolbarTitle();
    }

    @Override
    public void onBackPressed() {
        // TODO: save fragment state instead of loading wall everytime onBackPressed()
        if (currentFragment instanceof WallpaperFragment) {
            loadFragment(wallsListFragment);
            //loadCategoryToFragment(currentCategory);
        } else {
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setTitle("Exit");
            ab.setMessage(Constants.EXIT_CONFIRMATION_MSG);
            ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ab.show();
        }
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
        currentCategory = position;
        currentCategoryTitle = albums.get(currentCategory).title;
        setToolbarTitle();
        wallsListFragment.setAlbumID(albums.get(currentCategory).id);
        wallsListFragment.loadAlbum(albums.get(currentCategory).id);
        drawer.closeDrawers();
    }

    public void loadFragment(Fragment fragment) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
        if (fragment instanceof WallpaperFragment) {
            getSupportActionBar().hide();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //TODO: wallpaperFragment != real wallpaperFragment, it's empty; Fix it!
            currentFragment = wallpaperFragment;
        } else {
            getSupportActionBar().show();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            currentFragment = wallsListFragment;
        }
    }

    @Override
    public void changeFragment(Fragment fragment) {
        loadFragment(fragment);
    }

    public void setToolbarTitle() {
        toolbar_title.setText(currentCategoryTitle);
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