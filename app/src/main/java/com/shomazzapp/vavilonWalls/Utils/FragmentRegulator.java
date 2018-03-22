package com.shomazzapp.vavilonWalls.Utils;

import com.shomazzapp.vavilonWalls.View.Fragments.WallpaperFragment;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public interface FragmentRegulator {

    void setToolbarTitle(String title);

    void loadCategoriesFragment();

    void loadWallsListFragment(int albumId, String category, HashSet<Integer> ids);

    void loadVKWallpaperFragment(ArrayList<VKApiPhoto> walls,
                                 int currentPosition, WallsLoader wallsLoader);

    void loadSavedWallpaperFragment(ArrayList<File> walls,
                                    int currentPosition, WallsLoader wallsLoader);

    void reloadHeader();

    void closeWallpaperFragment();

    WallpaperFragment getWallpaperFragment();

    void setProgressVisible(boolean visible);

    void lockNavView(boolean lock);

    void notifyWallsUpdated();

    void hide();

}
