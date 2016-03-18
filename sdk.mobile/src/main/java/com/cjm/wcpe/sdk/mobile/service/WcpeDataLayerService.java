package com.cjm.wcpe.sdk.mobile.service;

import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.mobile.Wcpe;
import com.cjm.wcpe.sdk.mobile.server.BaseWcpeServer;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.util.WcpePathUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiaminchen on 16/3/17.
 */
public class WcpeDataLayerService extends WearableListenerService {
    private final static String TAG = "Wcpe.WcpeDataLayerService";
    private final static int DEFAULT_AWAIT_SECONDS = 2;

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
        WcpePathUtil.Path path = WcpePathUtil.parser(messageEvent.getPath());
        if (path != null && path.from == WcpeProtocol.From.Wear && System.currentTimeMillis() - path.timestamp <= 5 * 1000) {
            MessageTask messageTask = new MessageTask();
            messageTask.path = path;
            messageTask.data = messageEvent.getData();
            Wcpe.getInstance().getWcpeWorker().postTask(messageTask);
        }
    }

    @Override
    public void onChannelOpened(Channel channel) {
        WcpePathUtil.Path path = WcpePathUtil.parser(channel.getPath());
        if (path != null && path.from == WcpeProtocol.From.Wear && System.currentTimeMillis() - path.timestamp <= 5 * 1000) {
            if (path.connectType == WcpeProtocol.ConnectType.Short) {
                ChannelTask channelTask = new ChannelTask();
                channelTask.path = path;
                channelTask.channel = channel;
                Wcpe.getInstance().getWcpeWorker().postTask(channelTask);
            } else if (path.connectType == WcpeProtocol.ConnectType.Long) {

            }
        }
    }

    private class ChannelTask implements Runnable, ChannelApi.ChannelListener {
        WcpePathUtil.Path path;
        Channel channel;

        boolean isClosed;

        @Override
        public void run() {
            ByteArrayOutputStream os = new ByteArrayOutputStream(path.contentLength);
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(Wcpe.getInstance().getContext()).addApi(Wearable.API).build();
            ConnectionResult result = googleApiClient.blockingConnect(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
            if (!result.isSuccess()) {
                LogUtil.e(TAG, "errorCode %d errorMsg %s", result.getErrorCode(), result.getErrorMessage());
            } else {
                Status status = channel.addListener(googleApiClient, this).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
                if (!status.isSuccess()) {
                    return;
                }
                InputStream is = channel.getInputStream(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS).getInputStream();
                if (is == null) {
                    LogUtil.e(TAG, "InputStream is null");
                } else {
                    byte[] buffer = new byte[1024];
                    while (!isClosed) {
                        try {
                            int read = is.read(buffer);
                            if (read > 0) {
                                os.write(buffer, 0, read);
                            }
                        } catch (IOException e) {
                        }
                    }
                    BaseWcpeServer server = Wcpe.getInstance().getFunServer(path.funId);
                    if (server != null) {
                        server.handleRequest(path, os.toByteArray());
                    }
                }
                channel.removeListener(googleApiClient, this).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);;
            }
        }

        @Override
        public void onChannelOpened(Channel channel) {

        }

        @Override
        public void onChannelClosed(Channel channel,  int closeReason, int appSpecificErrorCode) {
            if (channel.getPath().equals(channel.getPath())) {
                isClosed = true;
            }
        }

        @Override
        public void onInputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
            if (channel.getPath().equals(channel.getPath())) {
                isClosed = true;
            }
        }

        @Override
        public void onOutputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {

        }
    }

    private class MessageTask implements Runnable {
        WcpePathUtil.Path path;
        byte[] data;

        @Override
        public void run() {
            BaseWcpeServer server = Wcpe.getInstance().getFunServer(path.funId);
            if (server != null) {
                server.handleRequest(path, data);
            }
        }
    }
}
