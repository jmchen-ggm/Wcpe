package com.cjm.wcpe.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.cjm.wcpe.sdk.WcpeProtocol;
import com.cjm.wcpe.sdk.util.LogUtil;
import com.cjm.wcpe.sdk.wear.Wcpe;
import com.cjm.wcpe.sdk.wear.client.WcpeShortReq;
import com.cjm.wcpe.sdk.wear.client.WcpeShortResp;
import com.cjm.wcpe.sdk.wear.task.WcpeShortTask;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class SampleUI extends Activity implements WcpeShortTask.Listener {
    private final static String TAG = "Wcpe.SampleUI";
    public final static int TEST_FUN_ID = 0x01000;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_ui);
        button = (Button) findViewById(R.id.sampleBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WcpeShortReq req = new WcpeShortReq();
                req.funId = TEST_FUN_ID;
                req.data = "test.string".getBytes();
                WcpeShortTask wcpeShortTask = new WcpeShortTask(req);
                wcpeShortTask.setListener(SampleUI.this);
                Wcpe.getInstance().getWcpeWorker().postTask(wcpeShortTask);
            }
        });
    }

    @Override
    public void onEnd(WcpeShortReq req, WcpeShortResp resp) {
        if (resp.code == WcpeProtocol.WcpeCode.OK) {
            LogUtil.i(TAG, "onEnd %s", new String(resp.data));
        }
    }
}
