package com.torv.star.shakeflash;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.torv.star.shakeflash.service.ShakeService;
import com.torv.star.shakeflash.util.Constants;
import com.torv.star.shakeflash.util.Lg;
import com.torv.star.shakeflash.util.Util;


public class ShakeActivity extends Activity {

    private final String tag = "ShakeActivity";

    private boolean isRunning;

    private Button mBtnStartService;
    private Button mBtnStopService;
    private TextView mTvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        Lg.d(tag, "onCreate");

        initViews();

    }

    private void initViews() {

        mBtnStartService = (Button)findViewById(R.id.btn_start_service);
        mBtnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });

        mBtnStopService = (Button)findViewById(R.id.btn_stop_service);
        mBtnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        mTvStatus = (TextView)findViewById(R.id.tv_service_status);
    }

    public void startService(){
        startService(new Intent(this, ShakeService.class));
        setServiceIsRunning(true);
    }

    public void stopService(){
        stopService(new Intent(this, ShakeService.class));
        setServiceIsRunning(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Lg.d(tag, "onStart");

        if (Constants.USE_BINDER_SERVICE) {
            bindService(new Intent(this, ShakeService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Lg.d(tag, "onResume");

        isRunning = Util.isServiceRunning(getApplicationContext(), ShakeService.class);
        setServiceIsRunning(isRunning);
    }

    private void setServiceIsRunning(boolean isRunning) {
        if(isRunning){
            mTvStatus.setText(R.string.service_is_running);
            mBtnStartService.setEnabled(false);
            mBtnStopService.setEnabled(true);
        }else{
            mTvStatus.setText(R.string.service_is_not_running);
            mBtnStartService.setEnabled(true);
            mBtnStopService.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        Lg.d(tag, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Lg.d(tag, "onStop");

        if (Constants.USE_BINDER_SERVICE) {
            unbindService(mServiceConnection);
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Lg.d(tag, "onDestroy");
        super.onDestroy();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Lg.d(tag, "onServiceConnected");
            ShakeService.ShakeBinder binder = (ShakeService.ShakeBinder) iBinder;
            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Lg.d(tag, "onServiceDisconnected");
        }
    };
}