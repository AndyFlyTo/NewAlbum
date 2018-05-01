package com.example.chenchen.newapplication.album;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenchen.newapplication.BuildConfig;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.View.Fragment.AlbumFolderFragment;
import com.example.chenchen.newapplication.album.View.Fragment.AlbumFragment;

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private ActionBar actionBar;
    private BottomNavigationView navigation;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private AlbumFolderFragment albumFolderFragment;
    private AlbumFragment albumFragment;

    // for camera to save image
    private Uri content_uri;
    private File new_file;


    private static final int PERMISSION_CAMERA = 300;

    //底部栏点击宿事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction=fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.d("chen","navigation_home");
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setTitle("文件夹");
                    transaction.replace(R.id.content, new AlbumFolderFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    Log.d("chen","navigation_natifications");
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setTitle("照片");
                    transaction.replace(R.id.content, new AlbumFragment());
                    transaction.commit();
                    return true;

            }

            return false;
        }

    };
//    //隐藏所有Fragment
//    private void hideAllFragment(android.app.FragmentTransaction fragmentTransaction){
//        if(albumFolderFragment != null)fragmentTransaction.hide(albumFolderFragment);
//        if(albumFragment != null)fragmentTransaction.hide(albumFragment);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar=getSupportActionBar();
        //没有左部返回键
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("folder");

        fragmentManager=getSupportFragmentManager();



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
        switch (item.getItemId()){
            case R.id.action_camera:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{ Manifest.permission.CAMERA },
                                PERMISSION_CAMERA);
                    }
                    else {
                        startCamera();
                    }
                }
                else {
                    startCamera();
                }
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 打开相机获取图片  需要在xml中创建xml文件 指名拍照照片的存储路径
     */
    private void startCamera() {
        File imagePath = new File(Environment.getExternalStorageDirectory(), "tmp");
        if (!imagePath.exists()) imagePath.mkdirs();
        new_file = new File(imagePath, "default_image.jpg");
        //第二参数是在manifest.xml定义 provider的authorities属性
        //帮助我们将访问受限的 file:// URI 转化为可以授权共享的 content:// URI。
        content_uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider", new_file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //兼容版本处理，因为 intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) 只在5.0以上的版本有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip = ClipData.newUri(getContentResolver(), "A photo", content_uri);
            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
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
        startActivityForResult(intent, 1); //报错
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    
    
    //对新拍的图片分类处理
    private void dealPics(FileDescriptor fileDescriptor) {
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

        // TODO: 18-4-30 调分类的接口，分类。 



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


