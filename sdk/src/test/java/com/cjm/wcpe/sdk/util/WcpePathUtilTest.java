package com.cjm.wcpe.sdk.util;

import com.cjm.wcpe.sdk.WcpeProtocol;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpePathUtilTest {
    private final static String TAG = "Wcpe.WcpePathUtilTest";

    @Test
    public void parser_isCorrect() throws Exception {
        String pathString = "/34872934uer/wear/short/1230987/1/200/123123123";
        WcpePathUtil.Path path = WcpePathUtil.parser(pathString);
        assertTrue(path.from == WcpeProtocol.From.Wear);
        assertTrue(path.connectType == WcpeProtocol.ConnectType.Short);
    }

    @Test
    public void encode_isCorrect() throws Exception {
        String path = WcpePathUtil.encode("34872934uer", WcpeProtocol.From.Wear, WcpeProtocol.ConnectType.Short, 1230987, 1, 200);
        assertTrue(path.startsWith("/34872934uer/wear/short/1230987/1/200/"));
    }
}
