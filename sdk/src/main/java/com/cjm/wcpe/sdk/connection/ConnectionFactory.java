package com.cjm.wcpe.sdk.connection;

import android.content.Context;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class ConnectionFactory {

    public final static IConnection createConnection(Context context) {
        return new GoogleApiClientConnection(context);
    }
}
