package com.shomazzapp.vavilonWalls.Presenter;

import android.os.Environment;
import android.util.Log;

import com.shomazzapp.vavilonWalls.Requests.AllPhotosRequest;
import com.shomazzapp.vavilonWalls.Requests.PhotosRequest;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.View.Fragments.WallsListFragment;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

public class WallsListPresenter {

    private WallsListFragment fragment;
    private boolean isNewCategory;

    public WallsListPresenter(WallsListFragment fragment) {
        this.fragment = fragment;
    }

    public ArrayList<VKApiPhoto> getNewWalls(int offset, int count) {
        ArrayList<VKApiPhoto> walls = new AllPhotosRequest(offset, count).getPhotos();
        this.isNewCategory = true;
        //Log.d(WallsListFragment.log, "Album size : " + walls.size());
        return walls;
    }

    public ArrayList<VKApiPhoto> getAllWallsByAlbumID(int albumID, int offset) {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(albumID, offset).getPhotos();
        this.isNewCategory = false;
        Log.d(WallsListFragment.log, "Album size : " + walls.size() + "  id: " + albumID);
        return walls;
    }

    public ArrayList<VKApiPhoto> getWallsByAlbumID(int albumID, int offset, int count) {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(albumID, offset, count).getPhotos();
        this.isNewCategory = false;
        Log.d(WallsListFragment.log, "Album size : " + walls.size() + "  id: " + albumID);
        return walls;
    }

    public static ArrayList<VKApiPhoto> getNavHeaderAlbum() {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(Constants.NAVHEADER_ALBUM_ID, 0, 1).getPhotos();
        return walls;
    }

    public static ArrayList<File> getSavedWalls() {
        ArrayList<File> files = new ArrayList<File>();
        File folder = new File(Environment.getExternalStorageDirectory(),
                Constants.FOLDER_NAME);
        File[] filesInFolder = folder.listFiles();
        if (folder.exists() && filesInFolder.length > 0)
            for (File file : filesInFolder) {
                if (!file.getName().startsWith(".") && !file.isDirectory() && (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg"))) {
                    files.add(0, file);
                }
            }
        return files;
    }

    public void loadWallByCategory(int albumID, int offset) {
        fragment.updateData(getWallsByCategory(albumID, offset));
    }

    public ArrayList<VKApiPhoto> getWallsByCategory(int albumID, int offset) {
        if (albumID == Constants.NEW_WALLS_ALBUM_ID)
            return getNewWalls(offset, Constants.WALLS_LOAD_COUNT);
        else return getWallsByAlbumID(albumID, offset, Constants.WALLS_LOAD_COUNT);
    }

    public void loadSavedWalls() {
        fragment.updateSavedWallsData(getSavedWalls());
    }

    public boolean isNewCategory() {
        return isNewCategory;
    }

}
