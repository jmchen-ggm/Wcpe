package com.cjm.wcpe.sample;

import android.app.Application;
import com.cjm.wcpe.sdk.wear.Wcpe;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Wcpe.create(this);
    }

    @Override
    public void onTerminate() {
        Wcpe.destroy();
        super.onTerminate();
    }
}
