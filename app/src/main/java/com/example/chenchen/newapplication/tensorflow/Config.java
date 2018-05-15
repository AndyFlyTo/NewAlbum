package com.example.chenchen.newapplication.tensorflow;


import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;

import java.util.List;

public class Config {
    // for tensorflow
    public static final int NUM_CLASSES = 10;

    public static final int INPUT_SIZE = 32;

    public static final int IMAGE_MEAN = 128;
    public static final float IMAGE_STD = 128;
    public static final String INPUT_NAME = "inputnode";
    public static final String OUTPUT_NAME = "outnode";
    public static final String MODEL_FILE = "file:///android_asset/vgg16_mod-model.pb";
    public static final String LABEL_FILE = "file:///android_asset/retrained_labels.txt";

    // database
    public static MyDatabaseHelper dbHelper;
    public static int dbversion = 5;
    public static String DB_NAME = "Album.db";
    public static String ALBUM_NAME="AlbumPhotos";

}
