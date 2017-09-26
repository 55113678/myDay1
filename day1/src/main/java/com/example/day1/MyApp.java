package com.example.day1;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Administrator on 2017/9/26.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(30)
                .threadPriority(30)
                .diskCacheSize(1024*2)
                .build();
        ImageLoader.getInstance().init(configuration);

    }
}
