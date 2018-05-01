package com.example.chenchen.newapplication.album;

import android.app.Application;

import com.example.chenchen.newapplication.album.imageloader.UniversalAndroidImageLoader;
import com.facebook.stetho.Stetho;

/**
 * Created by chenchen on 18-4-28.
 */

public class MyApplication extends Application {

        public void onCreate() {
            super.onCreate();
            Stetho.initializeWithDefaults(this);
            UniversalAndroidImageLoader.init(getApplicationContext());
        }
}



