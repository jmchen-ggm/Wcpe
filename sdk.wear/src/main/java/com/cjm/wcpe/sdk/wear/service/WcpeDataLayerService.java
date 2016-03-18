package com.cjm.wcpe.sdk.wear.service;

import android.util.Log;
import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.util.WcpePathUtil;
import com.cjm.wcpe.sdk.wear.Wcpe;
import com.cjm.wcpe.sdk.wear.client.ClientCallbackEntity;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.MessageEvent;
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
    public void onMessageReceived(MessageEvent messageEvent) {
        LogUtil.v(TAG, "onMessageReceived");
        WcpePathUtil.Path path = WcpePathUtil.parser(messageEvent.getPath());
        if (path != null && path.from == WcpeProtocol.From.Phone) {
            ClientCallbackEntity entity = new ClientCallbackEntity();
            entity.path = path;
            entity.data = messageEvent.getData();
            Wcpe.getInstance().publish(entity);
        }
    }

    @Override
    public void onChannelOpened(Channel channel) {
        LogUtil.v(TAG, "onChannelOpened");
        WcpePathUtil.Path path = WcpePathUtil.parser(channel.getPath());
        if (path != null && path.from == WcpeProtocol.From.Wear && System.currentTimeMillis() - path.timestamp <= 5 * 1000) {
            if (path.connectType == WcpeProtocol.ConnectType.Short) {
                ClientCallbackEntity entity = new ClientCallbackEntity();
                entity.path = path;
                entity.channel = channel;
                Wcpe.getInstance().publish(entity);
            }
        }
    }
}
