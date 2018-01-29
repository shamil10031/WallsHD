package com.shomazzapp.vavilonWalls.Utils;

import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

public interface WallsLoader {

    boolean isNewCategory();

    ArrayList<VKApiPhoto> getWallsByCategory(int albumID, int offset);

    void loadVKWallpaperFragment(ArrayList<VKApiPhoto> walls,
                                 int currentPosition);

    void loadSavedWallpaperFragment(ArrayList<File> walls,
                                    int currentPosition);

    void closeWallpaperFragment();

}