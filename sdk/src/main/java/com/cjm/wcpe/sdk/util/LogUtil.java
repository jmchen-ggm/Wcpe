package com.cjm.wcpe.sdk.util;

import android.util.Log;

/**
 * Created by jiaminchen on 16/3/17.
 */
public class LogUtil {

    public final static void v(String tag, String format, Object... args) {
        String log = String.format(format, args);
        Log.v(tag, log);
    }

    public final static void d(String tag, String format, Object... args) {
        String log = String.format(format, args);
        Log.d(tag, log);
    }

    public final static void i(String tag, String format, Object... args) {
        String log = String.format(format, args);
        Log.i(tag, log);
    }

    public final static void w(String tag, String format, Object... args) {
        String log = String.format(format, args);
        Log.w(tag, log);
    }

    public final static void e(String tag, Throwable e) {
        Log.e(tag, "", e);
    }

    public final static void e(String tag, Throwable e, String format, Object... args) {
        String log = String.format(format, args);
        Log.e(tag, log, e);
    }

    public final static void e(String tag, String format, Object... args) {
        String log = String.format(format, args);
        Log.e(tag, log);
    }
}
