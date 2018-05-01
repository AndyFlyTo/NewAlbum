package com.example.chenchen.newapplication.album;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;
import com.example.chenchen.newapplication.album.imageloader.ImageScanResult;
import com.example.chenchen.newapplication.album.imageloader.ImageScannerModel;
import com.example.chenchen.newapplication.album.imageloader.ImageScannerModelImpl;
import com.example.chenchen.newapplication.tensorflow.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.ToDoubleBiFunction;

/**
 * Created by chenchen on 18-4-30.
 */

public class WelcomeActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE=200;
    private MyDatabaseOperator dbOperator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            }
            else {
                do_prepare(WelcomeActivity.this);
            }
        }
        else {
            do_prepare(WelcomeActivity.this);
        }

        // TODO: 18-4-30
//        handler.sendEmptyMessageDelayed(0,3000);
//        放在这里貌似是不对的  prepare函数中应该是

    }

//
//    private void prepare(){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                //使线程拥有自己的消息列队，主线程拥有自己的消息列队，一般线程创建时没有自己的消息列队，消息处理时就在主线程中完成
//                //如果线程中使用Looper.prepare()和Looper.loop()创建了消息队列就可以让消息处理在该线程中完成
//                do_prepare(WelcomeActivity.this);
//                Looper.loop();
//            }
//        }).start();
//    }

    private void do_prepare(Context context){
        List<String> notBeclassiedImageList=new ArrayList<>();
        //内部操作数据库  里面有创建数据库 但是仅会执行一次
        MyDatabaseOperator operator = new MyDatabaseOperator(context, Config.DB_NAME, Config.dbversion);
        //get all images
        // TODO: 18-4-30  需要得到所有的图片 查询是否被删除，是否存在为分类的
        //所以我现在需要去写数据库查询图片数据的接口

//        List<Map> imagelist=operator.getExternalImageInfo(context);
       //得到图片信息
        String  url;

        ImageScannerModelImpl imageScannerModel=new ImageScannerModelImpl();
        //这里就是开的线程 加载照片
        imageScannerModel.startScanImage(context, getSupportLoaderManager(), new ImageScannerModel.OnScanImageFinish() {
            @Override
            public void onFinish(ImageScanResult imageScanResult) {
                Log.d("chen","onFinish");
                Map<String,ArrayList<String>> findResult=imageScanResult.getAlbumInfo();
                Log.d("chen","findResult size（文件夹的数目）="+findResult.size()); //文件夹的数目
                Set<String> keyset=findResult.keySet();
                ArrayList<String> urlList;
                List<Map> search_result;
                for (String key: keyset){
                     urlList=findResult.get(key);
                    for(String url:urlList){
                        search_result=operator.search(Config.ALBUM_NAME,"url = '"+url+"'");
                        if(search_result.size()==0){
                            notBeclassiedImageList.add(url);
                        }
                    }
                }
                Log.d("chen","未处理的图片有"+notBeclassiedImageList.size());
                operator.close();
            }
        });



//        for(Map<String,String> image:imagelist){
//            url=image.get("_data");
//
//
//            //存在未处理的图片
//            if(findResult.size()==0){
//                notBeclassiedImageList.add(url);
//
//            }else{
//
//            }
//            if(notBeclassiedImageList.size()!=0){
//                if(notBeclassiedImageList.size()>20){
//                    // TODO: 18-5-1 转到MainActivity去处理
//                }else {
//                    myHandler.sendEmptyMessage(1);
//                    //handler.sendMessage()
//                    // TODO: 18-5-1
//                }
//            }else {
//
//            }
//
//        }


    }
    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    classImage();
            }
        }
    };
    private void classImage(){

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome(){
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
//                pbar.setVisibility(pbar.VISIBLE);
//                myHandler.sendEmptyMessage(0x3);
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
