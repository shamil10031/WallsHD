package com.shomazzapp.vavilonWalls.Utils;

public interface FragmentRegulator {

    //void

    void setToolbarTitle(String title);

    void loadCategoriesFragment();

    void loadWallsListFragment(int albumId, String category);

    void reloadHeader();

}
