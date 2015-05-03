package com.runningracehisotry.utilities;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * Created by QuyNguyen on 5/3/2015.
 */
public class ImageLoaderPerson  extends ImageLoader {

    private volatile static ImageLoaderPerson instance;

    /** Returns singletone class instance */
    public static ImageLoaderPerson getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoaderPerson();
                }
            }
        }
        return instance;
    }
}
