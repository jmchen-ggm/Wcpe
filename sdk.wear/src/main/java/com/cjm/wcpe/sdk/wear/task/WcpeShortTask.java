package com.cjm.wcpe.sdk.wear.task;

import android.os.Handler;
import android.os.Looper;
import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.wear.R;
import com.cjm.wcpe.sdk.wear.Wcpe;
import com.cjm.wcpe.sdk.wear.client.WcpeShortClient;
import com.cjm.wcpe.sdk.wear.client.WcpeShortReq;
import com.cjm.wcpe.sdk.wear.client.WcpeShortResp;


/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpeShortTask implements Runnable {

    private final static String TAG = "Wcpe.WcpeShortTask";

    public interface Listener {
        void onEnd(WcpeShortReq req, WcpeShortResp resp);
    }

    private WcpeShortReq req;
    public WcpeShortTask(WcpeShortReq req) {
        this.req = req;
    }

    private Listener listener;
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        WcpeShortClient client = new WcpeShortClient(Wcpe.getInstance().getContext(), WcpeProtocol.From.Wear);
        WcpeShortResp resp = client.request(req);
        Callback callback = new Callback();
        callback.resp = resp;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(callback);
    }

    private class Callback implements Runnable {
        WcpeShortResp resp;

        @Override
        public void run() {
            if (listener != null) {
                listener.onEnd(req, resp);
            }
        }
    }
}
