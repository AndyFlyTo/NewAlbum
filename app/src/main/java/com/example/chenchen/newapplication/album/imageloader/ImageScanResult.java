package com.example.chenchen.newapplication.album.imageloader;

import android.util.Log;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchen on 18-5-1.
 */

public class ImageScanResult {

    private Map<String,ArrayList<String>> albumInfo;

    public void setAlbumInfo(Map<String, ArrayList<String>> albumInfo) {
//        Log.d("chen","setAlbumInfo");
        this.albumInfo = albumInfo;
    }

    public Map<String, ArrayList<String>> getAlbumInfo() {
        return albumInfo;
    }
}
