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

    public WallsListPresenter(WallsListFragment fragment) {
        this.fragment = fragment;
    }

    public void loadWallByCategory(int albumID) {
        if (albumID == Constants.NEW_WALLS_ALBUM_ID)
            fragment.updateData(getNewWalls());
        else fragment.updateData(getWallsByAlbumID(albumID));
    }

    public static ArrayList<VKApiPhoto> getNewWalls() {
        ArrayList<VKApiPhoto> walls = new AllPhotosRequest(0, Constants.NEW_WALLS_COUNT).getPhotos();
        //Log.d(WallsListFragment.log, "Album size : " + walls.size());
        return walls;
    }

    public static ArrayList<VKApiPhoto> getWallsByAlbumID(int albumID) {
        ArrayList<VKApiPhoto> walls = new PhotosRequest(albumID).getPhotos();
        Log.d(WallsListFragment.log, "Album size : " + walls.size() + "  id: " + albumID);
        return walls;
    }

    public void loadSavedWalls() {
        fragment.updateSavedWallsData(getSavedWalls());
    }

    public static ArrayList<File> getSavedWalls() {
        ArrayList<File> files = new ArrayList<File>();
        File folder = new File(Environment.getExternalStorageDirectory(),
                Constants.FOLDER_NAME);
        File[] filesInFolder = folder.listFiles();
        if (filesInFolder.length > 0)
            for (File file : filesInFolder) {
                if (!file.isDirectory() && (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg"))) {
                    files.add(0, file);
                }
            }
        return files;
    }

    //public static boolean is

}
