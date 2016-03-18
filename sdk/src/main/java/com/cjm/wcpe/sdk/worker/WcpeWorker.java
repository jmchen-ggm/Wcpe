package com.cjm.wcpe.sdk.worker;

import android.content.Context;
import com.cjm.wcpe.sdk.util.LogUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by jiaminchen on 16/3/18.
 */
public class WcpeWorker implements Runnable {
    private final static String TAG = "Wcpe.WcpeWorker";
    private BlockingQueue<Runnable> taskQueue;

    private boolean running;

    public WcpeWorker() {
        taskQueue = new LinkedBlockingDeque<>();
    }

    public boolean postTask(Runnable runnable) {
        return taskQueue.add(runnable);
    }

    public void stop() {
        if (!running) {
            return;
        }
        taskQueue.clear();
        running = false;
        taskQueue.add(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "stop task");
            }
        });
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        Thread t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @Override
    public void run() {
        while(running) {
            Runnable runnable = null;
            try {
                runnable = taskQueue.take();
            } catch (Exception e) {
            }
            if (runnable != null) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    LogUtil.e(TAG, e);
                }
            }
        }
        LogUtil.i(TAG, "stop wcpe worker");
    }
}
