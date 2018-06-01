package com.example.chenchen.newapplication.album.imageloader;

/**
 * ImageLoader工厂类
 * <p/>
 * Created by chenchen on 18-5-2.
 */
public class ImageLoaderFactory {

    private static volatile ImageLoaderWrapper sInstance;

    private ImageLoaderFactory() {

    }

    /**
     * 获取图片加载器
     *
     * @return
     */
    public static ImageLoaderWrapper getLoader() {
        if (sInstance == null) {
            synchronized (ImageLoaderFactory.class) {
                if (sInstance == null) {
                    sInstance = new UniversalAndroidImageLoader();
                    //<link>https://github.com/nostra13/Android-Universal-Image-Loader</link>
                }
            }
        }
        return sInstance;
    }
}
