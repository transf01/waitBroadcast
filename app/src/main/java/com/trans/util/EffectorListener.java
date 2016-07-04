package com.trans.util;

import android.content.Intent;

/**
 * Created by trans on 2016-06-28.
 */
public interface EffectorListener {
    public boolean isActiveState();
    public boolean isRightBroadcast(Intent intent);
    public void preRegisterAction();
    public void handleTimeout();
    public void handleProcess();
}
