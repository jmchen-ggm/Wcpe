package com.cjm.wcpe.sdk.connection;

import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;

import java.io.InputStream;

/**
 * Created by jiaminchen on 16/3/18.
 */
public interface IConnection {
    int sendMessage(String path, byte[] data);
    int sendMessage(String nodeId, String path, byte[] data);
    int sendData(String path, byte[] data);
    int sendData(String nodeId, String path, byte[] data);
    String getNodeId();
    int addChannelListener(Channel channel, ChannelApi.ChannelListener listener);
    int removeChannelListener(Channel channel, ChannelApi.ChannelListener listener);
    InputStream getChannelInputStream(Channel channel);
    int closeChannel(Channel channel);
}
