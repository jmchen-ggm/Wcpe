package com.cjm.wcpe.sdk.util;

import com.cjm.wcpe.sdk.WcpeProtocol;

/**
 * /[NodeID]/[From]/[ConnectType]/[SessionId]/[FunId]/[ContentLength]/[TimeStamp]
 *
 * Created by jiaminchen on 16/3/18.
 */
public class WcpePathUtil {

    public final static class Path {
        private Path() {

        }
        public String nodeId;
        public WcpeProtocol.From from;
        public WcpeProtocol.ConnectType connectType;
        public long sessionId;
        public int funId;
        public int contentLength;
        public long timestamp;

        private String originPath;

        @Override
        public String toString() {
            return originPath;
        }
    }

    public final static Path parser(String pathString) {
        if (Util.isNullOrNil(pathString)) {
            return null;
        }
        String[] pathSession = pathString.split("/");
        if (pathSession == null || pathSession.length != 8) {
            return null;
        }
        Path path = new Path();
        path.originPath = pathString;
        if (!Util.isNullOrNil(pathSession[1])) {
            path.nodeId = pathSession[1];
        } else {
            return null;
        }

        if (pathSession[2].equalsIgnoreCase(WcpeProtocol.From.Wear.toString())) {
            path.from = WcpeProtocol.From.Wear;
        } else if (pathSession[2].equalsIgnoreCase(WcpeProtocol.From.Phone.toString())) {
            path.from = WcpeProtocol.From.Phone;
        } else {
            return null;
        }
        if (pathSession[3].equalsIgnoreCase(WcpeProtocol.ConnectType.Short.toString())) {
            path.connectType = WcpeProtocol.ConnectType.Short;
        } else if (pathSession[3].equalsIgnoreCase(WcpeProtocol.ConnectType.Push.toString())) {
            path.connectType = WcpeProtocol.ConnectType.Push;
        } else if (pathSession[3].equalsIgnoreCase(WcpeProtocol.ConnectType.Long.toString())) {
            path.connectType = WcpeProtocol.ConnectType.Long;
        } else {
            return null;
        }
        try {
            path.sessionId = Long.parseLong(pathSession[4]);
            path.funId = Integer.parseInt(pathSession[5]);
            path.contentLength = Integer.parseInt(pathSession[6]);
            path.timestamp = Long.parseLong(pathSession[7]);
        } catch (Exception e) {
            return null;
        }
        return path;
    }

    public final static String encode(String nodeId, WcpeProtocol.From from, WcpeProtocol.ConnectType connectType,
                                          long sessionId, int funId, int contentLength) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("/");
        buffer.append(nodeId);
        buffer.append("/");
        buffer.append(from.toString().toLowerCase());
        buffer.append("/");
        buffer.append(connectType.toString().toLowerCase());
        buffer.append("/");
        buffer.append(sessionId);
        buffer.append("/");
        buffer.append(funId);
        buffer.append("/");
        buffer.append(contentLength);
        buffer.append("/");
        buffer.append(System.currentTimeMillis());
        return buffer.toString();
    }
}
