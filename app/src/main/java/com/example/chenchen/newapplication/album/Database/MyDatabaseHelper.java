package com.example.chenchen.newapplication.album.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;



public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_ALBUM_PHOTOS = "create table AlbumPhotos ("
            + "id integer primary key autoincrement, "
            + "album_name text, "    // TF分类之后的文件夹名
            + "folder_name text, "   //在系统中所在的文件夹名
            + "url text,"
            +"tf_type text, "
            +"confidence text)";
    private static final String CREATE_ALBUM = "create table Album ("
            + "id integer primary key autoincrement, "
            + "album_name text, "
            + "image text)";
    private static final String CREATE_FOLDER = "create table AlbumFolder ("
            + "id integer primary key autoincrement, "
            + "folder_name text, "
            + "image text)";

//    private static final String CREATE_TF_INFORMATION = "create table TFInformation ("
//            + "id integer primary key autoincrement, "
//            + "url text, "
//            + "tf_type text, "
//            + "confidence text)";
//    private static final String CREATE_SETTINGS = "create table Settings ("
//            + "id integer primary key autoincrement, "
//            + "notFirstIn text, "
//            + "updateTime integer)";
    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("chen","database onCreate");
        try {
            db.execSQL(CREATE_ALBUM_PHOTOS);
            db.execSQL(CREATE_ALBUM);
            db.execSQL(CREATE_FOLDER);

            Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("chen","upgrade database");

        try {
            db.execSQL("drop table if exists AlbumPhotos");
            db.execSQL("drop table if exists Album");
            db.execSQL("drop table if exists TFInformation");
            db.execSQL("drop table if exists Settings");
            onCreate(db);
        } catch (Exception e) {
            ;
        }
    }
}
