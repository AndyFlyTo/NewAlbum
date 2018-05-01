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

    //return all data  use List<map>   注意有searchwhat  查询条件 where部分
    public List<Map> search(String tableName, String searchWhat) {
        cursor = db.query(tableName, null, searchWhat, null, null, null, null);
        Log.d("chen","查询条件"+searchWhat);
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
    public long insert(String tableName, ContentValues values) {
        return db.insert(tableName, null, values);
    }
    public int erase(String tableName, String whereClause, String[] whereArgs) {
        return db.delete(tableName, whereClause, whereArgs);
    }
    public int update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(tableName, values, whereClause, whereArgs);
    }
    public boolean close() {
        try {
            this.db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map> getExternalImageInfo(Context ctx) {
        //查询外置内存卡的音频字段信息  uri表示要查询的数据库名字+表的名字  内置内存卡INTERNAL_CONTENT_URI
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d("chen","getExternalImageInfo uri="+uri);
        //     uri=content://media/external/images/media
        return getMediaImageInfo(ctx, uri, null);
    }

    //访问系统图片
    private  List<Map> getMediaImageInfo(Context ctx, Uri uri, String[] columns) {
//        String[] columns = new String[]{
//                MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DISPLAY_NAME
//                , MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.DATE_TAKEN};

        ContentResolver contentResolver = ctx.getContentResolver();

        //表示获取某一列的数据 返回值  例如电话联系人有多列 分别表示姓名 ，电话等信息，此时如果只需要name 就name就好了
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        Log.d("chen","getMediaImageInfo columns="+columns);


        //打印的是所有图片的信息
        if (cursor != null) {
            Map<String, String> item;
            List<Map> result = new ArrayList<>();
            String[] column = cursor.getColumnNames();
//            for (String c:column){
                //Log.d("chen","column="+c);
                //图片信息
                //_id,_data,_size,_display_name,mime_type,title,,ata_added,date_modified,descrition.....
//            }
            ////这里键值是什么
            int count=0;
            String columnName= MediaStore.Images.Media.DATA;
            while (cursor.moveToNext()) {


              //  String[] columnNames = cursor.getColumnNames();
                //List<Hashmap<String,String>>
                item = new HashMap<>();
//                这里存储的是所有的信息
//                for (String columnName : columnNames) {
//                    int columnIndex = cursor.getColumnIndex(columnName);
//                    String columnValue = cursor.getString(columnIndex);
//                    item.put(columnName, columnValue);
//                    count++;
//                    //  Log.d("chen","c name="+columnName+" c Value="+columnValue);
//                }
//                现在修改为仅存储图片路径

                int columnIndex = cursor.getColumnIndex(columnName);
                String columnValue = cursor.getString(columnIndex);
                item.put(columnName, columnValue);
                result.add(item);
            }
//            Log.d("chen","map total size="+count);   //这就对了 5980个map 每个里面一共20个
            Log.d("chen","result.size()="+result.size());
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
}
