package com.runningracehisotry.utilities;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by QuyNguyen on 5/3/2015.
 */
public class ImageLoaderMedal extends ImageLoader {

    private volatile static ImageLoaderMedal instance;

    /** Returns singletone class instance */
    public static ImageLoaderMedal getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoaderMedal();
                }
            }
        }
        return instance;
    }
}
