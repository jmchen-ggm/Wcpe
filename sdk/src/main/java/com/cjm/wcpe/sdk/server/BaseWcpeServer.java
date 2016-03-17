package com.cjm.wcpe.sdk.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaminchen on 16/3/17.
 */
public abstract class BaseWcpeServer {

    public List<Integer> getFunIdList() {
        return new ArrayList<>();
    }

    public void handleRequest() {

    }
}
