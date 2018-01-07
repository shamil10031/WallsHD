package com.shomazzapp.vavilonWalls.View;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.FragmentRegulator;
import com.shomazzapp.vavilonWalls.Utils.RoboErrorReporter;
import com.shomazzapp.vavilonWalls.View.Fragments.CategoriesFragment;
import com.shomazzapp.vavilonWalls.View.Fragments.WallsListFragment;
import com.shomazzapp.walls.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FragmentRegulator {

    private WallsListFragment wallsListFragment = new WallsListFragment();
    private CategoriesFragment categoriesFragment = new CategoriesFragment();

    private Fragment currentFragment;

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.main_activity_drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RoboErrorReporter.bindReporter(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        wallsListFragment = new WallsListFragment();
        categoriesFragment = new CategoriesFragment();
        requestPermissionIfNeed();
        setUpNavigationView();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToolbarTitle("Categories");
        loadCategoriesFragment();
        setFragmentChangers();
    }

    public void setFragmentChangers() {
        wallsListFragment.setFragmentRegulator(this);
        categoriesFragment.setFragmentRegulator(this);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof CategoriesFragment) {
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
        } else loadCategoriesFragment();   // instanceof WallsListFragment
    }

    private void setUpNavigationView() {
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
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_categories:
                        loadFragment(categoriesFragment);
                        drawer.closeDrawers();
                        break;
                    case R.id.drawer_saved_walls:
                        //TODO: display saved walls
                        Toast.makeText(MainActivity.this, "Saved Walls", Toast.LENGTH_SHORT)
                                .show();
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
        });
    }

    public void loadFragment(Fragment fragment) {
        System.out.println("From MainActivity loadFragment : " + fragment.getClass().getSimpleName());
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
        getSupportActionBar().show();
        drawer.closeDrawers();
    }

    @Override
    public void loadWallsListFragment(int albumId, String category) {
        currentFragment = wallsListFragment;
        getSupportActionBar().setTitle(category);
        wallsListFragment.setAlbumID(albumId);
        loadFragment(wallsListFragment);
    }

    @Override
    public void loadCategoriesFragment() {
        currentFragment = categoriesFragment;
        setToolbarTitle("Categories");
        loadFragment(categoriesFragment);
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