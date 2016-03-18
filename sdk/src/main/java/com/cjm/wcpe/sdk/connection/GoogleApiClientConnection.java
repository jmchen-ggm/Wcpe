package com.cjm.wcpe.sdk.connection;

import android.content.Context;
import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class GoogleApiClientConnection implements IConnection {
    private final static String TAG = "Wcpe.GoogleApiClientConnection";
    private final static int DEFAULT_AWAIT_SECONDS = 2;
    private GoogleApiClient googleApiClient;
    private ReentrantLock lock;

    protected GoogleApiClientConnection(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        lock = new ReentrantLock();
    }

    private boolean checkConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        } else {
            ConnectionResult result = googleApiClient.blockingConnect(2, TimeUnit.SECONDS);
            if (result.isSuccess()) {
                return true;
            } else {
                LogUtil.e(TAG, "connect google api client error %d", result.getErrorCode());
                return false;
            }
        }
    }

    @Override
    public int sendMessage(String path, byte[] data) {
        if (!checkConnection()) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (nodes != null) {
            for (Node node : nodes.getNodes()) {
                results.add(node.getId());
            }
        }
        if (results.size() == 0) {
            return WcpeProtocol.WcpeCode.NO_CONNECT_NODE_ERROR;
        }
        for (String node : results) {
            int code = sendMessage(node, path, data);
            if (code != WcpeProtocol.WcpeCode.OK) {
                LogUtil.e(TAG, "send message to node error %s %d", node, code);
                return code;
            }
        }
        return  WcpeProtocol.WcpeCode.OK;
    }

    @Override
    public int sendMessage(String nodeId, String path, byte[] data) {
        if (!checkConnection()) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        // 默认2秒获得锁，如果超过2s没有获得锁，则返回错误
        try {
            lock.tryLock(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LogUtil.e(TAG, e);
            return WcpeProtocol.WcpeCode.LOCK_ERROR;
        }
        LogUtil.i(TAG, "send message item %s %s %d", nodeId, path, data == null ? 0 : data.length);
        Status status = Wearable.MessageApi.sendMessage(googleApiClient, nodeId, path,
            data).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS).getStatus();
        try {
            lock.unlock();
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
            return WcpeProtocol.WcpeCode.LOCK_ERROR;
        }
        if (!status.isSuccess()) {
            LogUtil.e(TAG, "send message not success errorCode=%d | errorMsg=%s", status.getStatusCode(), status.getStatusMessage());
            return WcpeProtocol.WcpeCode.ERROR;
        }
        return WcpeProtocol.WcpeCode.OK;
    }

    @Override
    public int sendData(String path, byte[] data) {
        if (data == null) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        if (!checkConnection()) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (nodes != null) {
            for (Node node : nodes.getNodes()) {
                results.add(node.getId());
            }
        }
        if (results.size() == 0) {
            return WcpeProtocol.WcpeCode.NO_CONNECT_NODE_ERROR;
        }
        for (String node : results) {
            int code = sendData(node, path, data);
            if (code != WcpeProtocol.WcpeCode.OK) {
                LogUtil.e(TAG, "send data to node error %s %d", node, code);
                return code;
            }
        }
        return  WcpeProtocol.WcpeCode.OK;
    }

    @Override
    public int sendData(String nodeId, String path, byte[] data) {
        if (data == null) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        if (!checkConnection()) {
            return WcpeProtocol.WcpeCode.ERROR;
        }

        // 默认2秒获得锁，如果超过2s没有获得锁，则返回错误
        try {
            lock.tryLock(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LogUtil.e(TAG, e);
            return WcpeProtocol.WcpeCode.LOCK_ERROR;
        }

        ChannelApi.OpenChannelResult openChannelResult = Wearable.ChannelApi.openChannel(googleApiClient, nodeId, path).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (!openChannelResult.getStatus().isSuccess()) {
            LogUtil.e(TAG, "open channel not success errorCode=%d | errorMsg=%s",
                openChannelResult.getStatus().getStatusCode(), openChannelResult.getStatus().getStatusMessage());
            return WcpeProtocol.WcpeCode.ERROR;
        }
        Channel channel =openChannelResult.getChannel();
        Channel.GetOutputStreamResult getOutputStreamResult = channel.getOutputStream(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (!getOutputStreamResult.getStatus().isSuccess()) {
            LogUtil.e(TAG, "open output stream not success errorCode=%d | errorMsg=%s",
                getOutputStreamResult.getStatus().getStatusCode(), getOutputStreamResult.getStatus().getStatusMessage());
            return WcpeProtocol.WcpeCode.ERROR;
        }
        OutputStream os = getOutputStreamResult.getOutputStream();
        try {
            os.write(data);
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return WcpeProtocol.WcpeCode.ERROR;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
            }
            channel.close(googleApiClient);
        }
        try {
            lock.unlock();
        } catch (Throwable e) {
            LogUtil.e(TAG, e);
            return WcpeProtocol.WcpeCode.LOCK_ERROR;
        }
        return WcpeProtocol.WcpeCode.OK;
    }

    @Override
    public String getNodeId() {
        if (!checkConnection()) {
            return null;
        }
        NodeApi.GetLocalNodeResult result = Wearable.NodeApi.getLocalNode(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (!result.getStatus().isSuccess()) {
            return null;
        } else {
            return result.getNode().getId();
        }
    }

    @Override
    public int addChannelListener(Channel channel, ChannelApi.ChannelListener listener) {
        if (!checkConnection()) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        Status status = channel.addListener(googleApiClient, listener).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (status.isSuccess()) {
            return WcpeProtocol.WcpeCode.OK;
        } else {
            return WcpeProtocol.WcpeCode.ERROR;
        }
    }

    @Override
    public int removeChannelListener(Channel channel, ChannelApi.ChannelListener listener) {
        if (!checkConnection()) {
            return WcpeProtocol.WcpeCode.ERROR;
        }
        Status status = channel.removeListener(googleApiClient, listener).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (status.isSuccess()) {
            return WcpeProtocol.WcpeCode.OK;
        } else {
            return WcpeProtocol.WcpeCode.ERROR;
        }
    }

    @Override
    public InputStream getChannelInputStream(Channel channel) {
        if (!checkConnection()) {
            return null;
        }
        Channel.GetInputStreamResult result = channel.getInputStream(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (result.getStatus().isSuccess()) {
            return result.getInputStream();
        } else {
            return null;
        }
    }

    @Override
    public int closeChannel(Channel channel) {
        Status status = channel.close(googleApiClient).await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        if (status.isSuccess()) {
            return WcpeProtocol.WcpeCode.OK;
        } else {
            return WcpeProtocol.WcpeCode.ERROR;
        }
    }
}
