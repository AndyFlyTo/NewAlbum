package com.example.chenchen.newapplication.album.imageloader;

import android.content.Context;
import android.support.v4.app.LoaderManager;



/**
 * 图片扫描Model层接口
 * <p/>
 * Created by chenchen on 18-4-20.
 */
public interface ImageScannerModel {

    /**
     * 获取所有图片的信息列表（图片目录的绝对路径作为map的key，value是该图片目录下的所有图片文件信息）
     *
     * @param context
     * @param loaderManager
     * @param onScanImageFinish 扫描图片结束返回结果的回调接口
     * @return
     */

    public void startScanImage(Context context,LoaderManager loaderManager,OnScanImageFinish onScanImageFinish);
    /**
     * 归档整理相册信息
     * @param imageScanResult
     * @return 整理好的相册目录信息
     */

    /**
    * 图片扫描结果回调接口
     */
    public static interface OnScanImageFinish {

        /**
         * 扫描结束的时候执行此函数
         * @param imageScanResult 返回扫描结果，不存在图片则返回null
         */
        public void onFinish(ImageScanResult imageScanResult);
    }
}
