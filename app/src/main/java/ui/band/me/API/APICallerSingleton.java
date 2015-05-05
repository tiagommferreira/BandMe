package ui.band.me.API;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import ui.band.me.BandMeApplication;

/**
 * Created by Tiago on 05/05/2015.
 */
public class APICallerSingleton {
    private static APICallerSingleton sInstance = null;
    public static final String URL_SPOTIFY_SEARCH =  "https://api.spotify.com/v1/search";
    public static final String URL_SPOTIFY_ARTISTS =  "https://api.spotify.com/v1/artists/";


    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private APICallerSingleton() {
        mRequestQueue = Volley.newRequestQueue(BandMeApplication.getAppContext());
        mImageLoader = new ImageLoader(mRequestQueue,new ImageLoader.ImageCache() {
            private LruCache<String,Bitmap> cache = new LruCache<>((int) ((Runtime.getRuntime().maxMemory()/1024)/8));

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);
            }
        });
    }

    public static APICallerSingleton getsInstance() {
        if(sInstance == null) {
            sInstance = new APICallerSingleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
