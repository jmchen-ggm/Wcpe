package com.cjm.wcpe.sdk;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpeProtocol {
    public enum From {
        Wear, Phone
    }
    public enum ConnectType {
        Short, Long, Push
    }


    public final static class WcpeCode {
        public final static int CANCEL = 1;
        public final static int OK = 0;
        public final static int ERROR = -1;

        public final static int LOCK_ERROR = 0x10000100;
        public final static int NO_CONNECT_NODE_ERROR = 0x10000200;
        public final static int TIMEOUT_ERROR = 0x10000300;
    }
}
