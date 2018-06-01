package com.example.chenchen.newapplication.album;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.LocaleDisplayNames;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;
import com.example.chenchen.newapplication.album.imageloader.ImageScanResult;
import com.example.chenchen.newapplication.album.imageloader.ImageScannerModel;
import com.example.chenchen.newapplication.album.imageloader.ImageScannerModelImpl;
import com.example.chenchen.newapplication.tensorflow.Classifier;
import com.example.chenchen.newapplication.tensorflow.Config;
import com.example.chenchen.newapplication.tensorflow.TensorFlowImageClassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.chenchen.newapplication.tensorflow.ImageDealer.do_tensorflow;
import static com.example.chenchen.newapplication.tensorflow.ImageDealer.insertImageIntoDB;

/**
 * Created by chenchen on 18-4-30.
 */

public class WelcomeActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE = 200;
    private MyDatabaseOperator dbOperator;
    List<String> notBeclassiedImageList;

    private Classifier classifier;
    private ContentValues value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        //请求读写权限
        if (Build.VERSION.SDK_INT >= 23) {
            // check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                // require permission for wr  系统自定义的dialog
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                }, PERMISSION_REQUEST_STORAGE);
            } else {
                do_prepare(WelcomeActivity.this);
            }
        } else {
            do_prepare(WelcomeActivity.this);
        }


    }

//

    private void do_prepare(Context context) {
        notBeclassiedImageList = new ArrayList<>();
        //内部操作数据库  里面有创建数据库 但是仅会执行一次
        MyDatabaseOperator operator = new MyDatabaseOperator(context, Config.DB_NAME, Config.dbversion);

        String url;

        ImageScannerModelImpl imageScannerModel = new ImageScannerModelImpl();
        //线程 加载照片
        imageScannerModel.startScanImage(context, getSupportLoaderManager(), new ImageScannerModel.OnScanImageFinish() {
            @Override
            public void onFinish(ImageScanResult imageScanResult) {
                Map<String, ArrayList<String>> findResult = imageScanResult.getAlbumInfo();
                Log.d("chen", "findResult size（文件夹的数目）=" + findResult.size()); //文件夹的数目
                Set<String> keyset = findResult.keySet();
                ArrayList<String> urlList;
                List<Map> search_result;
                List<String> find_folder = operator.searchAllFolderName();
                value = new ContentValues();
                for (String key : keyset) {
                    if (!key.equals("0")) {
                        Log.d("key", key);
                        urlList = findResult.get(key);
                        if (!find_folder.contains(key)) {
                            value.put("folder_name", key);
                            value.put("image", urlList.get(0));
                            operator.insert("AlbumFolder", value);
                            value.clear();
                        }
                        for (String url : urlList) {
                            search_result = operator.search(Config.ALBUM_NAME, "url = '" + url + "'");
                            if (key.equals("Download")) {
                                if (search_result.size() == 0) {
                                    notBeclassiedImageList.add(url);
                                }
                            } else {
                                if (search_result.size() == 0) {
                                    value.put("folder_name", key);
                                    value.put("url", url);
                                    Log.d("chen","新添加图片url="+url);
                                    operator.insert("AlbumPhotos", value);
                                    value.clear();
                                }
                            }
                        }
                    }

                }

                operator.close();

                classifyNewImages();
                Log.d("chen", "未处理的图片有" + notBeclassiedImageList.size());

            }
        });




    }

    //分类
    private void classifyNewImages() {

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                Looper.prepare();
                // 初始化训练模型
                if (classifier == null) {
                    try {
                        classifier = TensorFlowImageClassifier.create(getAssets(), Config.MODEL_FILE,
                                Config.LABEL_FILE, Config.INPUT_SIZE, Config.IMAGE_MEAN,
                                Config.IMAGE_STD, Config.INPUT_NAME, Config.OUTPUT_NAME);


                    } catch (Exception e) {
                        Log.d("chen", "mainActivity tensorflow load error!!!!!");
                    }
                }
                Bitmap bitmap;
                value = new ContentValues();
                if(dbOperator!=null) Log.d("chen","error dboperator is not null");
                dbOperator = new MyDatabaseOperator(WelcomeActivity.this, Config.DB_NAME, Config.dbversion);
                Log.d("chen","未分类图片"+notBeclassiedImageList.size());
                for (String url : notBeclassiedImageList) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                    bitmap = BitmapFactory.decodeFile(url, options);
                    insertImageIntoDB(url, do_tensorflow(bitmap, classifier), dbOperator, value);
                }
                dbOperator.close();
                Log.d("chen", "分类完毕");
                myHandler.sendEmptyMessage(10);
                Looper.loop();
            }
        }).start();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    classImage();
                    break;
                case 10:
                    do_finish();
                    break;
            }
        }
    };

    private void do_finish() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        };
        timer.schedule(task, 1000);
    }

    private void classImage() {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //动态请求权限，得到用户的响应之后
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                do_prepare(WelcomeActivity.this);
            } else {
                Toast.makeText(WelcomeActivity.this,
                        "对不起，不能访问存储卡我不能继续工作！",
                        Toast.LENGTH_LONG).show();
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        WelcomeActivity.this.finish();
                    }
                };
                timer.schedule(task, 1000 * 2);
            }
        }
    }


}
