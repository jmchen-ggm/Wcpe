package com.cjm.wcpe.sdk.wear.client;

import android.content.Context;
import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.connection.ConnectionFactory;
import com.cjm.wcpe.sdk.connection.IConnection;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.util.Util;
import com.cjm.wcpe.sdk.util.WcpePathUtil;
import com.cjm.wcpe.sdk.wear.Wcpe;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpeShortClient implements IClientCallbackListener, ChannelApi.ChannelListener {

    private final static String TAG = "Wcpe.WcpeShortClient";
    private final static int MAX_MESSAGE_PACK_SIZE = 90 * 1024;
    private final static long DEFAULT_TIMEOUT = 10 * 1000;

    private IConnection connection;
    private Status status = Status.Init;
    private WcpeProtocol.From from;

    public WcpeShortClient(Context context, WcpeProtocol.From from) {
        this.connection = ConnectionFactory.createConnection(context);
        this.from = from;
    }

    private long timeout = DEFAULT_TIMEOUT;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    private WcpeShortReq req;
    private WcpeShortResp resp;

    public synchronized WcpeShortResp request(WcpeShortReq req) {
        this.req = req;
        resp = new WcpeShortResp();
        status = Status.Start;
        byte[] sendData = req.data;
        String nodeId = connection.getNodeId();
        if (Util.isNullOrNil(nodeId)) {
            resp.code = WcpeProtocol.WcpeCode.ERROR;
            return resp;
        }
        int contentLength = req.data == null ? 0 : req.data.length;
        String sendPath = WcpePathUtil.encode(nodeId, from, WcpeProtocol.ConnectType.Short,
            req.getSessionId(), req.funId, contentLength);
        LogUtil.i(TAG, "send data length = %d", sendData.length);
        int code;
        if (contentLength < MAX_MESSAGE_PACK_SIZE) {
            code = connection.sendMessage(sendPath, sendData);
        } else {
            code = connection.sendData(sendPath, sendData);
        }
        if (code != WcpeProtocol.WcpeCode.OK) {
            resp.code = code;
            return resp;
        }
        Wcpe.getInstance().addListener(req.funId, this);
        try {
            wait(timeout);
        } catch (InterruptedException e) {
        }
        Wcpe.getInstance().removeListener(req.funId, this);
        if (status == Status.Init) {
            status = Status.Timeout;
        }
        if (status == Status.Timeout) {
            resp.code = WcpeProtocol.WcpeCode.TIMEOUT_ERROR;
        } else if (status == Status.Cancel) {
            resp.code = WcpeProtocol.WcpeCode.CANCEL;
        } else {
            resp.code = WcpeProtocol.WcpeCode.OK;
        }
        return resp;
    }

    @Override
    public boolean callback(ClientCallbackEntity entity) {
        if (entity.path.sessionId == req.getSessionId() && entity.path.connectType == WcpeProtocol.ConnectType.Short) {
            resp.path = entity.path;
            resp.data = entity.data;
            resp.channel = entity.channel;
            status = Status.Resp;
            synchronized (this) {
                notify();
            }
        }
        return false;
    }

    private boolean isChannelClose;
    @Override
    public void onChannelOpened(Channel channel) {

    }

    @Override
    public void onChannelClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        isChannelClose = true;
    }

    @Override
    public void onInputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
        isChannelClose = true;
    }

    @Override
    public void onOutputClosed(Channel channel, int closeReason, int appSpecificErrorCode) {

    }

    enum Status {
        Init, Start, Resp, Timeout, Cancel
    }
}
