package com.trans.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

/**
 * Created by trans on 2016-06-27.
 */

public class Effector {

    private Context mContext;
    private InnerBroadcastReceiver mReceiver;
    private String mAction;
    private EffectorListener mListener;
    private long mTimeoutMillis;
    private Handler mHandler = new Handler();
    private Runnable mTimeoutRunnable;

    public Effector(Context context, String action, long timeoutMillis, EffectorListener listener) throws NullPointerException{
        if (listener == null) {
            throw new NullPointerException("Listener should be exist");
        }
        mContext = context;
        mAction = action;
        mTimeoutMillis = timeoutMillis;
        mListener = listener;
    }

    public void start() {
        if (mListener.isActiveState()) {
            mListener.handleProcess();
        } else {
            clear();
            mListener.preRegisterAction();
            registerAction();
            startTimeoutHandler();
        }
    }

    private void clear() {
        unregisterAction();
        cancelTimeout();
    }

    public void destroy() {
        clear();
    }

    private void registerAction() {
        mReceiver = new InnerBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(mAction);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterAction() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

    }

    private void startTimeoutHandler() {
        if (mTimeoutMillis < 0) {
            return;
        }
        mTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                unregisterAction();
                mListener.handleTimeout();
            }
        };
        mHandler.postDelayed(mTimeoutRunnable, mTimeoutMillis);
    }

    private void cancelTimeout() {
        if (mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mTimeoutRunnable = null;
        }
    }

    private class InnerBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAction.equals(intent.getAction())) {
                if (mListener.isRightBroadcast(intent)) {
                    cancelTimeout();
                    unregisterAction();
                    mListener.handleProcess();
                }
            }
        }
    }

}
