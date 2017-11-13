package com.shomazzapp.walls.Presenter;

import com.shomazzapp.walls.Requests.PhotosRequest;
import com.shomazzapp.walls.View.Fragments.WallsListFragment;
import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

public class WallsListPresenter {

    private WallsListFragment fragment;

    public WallsListPresenter(WallsListFragment fragment) {
        this.fragment = fragment;
    }

    public void loadWallByCategory(int albumID) {
        fragment.updateData(getWallsByAlbumID(albumID));
    }

    public ArrayList<VKApiPhoto> getWallsByAlbumID(int albumID) {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(albumID).getPhotos();
        System.out.println("Album size : " + walls.size() + "  id: " + albumID);
        return walls;
    }

  /*  public ArrayList<VKApiPhoto> getWalls() {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(fragment.getAlbumID()).getPhotos();
        System.out.println("Album size : " + walls.size() + "  id: " + fragment.getAlbumID());
        return walls;
    }*/
}
