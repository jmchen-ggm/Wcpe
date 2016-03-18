package com.cjm.wcpe.sample;

import android.app.Application;
import com.cjm.wcpe.sample.server.TestShortServer;
import com.cjm.wcpe.sdk.mobile.Wcpe;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Wcpe.create(this);
        Wcpe.getInstance().bingServer(new TestShortServer());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Wcpe.destroy();
    }
}
