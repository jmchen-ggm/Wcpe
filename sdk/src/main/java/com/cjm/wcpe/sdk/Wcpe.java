package com.cjm.wcpe.sdk;

import android.util.SparseArray;
import com.cjm.wcpe.sdk.server.BaseWcpeServer;
import com.cjm.wcpe.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaminchen on 16/3/17.
 */
public class Wcpe {
    private final static String TAG = "Wcpe.Wcpe";
    private Wcpe() {

    }

    private static Wcpe sInstance = null;

    public static void create() {
        if (sInstance != null) {
            LogUtil.i(TAG, "already create");
            return;
        }
        sInstance = new Wcpe();
        sInstance.serverList = new ArrayList<>();
    }

    public static void destroy() {

    }

    private List<BaseWcpeServer> serverList;

    public void addServer(BaseWcpeServer server) {
        if (!serverList.contains(server)) {
            serverList.add(server);
        }
    }
}
