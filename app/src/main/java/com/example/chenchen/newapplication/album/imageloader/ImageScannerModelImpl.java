package com.example.chenchen.newapplication.album.imageloader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenchen on 18-4-20.
 */
public class ImageScannerModelImpl implements ImageScannerModel {

    private final static String TAG = ImageScannerModelImpl.class.getSimpleName();
    /**
     * Loader的唯一ID号
     */
    private final static int IMAGE_LOADER_ID = 1000;
    /**
     * 加载数据的映射
     */
    private final static String[] IMAGE_PROJECTION = new String[]{
            //mediaStore 利用它检索图像，而且作为内容提供器，也是以数据库的形式来进行操作的。
            //与对sqlite操作不同的是，他是根据uri（权限和路径组成）来进行操作
            MediaStore.Images.Media.DATA,//图片路径
            MediaStore.Images.Media.DISPLAY_NAME,//图片文件名，包括后缀名
            MediaStore.Images.Media.TITLE//图片文件名，不包含后缀
    };

    private OnScanImageFinish mOnScanImageFinish;

    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("chen","handle Album");
            ImageScanResult imageScanResult = (ImageScanResult) msg.obj;
            if(mOnScanImageFinish==null){
                Log.d("chen","on ScanImageFinish==null");
            }
            if(imageScanResult==null){
                Log.d("chen","result is null");
            }
            if (mOnScanImageFinish != null && imageScanResult != null) {
                Log.d("chen","handle Image onFinish");
                mOnScanImageFinish.onFinish(imageScanResult);
            }
        }
    };
    @Override
    public void startScanImage(Context context, LoaderManager loaderManager, OnScanImageFinish onScanImageFinish) {
        mOnScanImageFinish = onScanImageFinish;

        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Log.d("chen", "-----onCreateLoader-----");
                CursorLoader imageCursorLoader = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
                return imageCursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.d("chen","data count="+data.getCount());
                if (data.getCount() == 0) {
                    if (onScanImageFinish != null) {
                        onScanImageFinish.onFinish(null);//无图片直接返回null
                    }

                } else {
                    int dataColumnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA);
//                    Log.d("chen", "dataColumnIndex=" + dataColumnIndex);
//                    Log.d("chen", "media...data=" + MediaStore.Images.Media.DATA);   //_data
//                    Log.d("chen", "media...title=" + MediaStore.Images.Media.TITLE);  //title
//                    Log.d("chen", "media...display_name=" + MediaStore.Images.Media.DISPLAY_NAME); //_display_name
                    //int displayNameColumnIndex = data.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    //int titleColumnIndex = data.getColumnIndex(MediaStore.Images.Media.TITLE);
//                    ArrayList<File> albumFolderList = new ArrayList<>();
//                    HashMap<String, ArrayList<File>> albumImageListMap = new HashMap<>();
                    Map<String, String> item;
//
                    String folder_name;
                    String[] folder;

                    Map<String, ArrayList<String>> result = new HashMap<>();
                    while (data.moveToNext()) {

//                        item = new HashMap<>();
                        String columnValue = data.getString(dataColumnIndex);
//                        item.put(MediaStore.Images.Media.DATA, columnValue);
                        //columnValue is url
                        File imageFile = new File(data.getString(dataColumnIndex));//图片文件
                        File albumFolder = imageFile.getParentFile();//图片目录
                        String albumPath = albumFolder.getAbsolutePath();   //绝对路径 文件夹路径
                        // /storage/emulated/0/DCIM/Screenshots
                        // /storage/emulated/0/Tencent/QQ_Images .......
                        folder = albumPath.split("\\/");
                        folder_name = folder[folder.length - 1];

//                        Log.d("chen", "folder_name=" + folder_name);
                        ArrayList<String> albumImageList = result.get(folder_name);
                        if (albumImageList == null) {
                            albumImageList = new ArrayList<>();
                            result.put(folder_name, albumImageList);
                        }
                        //添加到对应的相册目录下面
                        albumImageList.add(columnValue);


                    }

                    ImageScanResult imageScanResult = new ImageScanResult();
                    imageScanResult.setAlbumInfo(result);

                    Message message = mRefreshHandler.obtainMessage();
                    message.obj = imageScanResult;
                    mRefreshHandler.sendMessage(message);
                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                Log.d("chen", "-----onLoaderReset-----");
            }
        };
        loaderManager.initLoader(IMAGE_LOADER_ID, null, loaderCallbacks);//初始化指定id的Loader
    }


    /**
     * 按照文件的修改时间进行排序，越最近修改的，排得越前
     */
    private void sortByFileLastModified(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.lastModified() > rhs.lastModified()) {
                    return -1;
                } else if (lhs.lastModified() < rhs.lastModified()) {
                    return 1;
                }
                return 0;
            }
        });
    }


}
