package com.shomazzapp.vavilonWalls.Utils;

import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

public interface WallsLoader {

    boolean isNewCategory();

    ArrayList<VKApiPhoto> getWallsByCategory(int albumID, int offset);

    void setCurrentWalls(ArrayList<VKApiPhoto> walls);

}
