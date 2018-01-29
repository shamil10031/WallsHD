package com.shomazzapp.vavilonWalls.View;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shomazzapp.vavilonWalls.Presenter.WallsListPresenter;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.RoboErrorReporter;
import com.shomazzapp.vavilonWalls.Utils.WallsLoader;
import com.shomazzapp.vavilonWalls.View.Fragments.CategoriesFragment;
import com.shomazzapp.vavilonWalls.View.Fragments.WallpaperFragment;
import com.shomazzapp.vavilonWalls.View.Fragments.WallsListFragment;
import com.shomazzapp.walls.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FragmentRegulator {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    @BindView(R.id.app_bar_progress)
    ProgressBar progressBar;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.main_activity_drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private WallpaperFragment wallpaperFragment = new WallpaperFragment();
    private WallsListFragment wallsListFragment = new WallsListFragment();
    private CategoriesFragment categoriesFragment = new CategoriesFragment();
    private Fragment currentFragment;
    private String log = "mainActivity";
    //private SharedPreferences sharedPreferences;
    NavigationView.OnNavigationItemSelectedListener navClickListenner =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.drawer_categories:
                            loadFragment(categoriesFragment);
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_saved_walls:
                            currentFragment = wallsListFragment = new WallsListFragment();
                            wallsListFragment.setFragmentRegulator(MainActivity.this);
                            loadFragment(wallsListFragment);
                            wallsListFragment.setForSavedWalls(true);
                            wallsListFragment.changeToSavedWalls();
                            wallsListFragment.loadSavedWalls();
                            setToolbarTitle("Saved");
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_remove_ad:
                            Toast.makeText(MainActivity.this, "Remove Ad!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_rate_app:
                            Toast.makeText(MainActivity.this, "Rate App!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_share:
                            Toast.makeText(MainActivity.this, "Share!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_feedback:
                            Toast.makeText(MainActivity.this, "Feedback!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_about_info:
                            Toast.makeText(MainActivity.this, "About Info!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;
                    }
                    item.setChecked(true);
                    return true;
                }
            };
    private RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.mipmap.header_background)
            .error(R.mipmap.header_background);

    public static String getPhotoMaxQualityLink(VKApiPhoto vkApiPhoto) {
        String links = vkApiPhoto.photo_2560 + vkApiPhoto.photo_1280 + vkApiPhoto.photo_807
                + vkApiPhoto.photo_604 + vkApiPhoto.photo_130 + vkApiPhoto.photo_75;
        //System.out.println(links);
        int index = links.indexOf(".jpg");
        if (index < 1) return "";
        else return links.substring(0, index + ".jpg".length());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoboErrorReporter.bindReporter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //if (!VKSdk.isLoggedIn())
        VKSdk.login(this, VKScope.OFFLINE, VKScope.PHOTOS, VKScope.GROUPS, VKScope.DOCS);
        //else
        init();
    }

    public void init() {
        ButterKnife.bind(this);
        categoriesFragment = new CategoriesFragment();
        requestPermissionIfNeed();
        setUpNavigationView();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        loadCategoriesFragment();
        delayedHide(100);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(log, "OnActivityResult!");
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                //Constants.ACCES_TOKEN = res.accessToken;
                init();
            }

            @Override
            public void onError(VKError error) {
                VKSdk.logout();
                //VKSdk.login(MainActivity.this, VKScope.OFFLINE, VKScope.PHOTOS, VKScope.GROUPS, VKScope.DOCS);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public void hide() {
        mVisible = false;
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        delayedHide(100);
    }

    @Override
    public void setToolbarTitle(String title) {
        Log.d(log, " setToolbarTitle (  " + title + "  )!");
        toolbar_title.setText(title);
    }

    @Override
    public void onBackPressed() {
        try {
            if (currentFragment instanceof WallsListFragment) loadCategoriesFragment();
            else if (currentFragment instanceof WallpaperFragment)
                wallsListFragment.closeWallpaperFragment();
            else {
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("Exit");
                ab.setMessage(Constants.EXIT_CONFIRMATION_MSG);
                ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                        finishAffinity();
                        System.exit(0);
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
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setUpNavigationView() {
        loadHeaderBackground();
        setSupportActionBar(toolbar);
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
        navView.setNavigationItemSelectedListener(navClickListenner);
    }

    public void loadHeaderBackground() {
        try {
            ImageView im = navView.getHeaderView(0).findViewById(R.id.nav_header_bg);
            ArrayList<VKApiPhoto> walls = WallsListPresenter.getNavHeaderAlbum();
            VKApiPhoto vkApiPhoto = walls.get(0);
            Glide.with(drawer)
                    .load(getPhotoMaxQualityLink(vkApiPhoto))
                    .apply(options)
                    .into(im);
            //        Log.d(log, "maxQuality = " + getPhotoMaxQualityLink(vkApiPhoto));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.d(log, "can't load header background!");
        }
    }

    @Override
    public void lockNavView(boolean lock) {
        if (drawer != null)
            if (lock) drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            else drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void setProgressVisible(boolean visible) {
        if (visible) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    @Override
    public WallpaperFragment getWallpaperFragment() {
        return wallpaperFragment;
    }

    @Override
    public void reloadHeader() {
        loadHeaderBackground();
    }

    @Override
    public void loadCategoriesFragment() {
        currentFragment = categoriesFragment;
        categoriesFragment.setFragmentRegulator(this);
        setToolbarTitle("Categories");
        loadFragment(categoriesFragment);
        navView.setCheckedItem(R.id.drawer_categories);
        lockNavView(false);
    }

    @Override
    public void loadWallsListFragment(int albumId, String category) {
        currentFragment = wallsListFragment = new WallsListFragment();
        wallsListFragment.setForSavedWalls(false);
        wallsListFragment.setAlbumID(albumId);
        wallsListFragment.changeToInternetWalls();
        wallsListFragment.setFragmentRegulator(this);
        setToolbarTitle(category);
        loadFragment(wallsListFragment);
        lockNavView(false);
        Log.d(log, albumId + "   " + category);
    }

    @Override
    public void loadVKWallpaperFragment(ArrayList<VKApiPhoto> walls,
                                        int currentPosition, WallsLoader wallsLoader) {
        setProgressVisible(false);
        currentFragment = wallpaperFragment = new WallpaperFragment();
        wallpaperFragment.setParametrsForVKWalls(walls, currentPosition, wallsLoader);
        lockNavView(true);
    }

    @Override
    public void loadSavedWallpaperFragment(ArrayList<File> walls,
                                           int currentPosition, WallsLoader wallsLoader) {
        setProgressVisible(false);
        currentFragment = wallpaperFragment = new WallpaperFragment();
        wallpaperFragment.setParametrsForSavedWalls(walls, currentPosition, wallsLoader);
        lockNavView(true);
    }

    @Override
    public void notifyWallsUpdated() {
        if (wallpaperFragment != null)
            wallpaperFragment.notifyWallsUodated();
    }

    @Override
    public void closeWallpaperFragment() {
        currentFragment = wallsListFragment;
        lockNavView(false);
        getSupportActionBar().show();
        hide();
    }

    public void loadFragment(Fragment fragment) {
        /*Fragment f = new Fragment();
        try{
            f = fragment.getClass().newInstance();
        } catch (Exception e){}*/
        Log.d(log, "loadFragment : " + fragment.getClass().getSimpleName());
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
        getSupportActionBar().show();
        drawer.closeDrawers();
    }

    // ----- Permission settings ----- //

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