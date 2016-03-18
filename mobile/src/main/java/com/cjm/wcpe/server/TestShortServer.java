package com.cjm.wcpe.server;

import com.cjm.wcpe.sdk.mobile.server.BaseWcpeServer;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.util.WcpePathUtil;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class TestShortServer extends BaseWcpeServer {
    private final static String TAG = "Wcpe.TestShortServer";

    @Override
    public void handleRequest(WcpePathUtil.Path path, byte[] data) {
        LogUtil.i(TAG, "%s", new String(data));
    }
}
