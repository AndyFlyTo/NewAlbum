package com.example.chenchen.newapplication.album.Database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchen on 18-4-30.
 */

public class MyDatabaseOperator {

    private Cursor cursor;
    private Context context;
    private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;

    public MyDatabaseOperator(Context context, String dbName, int dbversion) {
        this.context = context;
        this.dbHelper = new MyDatabaseHelper(context, dbName, null, dbversion);
        this.db = dbHelper.getWritableDatabase();
    }

    public List<Map> search(String tableName) {
        return search(tableName, null);
    }

    //return all data  use List<map>  第二个参数 为要返回的列的名字
    public List<Map> search(String tableName, String searchWhat) {
        cursor = db.query(tableName, null, searchWhat, null, null, null, null);
        //cursor指向的是行
        if (cursor != null) {
            Map<String, String> item;
            List<Map> result = new ArrayList<>();
            while (cursor.moveToNext()) {
                String[] columnNames = cursor.getColumnNames();
                item = new HashMap<>();
                for (String columnName : columnNames) {
                    int columnIndex = cursor.getColumnIndex(columnName);
                    String columnValue = cursor.getString(columnIndex);
                    item.put(columnName, columnValue);
                }
                result.add(item);
            }
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public List<String> searchAllFolderName() {
        List<String> folderNameList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select folder_name from AlbumFolder", null);

        if (cursor.moveToFirst()) {
            do{
                folderNameList.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        return folderNameList;
    }

    public long insert(String tableName, ContentValues values) {
        return db.insert(tableName, null, values);
    }


    public List<Map> getExternalImageInfo(Context ctx) {
        //查询外置内存卡的音频字段信息  uri表示要查询的数据库名字+表的名字  内置内存卡INTERNAL_CONTENT_URI
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d("chen", "getExternalImageInfo uri=" + uri);
        //uri=content://media/external/images/media
        return getMediaImageInfo(ctx, uri, null);
    }

    //访问系统图片
    private List<Map> getMediaImageInfo(Context ctx, Uri uri, String[] columns) {
//        String[] columns = new String[]{
//                MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DISPLAY_NAME
//                , MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.DATE_TAKEN};

        ContentResolver contentResolver = ctx.getContentResolver();

        //表示获取某一列的数据 返回值  例如电话联系人有多列 分别表示姓名 ，电话等信息，此时如果只需要name 就name就好了
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        Log.d("chen", "getMediaImageInfo columns=" + columns);


        //打印的是所有图片的信息
        if (cursor != null) {
            Map<String, String> item;
            List<Map> result = new ArrayList<>();
            String[] column = cursor.getColumnNames();


            String columnName = MediaStore.Images.Media.DATA;
            while (cursor.moveToNext()) {


                item = new HashMap<>();

                int columnIndex = cursor.getColumnIndex(columnName);
                String columnValue = cursor.getString(columnIndex);
                item.put(columnName, columnValue);
                result.add(item);
            }
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    public boolean close() {
        try {
            this.db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
