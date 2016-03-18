package com.cjm.wcpe.sdk.mobile.server;

import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.connection.ConnectionFactory;
import com.cjm.wcpe.sdk.connection.IConnection;
import com.cjm.wcpe.sdk.mobile.Wcpe;
import com.cjm.wcpe.sdk.util.WcpePathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaminchen on 16/3/17.
 */
public abstract class BaseWcpeServer {

    public List<Integer> getFunIdList() {
        return new ArrayList<>();
    }

    public final void handleRequest(WcpePathUtil.Path path, byte[] data) {
        byte[] responseData = handleData(path.funId, data);
        IConnection connection = ConnectionFactory.createConnection(Wcpe.getInstance().getContext());
        int contentLength = responseData == null? 0 : responseData.length;
        String sendPath = WcpePathUtil.encode(path.nodeId, WcpeProtocol.From.Phone, path.connectType,
            path.sessionId, path.funId, contentLength);
        if (contentLength < WcpeProtocol.MAX_MESSAGE_PACK_SIZE) {
            connection.sendMessage(sendPath, data);
        } else {
            connection.sendData(sendPath, data);
        }
    }

    public abstract byte[] handleData(int funId, byte[] data);
}
