package ui.band.me;

import android.app.Application;
import android.content.Context;

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
