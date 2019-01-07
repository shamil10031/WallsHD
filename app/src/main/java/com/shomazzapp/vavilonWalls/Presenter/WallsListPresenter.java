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
import java.util.HashSet;

public class WallsListPresenter {

    private WallsListFragment fragment;
    private boolean isNewCategory;
    private int hidedWallsCount = 0;
    private String log;

    public WallsListPresenter(WallsListFragment fragment) {
        this.fragment = fragment;
        this.hidedWallsCount = 0;
        this.log = getClass().getSimpleName();
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

    public ArrayList<VKApiPhoto> getNewWalls(int offset, HashSet<Integer> ids) {
        int repeats = 1;
        this.isNewCategory = true;
        Log.d(log, "Start getting New Walls...");
        AllPhotosRequest req = new AllPhotosRequest(offset + hidedWallsCount,
                Constants.WALLS_LOAD_COUNT, ids);
        ArrayList<VKApiPhoto> walls = req.getPhotos();
        hidedWallsCount += req.getHidedWallsCount();
        while (req.getAllPhotosCount() - Constants.WALLS_LOAD_COUNT * repeats > 0
                && walls.size() < Constants.WALLS_LOAD_COUNT) {
            req = new AllPhotosRequest(offset + walls.size() + hidedWallsCount,
                    Constants.WALLS_LOAD_COUNT, ids);
            walls.addAll(req.getPhotos());
            repeats++;
            hidedWallsCount += req.getHidedWallsCount();
        }
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

    public void loadWallByCategory(int albumID, int offset, HashSet<Integer> ids) {
        fragment.updateData(getWallsByCategory(albumID, offset, ids));
    }

    public void clearHidedWallsCount() {
        this.hidedWallsCount = 0;
    }

    public ArrayList<VKApiPhoto> getWallsByCategory(int albumID, int offset, HashSet<Integer> ids) {
        if (albumID == Constants.NEW_WALLS_ALBUM_ID)
            return getNewWalls(offset, ids);
        else return getWallsByAlbumID(albumID, offset, Constants.WALLS_LOAD_COUNT);
    }

    public void loadSavedWalls() {
        fragment.updateSavedWallsData(getSavedWalls());
    }

    public boolean isNewCategory() {
        return isNewCategory;
    }

}
