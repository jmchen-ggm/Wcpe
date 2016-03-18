package com.cjm.wcpe.sdk.wear.client;

import com.cjm.wcpe.sdk.util.WcpePathUtil;
import com.google.android.gms.wearable.Channel;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class ClientCallbackEntity {
    public WcpePathUtil.Path path;
    public Channel channel;
    public byte[] data;
}
