package com.cjm.wcpe.sample.server;

import com.cjm.wcpe.sdk.mobile.server.BaseWcpeServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class TestShortServer extends BaseWcpeServer {
    private final static String TAG = "Wcpe.TestShortServer";
    public final static int TEST_SMALL_FUN_ID = 0x01000;
    public final static int TEST_LARGE_FUN_ID = 0x02000;

    @Override
    public List<Integer> getFunIdList() {
        ArrayList<Integer> funIdList = new ArrayList<>();
        funIdList.add(TEST_SMALL_FUN_ID);
        funIdList.add(TEST_LARGE_FUN_ID);
        return funIdList;
    }

    @Override
    public byte[] handleData(int funId, byte[] data) {
        switch (funId) {
            case TEST_LARGE_FUN_ID:
                return new byte[1024 * 4000];
            case  TEST_SMALL_FUN_ID:
                return "response test string".getBytes();
        }
        return null;
    }
}
