package com.example.chenchen.newapplication.tensorflow;


import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;

import java.util.List;

public class Config {
    // for tensorflow
    public static final int NUM_CLASSES = 10;

    public static final int INPUT_SIZE = 32;
    //这是？？
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

//    public static String[] album_type_name = {
//            "动物",
//            "建筑", "汽车", "花朵",
//            "人物", "二次元", "人物",
//            "风景", "风景", "文字",
//            "物品", "室内", "室内"
//    };
//    public static String[] tf_type_name = {"动物",
//            "建筑", "汽车", "花朵",
//            "很多人", "二次元", "人物",
//            "风景", "雪景", "文字",
//            "物品", "教室", "工作地点"
//    };
//    public static int[] tf_type_image = {R.drawable.animal,
//            R.drawable.building, R.drawable.car,R.drawable.flower,
//            R.drawable.group_people, R.drawable.manga, R.drawable.people,
//            R.drawable.scenery, R.drawable.snow,R.drawable.text,
//            R.drawable.things,R.drawable.room,R.drawable.room
//    };
    public static boolean task_finished = false;
}
