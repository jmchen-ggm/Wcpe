package com.cjm.wcpe.sdk.wear.client;

import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.util.WcpePathUtil;
import com.google.android.gms.wearable.Channel;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpeShortResp {
    public int code = WcpeProtocol.WcpeCode.OK;
    WcpePathUtil.Path path;
    public byte[] data;
    public Channel channel;
}
