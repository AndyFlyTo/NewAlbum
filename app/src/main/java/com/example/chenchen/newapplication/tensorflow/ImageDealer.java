package com.example.chenchen.newapplication.tensorflow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.media.ThumbnailUtils.extractThumbnail;


/**
 * Created by chenchen on 5/1/18.
 */

public class ImageDealer {
    /**
     * resize bitmap for tf
     *
     * @param bitmap
     * @return
     */
    public static Bitmap dealImageForTF(Bitmap bitmap) {
        try {
            // resize
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth = ((float) Config.INPUT_SIZE) / width;
            float scaleHeight = ((float) Config.INPUT_SIZE) / height;
            Matrix matrix = new Matrix();
            //x轴 y轴 缩放比例  //
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return newbm;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * resize image by url
     *
     * @param context
     * @param url
     * @return
     */
    public static Bitmap getThumbnails(Context context, String url) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        bitmap = BitmapFactory.decodeFile(url, options);
        bitmap = extractThumbnail(bitmap, 180, 180);
        return bitmap;
    }

    /**
     * insert new image into db
     *
     * @param url
     * @param results
     * @param operator
     * @param value
     */

    //得到分类结果时 往数据库里插入数据
    public static void insertImageIntoDB(String url, List<Classifier.Recognition> results,
                                         MyDatabaseOperator operator, ContentValues value) {
        if (results == null) {
            Log.d("chen", "分类结果为null");
            return;
        }

        List<Map> findResult;
        //Recognition is a Class
        for (Classifier.Recognition cr : results) {
            String type = cr.getTitle();
            // AlbumPhotos
            value.clear();
            value.put("album_name", type);   //// TODO: 18-5-2  in this album_name==tf_type 
            Log.d("chen", "album_name" + type);
            value.put("url", url);
            value.put("tf_type", type);
            value.put("folder_name", "Download"); //// TODO: 18-5-2 暂时先处理download下面的
            value.put("confidence", cr.getConfidence());  //null
            operator.insert("AlbumPhotos", value);

            // Album----图片类别 每一类图片的类别（存入数据库的第一张图片）
            findResult = operator.search("Album", "album_name = '" + type + "'");
            if (findResult.size() == 0) {
                value.clear();
                value.put("album_name", type);
                value.put("image", url);
                operator.insert("Album", value);
            }
            //TFInfromation
//            value.clear();
//            value.put("url", image);
//            value.put("tf_type", type);
//            value.put("confidence", cr.getConfidence());
//            operator.insert("TFInformation", value);
            //不要关数据库
        }
    }

    //暂时插入无法分类的图片信息
    public static void InsertImageInfoDB(MyDatabaseOperator operator, ContentValues values) {

    }

    public static void insertImageFolderIntoDB(MyDatabaseOperator operator, ContentValues value) {

        operator.insert("AlbumFolder", value);

    }

    public static List<Map<String, String>> getAlbumInfo(Context ctx) {
        List<Map<String, String>> result = new ArrayList<>();
        Config.dbHelper = new MyDatabaseHelper(ctx, "Album.db", null, Config.dbversion);

        SQLiteDatabase db = Config.dbHelper.getWritableDatabase();
        Log.d("chen", "getAlbumInfo");
        Cursor cursor = db.query("Album", null, null, null, null, null, null);
        Log.d("chen", "cursor size=" + cursor.getCount());
        if (cursor == null)
            Log.d("chen", "cursor is null !!!!!!!!!!!!!");
        if (cursor.moveToFirst()) {
            Map<String, String> tmp;
            do {
                tmp = new HashMap<>();
                ;
                String album_name = cursor.getString(cursor.getColumnIndex("album_name"));
                String url = cursor.getString(cursor.getColumnIndex("image"));
                tmp.put("album_name", album_name);
                tmp.put("image", url);
                result.add(tmp);
                Log.d("ITEM", String.valueOf(tmp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.d("Album info", "END");
        return result;
    }

    //转到operator 此时有一个数据库操作的实例 不用这个方法
//    public static List<String> getAllFolderName(Context context){
//        List<String> result = new ArrayList<>();
//        Config.dbHelper = new MyDatabaseHelper(context, "Album.db", null, Config.dbversion);
//        SQLiteDatabase db = Config.dbHelper.getWritableDatabase();
//    }
    //查询某一类别或者文件夹下 所有的图片
//    Cursor c = db.rawQuery("select * from user where username=? and password = ?",
    public static List<String> getImageInfo(Context ctx, String column_name, String type) {
        List<String> result = new ArrayList<>();
        Config.dbHelper = new MyDatabaseHelper(ctx, "Album.db", null, Config.dbversion);
        SQLiteDatabase db = Config.dbHelper.getWritableDatabase();
        Log.d("chen", "AlbumPhotos");
        Cursor cursor = db.query("AlbumPhotos", new String[]{"url"}, column_name + "=?", new String[]{type}, null, null, null);

        if (cursor == null)
            Log.d("chen", "cursor is null !!!!!!!!!!!!!");
        if (cursor.moveToFirst()) {

            do {
                result.add(cursor.getString(0));
//                String album_name = cursor.getString(cursor.getColumnIndex("album_name"));
//                String url = cursor.getString(cursor.getColumnIndex("url"));
//                tmp.put("album_name", album_name);
//                tmp.put("image", url);
//                result.add(tmp);
//                Log.d("ITEM", String.valueOf(tmp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Log.d("Album info", "END");
        return result;
    }


    public static List<Map> getFolderInfo(Context context) {
        List<Map> result = new ArrayList<>();
        Config.dbHelper = new MyDatabaseHelper(context, "Album.db", null, Config.dbversion);
        SQLiteDatabase db = Config.dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select folder_name,image from AlbumFolder", null);

        Map<String, String> folder_item;
        if (cursor.moveToFirst()) {
            folder_item = new HashMap<>();
            do {
                folder_item.put(cursor.getString(0), cursor.getString(1));
                result.add(folder_item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("chen", "文件夹页面查询到的数据有" + result.size());
        db.close();
        return result;
    }

    public static int getFolderImageCount(Context context, String folder) {
        Config.dbHelper = new MyDatabaseHelper(context, "Album.db", null, Config.dbversion);
        SQLiteDatabase db = Config.dbHelper.getWritableDatabase();
        Cursor cursor;
        int count = 0;

        cursor = db.query("AlbumPhotos", new String[]{"url"}, "folder_name=?", new String[]{folder}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
        }
//        Log.d("chen",folder+"有图片"+count);

        // TODO: 18-5-3  cursor未关闭 
        cursor.close();
        db.close();
        return count;
    }

    /**
     * use tf to classify the image
     *
     * @param bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static List<Classifier.Recognition>
    do_tensorflow(Bitmap bitmap, Classifier classifier) {
        // resize image
        Bitmap newbm = dealImageForTF(bitmap);
        // get results
        try {
            return classifier.recognizeImage(newbm);
        } catch (Exception e) {
            Log.d("TF-ERROR", "1");
            return null;
        }
    }

}
