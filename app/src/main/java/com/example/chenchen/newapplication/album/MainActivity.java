package com.example.chenchen.newapplication.album;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chenchen.newapplication.BuildConfig;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;
import com.example.chenchen.newapplication.album.View.Fragment.AlbumFolderFragment;
import com.example.chenchen.newapplication.album.View.Fragment.AlbumFragment;
import com.example.chenchen.newapplication.tensorflow.Classifier;
import com.example.chenchen.newapplication.tensorflow.Config;
import com.example.chenchen.newapplication.tensorflow.ImageDealer;
import com.example.chenchen.newapplication.tensorflow.TensorFlowImageClassifier;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.chenchen.newapplication.tensorflow.ImageDealer.do_tensorflow;

public class MainActivity extends AppCompatActivity {


    public static ActionBar actionBar;
    private BottomNavigationView navigation;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private AlbumFolderFragment albumFolderFragment;
    private AlbumFragment albumFragment;

    // for camera to save image
    private Uri content_uri;
    private File new_file;
    private MyDatabaseHelper helper;
    private Classifier classifier;
    private String result;


    private static final int PERMISSION_CAMERA = 300;

    //底部栏点击宿事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setTitle("文件夹");
                    transaction.replace(R.id.content, new AlbumFolderFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setTitle("照片");
                    transaction.replace(R.id.content, new AlbumFragment());
                    transaction.commit();
                    return true;

            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        //没有左部返回键
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("folder");

        fragmentManager = getSupportFragmentManager();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //模拟人手点击
        navigation.findViewById(R.id.navigation_home).performClick();
    }

    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //actionBar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_CAMERA);
                    } else {
                        startCamera();
                    }
                } else {
                    startCamera();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 打开相机获取图片  需要在xml中创建xml文件 指名拍照照片的存储路径   同时要跟新系统媒体库
     */
    private void startCamera() {
        File imagePath = new File(Environment.getExternalStorageDirectory(), "tmp");
        if (!imagePath.exists())
            imagePath.mkdirs();
        //按照时间来存储
        new_file = new File(imagePath, "default_image.jpg");
        //第二参数是在manifest.xml定义 provider的authorities属性
        //ContentProvider的子类FileProvider
        //第二参数是在manifest.xml定义 provider的authorities属性
        //帮助我们将访问受限的 file:// URI 转化为可以授权共享的 content:// URI。
        content_uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", new_file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        兼容版本处理，因为 intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) 只在5.0以上的版本有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            ClipData clip = ClipData.newUri(getContentResolver(), "A photo", content_uri);
            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            //将存储图片的uri读写权限授权给剪裁工具应用
            Log.d("chen", "----------------");
            List<ResolveInfo> resInfoList =
                    getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, content_uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        //将图片保存下来传给下面函数处理
        intent.putExtra(MediaStore.EXTRA_OUTPUT, content_uri);
        startActivityForResult(intent, 1);
    }

    // 接受拍照的结果
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ContentResolver contentProvider = getContentResolver();
                ParcelFileDescriptor mInputPFD;
                try {
                    //获取contentProvider图片
                    mInputPFD = contentProvider.openFileDescriptor(content_uri, "r");
                    final FileDescriptor fileDescriptor = mInputPFD.getFileDescriptor();

                    // new thread to deal image by tensorflow
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dealPics(fileDescriptor);
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // deal image by tensorflow
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void dealPics(FileDescriptor fileDescriptor) {
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        // init tensorflow
        if (classifier == null) {
            try {
                classifier = TensorFlowImageClassifier.create(getAssets(), Config.MODEL_FILE,
                        Config.LABEL_FILE, Config.INPUT_SIZE, Config.IMAGE_MEAN,
                        Config.IMAGE_STD, Config.INPUT_NAME, Config.OUTPUT_NAME);

            } catch (final Exception e) {
                Log.d("chen", "tensoflow error");
            }
        }


        Bitmap newbm = ImageDealer.dealImageForTF(bitmap);
        // 得到分类结果
        result = do_tensorflow(newbm, classifier).get(0).getTitle();

        // 存储图片
        String url = saveImage("", bitmap);
        Log.d("chen","new_url="+url);

        // 更新数据库
        if (helper == null)
            helper = new MyDatabaseHelper(MainActivity.this, Config.DB_NAME, null, Config.dbversion);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("folder_name", "temp");
        values.put("url", url);
        values.put("album_name", result);

        db.insert("AlbumPhotos", null, values);
        values.clear();

        Cursor cursor = db.rawQuery("select folder_name from AlbumFolder where folder_name=?", new String[]{"temp"});
        if (!cursor.moveToFirst()) {
            Log.d("chen", "cursor==" + null);
            values.put("folder_name", "temp");
            values.put("image", url);
            db.insert("AlbumFolder", null, values);
            values.clear();
        }
        cursor.close();
        db.close();


    }

    // save image
    private String saveImage(String type, Bitmap bitmap) {
        FileOutputStream b = null;
        // save images to this location
        File file = new File(Config.Save_Location);
        // 创建文件夹 @ Config.location
        if (!file.exists()) {
            Log.d("chen","file exit");
            file.mkdirs();
        }
        String str = null;
        Date date = null;
        // 获取当前时间，进一步转化为字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date();
        str = format.format(date);
        String fileName = Config.Save_Location + str + ".jpg";

        try {
            b = new FileOutputStream(fileName);

            // 把数据写入文件
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                File imagePath = new File(Environment.getExternalStorageDirectory(), "tmp");
                new_file = new File(imagePath, "default_image.jpg");
                new_file.delete();
                Log.d("chen", "Delete True");
            } catch (Exception e) {
                Log.e("chen", " delete Error");
            }
        }
        return fileName;
    }

    private MediaScannerConnection mMediaonnection;

    private void updateToMedia() {

        try {
            mMediaonnection = new MediaScannerConnection(MainActivity.this, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    mMediaonnection.disconnect();
                }
            });
            mMediaonnection.connect();
        } catch (Exception e) {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //获取拍照权限
        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(MainActivity.this,
                        "对不起，我需要相机的权限！",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}


