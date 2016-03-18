package com.cjm.wcpe.sdk.mobile;

import android.content.Context;
import android.util.SparseArray;
import com.cjm.wcpe.sdk.mobile.server.BaseWcpeServer;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.worker.WcpeWorker;

import java.util.List;

/**
 * Created by jiaminchen on 16/3/17.
 */
public class Wcpe {
    private final static String TAG = "Wcpe.Wcpe";

    private Wcpe() {

    }
    private Context context;
    private static Wcpe sInstance = null;

    public static Wcpe getInstance() {
        return sInstance;
    }

    private WcpeWorker wcpeWorker;

    public static void create(Context context) {
        if (sInstance != null) {
            LogUtil.i(TAG, "already create");
            return;
        }
        sInstance = new Wcpe();
        sInstance.context = context.getApplicationContext();
        sInstance.serverFunArray = new SparseArray<>();
        sInstance.wcpeWorker = new WcpeWorker();
        sInstance.wcpeWorker.start();
    }

    public static void destroy() {
        if (sInstance != null) {
            sInstance.serverFunArray.clear();
            sInstance.wcpeWorker.stop();
        }
    }

    private SparseArray<BaseWcpeServer> serverFunArray;

    public void bingServer(BaseWcpeServer server) {
        List<Integer> funIds = server.getFunIdList();
        for (int funId : funIds) {
            LogUtil.i(TAG, "bind funId %d %s", funId, server);
            serverFunArray.put(funId, server);
        }
    }

    public BaseWcpeServer getFunServer(int funId) {
        return serverFunArray.get(funId);
    }

    public WcpeWorker getWcpeWorker() {
        return wcpeWorker;
    }

    public Context getContext() {
        return context;
    }
}
