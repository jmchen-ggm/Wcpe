package com.cjm.wcpe.sdk.wear;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.wear.client.ClientCallbackEntity;
import com.cjm.wcpe.sdk.wear.client.IClientCallbackListener;
import com.cjm.wcpe.sdk.worker.WcpeWorker;

import java.util.LinkedList;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class Wcpe {
    private final static String TAG = "Wcpe";
    private final SparseArray<LinkedList<IClientCallbackListener>> listenersMap = new SparseArray<>();

    public boolean addListener(final int funId, final IClientCallbackListener listener) {
        boolean result;
        synchronized(this) {
            LogUtil.v(TAG, "addListener funId=%d", funId);
            LinkedList<IClientCallbackListener> container = listenersMap.get(funId);
            if (container == null) {
                listenersMap.put(funId, container = new LinkedList<>());
            }
            for (IClientCallbackListener l : container) {
                if (l == listener) {
                    return true;
                }
            }
            result = container.add(listener);
        }
        return result;
    }

    public boolean removeListener(final int funId, final IClientCallbackListener listener) {
        boolean result;
        synchronized(this) {
            LogUtil.v(TAG, "removeListener funId=%d", funId);
            LinkedList<IClientCallbackListener> container = listenersMap.get(funId);
            if (container == null) {
                return false;
            }
            result = container.remove(listener);
        }
        return result;
    }

    public boolean publish(final ClientCallbackEntity entity) {
        LogUtil.v(TAG, "publish funId=%d", entity.path.funId);
        LinkedList<IClientCallbackListener> resultListeners;
        synchronized (this) {
            LinkedList<IClientCallbackListener> listeners = listenersMap.get(entity.path.funId);
            if (listeners == null) {
                LogUtil.w(TAG, "No listener for this event %d.", entity.path.funId);
                return false;
            }
            resultListeners = new LinkedList<>();
        }
        trigger(resultListeners, entity);
        return true;
    }

    public void asyncPublish(final ClientCallbackEntity entity, final Looper looper) {
        LogUtil.v(TAG, "asyncPublish funId=%d", entity.path.funId);
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                publish(entity);
            }
        });
    }

    private void trigger(final LinkedList<IClientCallbackListener> listeners, final ClientCallbackEntity event) {
        IClientCallbackListener[] lstArr = new IClientCallbackListener[listeners.size()];
        listeners.toArray(lstArr);
        for (IClientCallbackListener listener : lstArr) {
            if (listener.callback(event)) {
                break;
            }
        }
    }

    private Context context;
    private static Wcpe sInstance;
    private WcpeWorker wcpeWorker;

    public static Wcpe getInstance() {
        return sInstance;
    }

    public static void create(Context context) {
        if (sInstance != null) {
            LogUtil.i(TAG, "already create");
            return;
        }
        sInstance = new Wcpe();
        sInstance.context = context.getApplicationContext();
        sInstance.wcpeWorker = new WcpeWorker();
        sInstance.wcpeWorker.start();
    }

    public static void destroy() {
        if (sInstance != null) {
            sInstance.context = null;
            sInstance.wcpeWorker.stop();
            sInstance.wcpeWorker = null;
        }
    }

    public WcpeWorker getWcpeWorker() {
        return wcpeWorker;
    }

    public Context getContext() {
        return context;
    }
}
