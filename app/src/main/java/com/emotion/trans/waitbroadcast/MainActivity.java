package com.emotion.trans.waitbroadcast;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.trans.util.Effector;
import com.trans.util.EffectorListener;

public class MainActivity extends AppCompatActivity {

    private Effector mWifiEffector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiEffector = new Effector(this, WifiManager.WIFI_STATE_CHANGED_ACTION, 10000, new EffectorListener() {
            @Override
            public boolean isActiveState() {
                return ((WifiManager)getSystemService(WIFI_SERVICE)).isWifiEnabled();
            }

            @Override
            public boolean isRightBroadcast(Intent intent) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                return state == WifiManager.WIFI_STATE_ENABLED;
            }

            @Override
            public void preRegisterAction() {
                ((WifiManager)getSystemService(WIFI_SERVICE)).setWifiEnabled(true);
            }

            @Override
            public void handleProcess() {
                Log.d("trans", "-----handleProcess-----");
            }

            @Override
            public void handleTimeout() {
                Log.d("trans", "-----Timeout-----");
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiEffector.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiEffector.destroy();
    }
}
