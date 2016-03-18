package com.cjm.wcpe.sdk.mobile.server;

import com.cjm.wcpe.sdk.util.WcpePathUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class BaseWcpeLongServer {

    public List<Integer> getFunIdList() {
        return new ArrayList<>();
    }

    /**
     * run in sub thread
     *
     * @param path
     * @param is
     */
    public void handleRequest(WcpePathUtil.Path path, InputStream is) {

    }

    private boolean isClose;

    public void close() {
        isClose = true;
    }

    public boolean isClose() {
        return isClose;
    }
}
