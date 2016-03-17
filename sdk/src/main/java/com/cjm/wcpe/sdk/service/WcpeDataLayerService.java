package com.cjm.wcpe.sdk.service;

import com.cjm.wcpe.sdk.util.LogUtil;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by jiaminchen on 16/3/17.
 */
public class WcpeDataLayerService extends WearableListenerService {
    private final static String TAG = "Wcpe.WcpeDataLayerService";
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }
}
