package com.example.chenchen.newapplication.album;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;
import com.example.chenchen.newapplication.tensorflow.Classifier;
import com.example.chenchen.newapplication.tensorflow.Config;
import com.example.chenchen.newapplication.tensorflow.TensorFlowImageClassifier;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;

import java.util.List;

import static com.example.chenchen.newapplication.tensorflow.ImageDealer.do_tensorflow;
import static com.example.chenchen.newapplication.tensorflow.ImageDealer.insertImageIntoDB;

/**
 * Created by chenchen on 18-5-7.
 */

public class ClassiedOnePictureActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private String image;
    private ImageView imageView;
    private TextView textResult;
    private String result;
    private Classifier classifier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("chen", "enter 单张照片的分类页面");
        setContentView(R.layout.activity_classied_image);
        image = getIntent().getStringExtra("image_url");
        Log.d("chen", image);

        actionBar = getSupportActionBar();
        //没有左部返回键
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.album_image);
        textResult = (TextView) findViewById(R.id.text_result);

        Glide
                .with(ClassiedOnePictureActivity.this)
                .load(image)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .crossFade()
                .thumbnail(0.1f).into(imageView);
        textResult.setText("识别结果是：" + getClassiedResult());
    }

    private String getClassiedResult() {
        MyDatabaseHelper helper = new MyDatabaseHelper(ClassiedOnePictureActivity.this, Config.DB_NAME, null, Config.dbversion);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select album_name from AlbumPhotos where url=?", new String[]{image});
        //纸张
        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
            if (result == null&&image.contains("tmp")) {

                if (classifier == null) {
                    Log.d("chen", "classied one picture  classifier==null");
                    // get permission
                    try {
                        classifier = TensorFlowImageClassifier.create(getAssets(), Config.MODEL_FILE,
                                Config.LABEL_FILE, Config.INPUT_SIZE, Config.IMAGE_MEAN,
                                Config.IMAGE_STD, Config.INPUT_NAME, Config.OUTPUT_NAME);


                    } catch (Exception e) {
                        Log.d("chen", "mainActivity tensorflow load error!!!!!");
                    }
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmap = BitmapFactory.decodeFile(image, options);
                result = do_tensorflow(bitmap, classifier).get(0).getTitle();
//            insertImageIntoDB(image, do_tensorflow(bitmap, classifier), dbOperator, value);
            }
            Log.d("chen","result="+result);
            cursor.close();
            db.close();
        }

        return result;
    }

    // 这个两个参数的方法系统出错 关机。。。时执行
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle
            persistentState) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

