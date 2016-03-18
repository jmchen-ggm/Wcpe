package com.cjm.wcpe.sdk.wear.client;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpeShortReq {
    public static long sLastSessionId;
    private long sessionId;

    public WcpeShortReq() {
        sessionId = System.currentTimeMillis();
        if (sessionId == sLastSessionId) {
            sessionId++;
            sLastSessionId = sessionId;
        }
    }

    public long getSessionId() {
        return sessionId;
    }

    public int funId;
    public byte[] data;
}
