package com.shomazzapp.vavilonWalls.Presenter;

import android.util.Log;

import com.shomazzapp.vavilonWalls.Requests.PhotosRequest;
import com.shomazzapp.vavilonWalls.View.Fragments.WallsListFragment;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

public class WallsListPresenter {

    private WallsListFragment fragment;

    public WallsListPresenter(WallsListFragment fragment) {
        this.fragment = fragment;
    }

    public void loadWallByCategory(int albumID) {
        Log.d(WallsListFragment.log, "---------- loadWallsByCategory() from presenter called");
        fragment.updateData(getWallsByAlbumID(albumID));
    }

    public static ArrayList<VKApiPhoto> getWallsByAlbumID(int albumID) {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(albumID).getPhotos();
        System.out.println("Album size : " + walls.size() + "  id: " + albumID);
        return walls;
    }
}
