package com.shomazzapp.vavilonWalls.View;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.appodeal.ads.Appodeal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shomazzapp.vavilonWalls.Presenter.WallsListPresenter;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.RoboErrorReporter;
import com.shomazzapp.vavilonWalls.Utils.Tasks.DownloadAsyncTask;
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
import java.util.Calendar;
import java.util.HashSet;

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
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
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
    private SharedPreferences sharedPreferences;
    private WallpaperFragment wallpaperFragment = new WallpaperFragment();
    private WallsListFragment wallsListFragment = new WallsListFragment();
    private CategoriesFragment categoriesFragment = new CategoriesFragment();
    private Fragment currentFragment;
    private String log = "mainActivity";
    NavigationView.OnNavigationItemSelectedListener navClickListenner =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.drawer_categories:
                            loadFragment(categoriesFragment);
                            item.setChecked(true);
                            break;
                        case R.id.drawer_saved_walls:
                            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL))
                                Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            currentFragment = wallsListFragment = new WallsListFragment();
                            wallsListFragment.setFragmentRegulator(MainActivity.this);
                            loadFragment(wallsListFragment);
                            wallsListFragment.setForSavedWalls(true);
                            wallsListFragment.changeToSavedWalls();
                            wallsListFragment.loadSavedWalls();
                            setToolbarTitle(getResources().getString(R.string.saved_walls));
                            item.setChecked(true);
                            break;
                        case R.id.login:
                            if (VKSdk.isLoggedIn())
                                showExitFromVkDialog();
                            else
                                showFeaturesOfVKDialog();
                            break;
                        /*case R.id.drawer_remove_ad:
                            Toast.makeText(MainActivity.this, "Remove Ad!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_rate_app:
                            Toast.makeText(MainActivity.this, "Rate App!", Toast.LENGTH_SHORT)
                                    .show();
                            drawer.closeDrawers();
                            break;*/
                        case R.id.drawer_share:
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                    getResources().getString(R.string.share_text));
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_feedback:
                            showFeedbackDialog();
                            drawer.closeDrawers();
                            break;
                        case R.id.drawer_about_info:
                            showAboutDialog();
                            drawer.closeDrawers();
                            break;
                    }
                    drawer.closeDrawers();
                    return true;
                }
            };
    private RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.mipmap.header_background)
            .error(R.mipmap.header_background);
    private final Runnable loadHeaderBgRunnable = new Runnable() {
        @Override
        public void run() {
            ImageView im = navView.getHeaderView(0).findViewById(R.id.nav_header_bg);
            File file = new File(DownloadAsyncTask.getFolder(), Constants.HEADER_FILE_NAME);
            if (file.exists())
                Glide.with(drawer)
                        .load(file)
                        .apply(options)
                        .into(im);
        }
    };

    public static String getPhotoMaxQualityLink(VKApiPhoto vkApiPhoto) {
        String links = vkApiPhoto.photo_2560 + vkApiPhoto.photo_1280 + vkApiPhoto.photo_807
                + vkApiPhoto.photo_604 + vkApiPhoto.photo_130 + vkApiPhoto.photo_75;
        int index = links.indexOf(".jpg");
        if (index < 1) return "";
        else return links.substring(0, index + ".jpg".length());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoboErrorReporter.bindReporter(this);
        super.onCreate(savedInstanceState);
        setAppodealAds();
        setContentView(R.layout.activity_main);
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_FILENAME, MODE_PRIVATE);
        if (!hasPermission(Constants.WRITE_STORAGE_PERMISSION))
            requestPermissionIfNeed();
        else init();
    }

    public void setAppodealAds() {
        Appodeal.setTesting(false);
        String appKey = "8415d2e5f0f6424f352f3a81c90388d5cf142a9cb388c6f0";
        Appodeal.initialize(this, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL);
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    public void init() {
        ButterKnife.bind(this);
        categoriesFragment = new CategoriesFragment();
        if (!VKSdk.isLoggedIn() && !sharedPreferences.contains(Constants.FEATURES_DIALOG_SHOWED_BOOL))
            showFeaturesOfVKDialog();
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
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                init();
            }

            @Override
            public void onError(VKError error) {
                VKSdk.logout();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void hide() {
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Appodeal.show(this, Appodeal.BANNER_VIEW);
        delayedHide(100);
        changeVKItemTitle();
    }

    public void changeVKItemTitle() {
        if (navView != null && navView.getMenu().size() > 0) {
            boolean founded = false;
            int i = 0;
            while (!founded && i < navView.getMenu().size()) {
                if (navView.getMenu().getItem(i).getItemId() == R.id.login) {
                    founded = true;
                    if (VKSdk.isLoggedIn())
                        navView.getMenu().getItem(i).setTitle(R.string.logout);
                    else
                        navView.getMenu().getItem(i).setTitle(R.string.login);
                } else i++;
            }
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar_title.setText(title);
        toolbar_title.setTextColor(Color.WHITE);
    }

    public void showFeaturesOfVKDialog() {
        hide();
        if (drawer != null)
            drawer.closeDrawers();
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle(getResources().getString(R.string.features));
        ab.setMessage(getResources().getString(R.string.account_features_msg));
        ab.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                VKSdk.login(MainActivity.this, VKScope.OFFLINE, VKScope.PHOTOS, VKScope.GROUPS, VKScope.DOCS);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putBoolean(Constants.FEATURES_DIALOG_SHOWED_BOOL, true);
                ed.apply();
                changeVKItemTitle();
            }
        });
        ab.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                changeVKItemTitle();
            }
        });
        ab.show();
    }

    public void showExitFromVkDialog() {
        hide();
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle(getResources().getString(R.string.exit_from_vk));
        ab.setMessage(getResources().getString(R.string.exit_from_vk_msg));
        ab.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VKSdk.logout();
                changeVKItemTitle();
                init();
            }
        });
        ab.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                changeVKItemTitle();
            }
        });
        ab.show();
    }

    public void showPermissionRequiredDialog() {
        hide();
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setMessage(getResources().getString(R.string.need_permission_msg));
        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestPermissionIfNeed();
            }
        });
        ab.show();
    }

    public void showFeedbackDialog() {
        hide();
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setMessage(getResources().getString(R.string.feedback_text));
        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

    public void showAboutDialog() {
        hide();
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setMessage(getResources().getString(R.string.about_text));
        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

    public void showExitAlertDialog() {
        hide();
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle(getResources().getString(R.string.exit));
        ab.setMessage(getResources().getString(R.string.exit_confirmation_msg));
        ab.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                finishAffinity();
                System.exit(0);
            }
        });
        ab.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

    @Override
    public void onBackPressed() {
        try {
            if (currentFragment instanceof WallsListFragment) loadCategoriesFragment();
            else if (currentFragment instanceof WallpaperFragment)
                wallsListFragment.closeWallpaperFragment();
            else showExitAlertDialog();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setUpNavigationView() {
        updateHeader();
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
        changeVKItemTitle();
    }

    public void updateDataInPref() {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt(Constants.HEADER_LAST_UPDATE_DATA,
                Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        e.apply();
    }

    public boolean isNeedToDownloadHeader() {
        int lastDay = sharedPreferences.getInt(Constants.HEADER_LAST_UPDATE_DATA, 0);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return currentDay - lastDay >= Constants.DAYS_PAST_TO_UPDATE;
    }

    public Thread getDownloadHeaderThread() {
        final ArrayList<VKApiPhoto> walls = WallsListPresenter.getNavHeaderAlbum();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DownloadAsyncTask.downloadFromLink(getPhotoMaxQualityLink(walls.get(0)), Constants.HEADER_FILE_NAME);
                updateDataInPref();
            }
        };
        return new Thread(r);
    }

    public void updateHeader() {
        if (isNeedToDownloadHeader() || !new File(DownloadAsyncTask.getFolder(),
                Constants.HEADER_FILE_NAME).exists()) {
            Thread t = getDownloadHeaderThread();
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        loadHeaderBgRunnable.run();
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
        updateHeader();
    }

    @Override
    public void loadCategoriesFragment() {
        currentFragment = categoriesFragment;
        categoriesFragment.setFragmentRegulator(this);
        setToolbarTitle(getResources().getString(R.string.categories));
        loadFragment(categoriesFragment);
        navView.setCheckedItem(R.id.drawer_categories);
        lockNavView(false);
    }

    @Override
    public void loadWallsListFragment(int albumId, String category, HashSet<Integer> ids) {
        currentFragment = wallsListFragment = new WallsListFragment();
        wallsListFragment.setForSavedWalls(false);
        wallsListFragment.setAlbumID(albumId);
        wallsListFragment.changeToInternetWalls();
        wallsListFragment.setFragmentRegulator(this);
        wallsListFragment.setIdsHashSet(ids);
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
        Appodeal.hide(MainActivity.this, Appodeal.BANNER_VIEW);
    }

    @Override
    public void loadSavedWallpaperFragment(ArrayList<File> walls,
                                           int currentPosition, WallsLoader wallsLoader) {
        setProgressVisible(false);
        currentFragment = wallpaperFragment = new WallpaperFragment();
        wallpaperFragment.setParametrsForSavedWalls(walls, currentPosition, wallsLoader);
        lockNavView(true);
        Appodeal.hide(MainActivity.this, Appodeal.BANNER_VIEW);
    }

    @Override
    public void notifyWallsUpdated() {
        if (wallpaperFragment != null)
            wallpaperFragment.notifyWallsUodated();
    }

    @Override
    public void closeWallpaperFragment() {
        Appodeal.show(this, Appodeal.BANNER_VIEW);
        currentFragment = wallsListFragment;
        lockNavView(false);
        getSupportActionBar().show();
        hide();
    }

    public void loadFragment(Fragment fragment) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
        getSupportActionBar().show();
        drawer.closeDrawers();
    }

    // ----- Permission settings ----- //

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            showPermissionRequiredDialog();
        else init();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    private void requestPermissionIfNeed() {
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