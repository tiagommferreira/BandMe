package ui.band.me;

import android.app.Application;
import android.content.Context;

/**
 * Created by Tiago on 05/05/2015.
 */
public class BandMeApplication extends Application {
    private static BandMeApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static BandMeApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
