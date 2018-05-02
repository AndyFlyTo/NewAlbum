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
     * @param context
     * @param url
     * @return
     */
    public static Bitmap getThumbnails(Context context, String url) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        bitmap = BitmapFactory.decodeFile(url, options);
        bitmap = extractThumbnail(bitmap,180 , 180);
        return bitmap;
    }

    /**
     * insert new image into db
     * @param url
     * @param results
     * @param operator
     * @param value
     */

    //得到分类结果时 往数据库里插入数据
    public static void insertImageIntoDB(String url, List<Classifier.Recognition> results,
                                         MyDatabaseOperator operator, ContentValues value) {
        if (results == null) return;
        List<Map> findResult;
        //Recognition is a Class
        Log.d("chen","插入的results size is "+results.size());
        for (Classifier.Recognition cr : results) {
            String type = cr.getTitle();
            // AlbumPhotos
            value.clear();
            value.put("album_name", type);   //// TODO: 18-5-2  in this album_name==tf_type 
            value.put("url", url);
            value.put("tf_type", type);
            value.put("confidence", cr.getConfidence());  //null
            operator.insert("AlbumPhotos", value);

            // Album----图片类别 每一类图片的类别（存入数据库的第一张图片）
            findResult = operator.search("Album", "album_name = '" + type + "'");
            if (findResult.size() == 0) {
                value.clear();
                value.put("album_name", type);
                value.put("image",url );
                operator.insert("Album", value);
            }
            //TFInfromation
//            value.clear();
//            value.put("url", image);
//            value.put("tf_type", type);
//            value.put("confidence", cr.getConfidence());
//            operator.insert("TFInformation", value);
        }
    }


    public static List<Map<String, String>> getAlbumInfo(Context ctx) {
        List<Map<String, String>> result = new ArrayList<>();
        Config.dbHelper = new MyDatabaseHelper(ctx, "Album.db", null, Config.dbversion);
        SQLiteDatabase db = Config.dbHelper.getWritableDatabase();
        Log.d("chen","getAlbumInfo");
        Cursor cursor = db.query("Album", null, null, null, null, null, null);
        Log.d("chen","cursor size="+cursor.getCount());
        if (cursor==null) Log.d("chen","cursor is null !!!!!!!!!!!!!");
        if (cursor.moveToFirst()) {
            Map<String, String> tmp;
            do {
                tmp = new HashMap<>();;
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
    /**
     * use tf to classify the image
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
