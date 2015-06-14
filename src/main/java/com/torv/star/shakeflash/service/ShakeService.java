package com.torv.star.shakeflash.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

import com.torv.star.shakeflash.util.Constants;
import com.torv.star.shakeflash.util.Lg;

public class ShakeService extends Service implements SensorEventListener {

    private final String tag = "ShakeService";

    private SensorManager mSensorManager = null;
    private Vibrator mVibrator = null;

    private final ShakeBinder mBinder = new ShakeBinder();

    private SharedPreferences mSharedPreferences;
    private Camera mCamera;

    private long mPreviousShakeTime = 0;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakelock;


    @Override
    public void onCreate() {
        super.onCreate();
        Lg.e(tag, "onCreate");

        if (null == mSensorManager) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        if (null == mVibrator) {
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        if (null == mSharedPreferences) {
            mSharedPreferences = getSharedPreferences(getApplication().getPackageName(), Activity.MODE_PRIVATE);
        }

        if (null == mPowerManager) {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakelock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ShakeServiceBackground");
            mWakelock.acquire();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Lg.e(tag, "onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Lg.e(tag, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Lg.e(tag, "onDestroy");
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        if (mWakelock != null) {
            mWakelock.release();
        }

        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Lg.e(tag, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onLowMemory() {
        Lg.e(tag, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        if (Math.abs(x) > Constants.SHAKE_THRESHOLD_VALUE
                || Math.abs(y) > Constants.SHAKE_THRESHOLD_VALUE
                || Math.abs(z) > Constants.SHAKE_THRESHOLD_VALUE) {

            long currentTime = System.currentTimeMillis();
            if (currentTime - mPreviousShakeTime < 1000) {
                return;
            }
            Lg.e(tag, "x="+x+",y="+y+",z="+z);
            mPreviousShakeTime = currentTime;

            mVibrator.vibrate(200);
            openTorch();
        }
    }

    private void openTorch() {
        boolean isOpened = mSharedPreferences.getBoolean(Constants.SP_KEY_IS_FLASH_OPENED, false);
        if (isOpened) {
            openTorch(false);
        } else {
            openTorch(true);
        }
    }

    private void openTorch(boolean open) {
        try {
            if (open) {

                if (null == mCamera) {
                    mCamera = Camera.open();
                }
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.startPreview();

                mSharedPreferences.edit().putBoolean(Constants.SP_KEY_IS_FLASH_OPENED, true).commit();

            } else {

                if (mCamera != null) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);
                    mCamera.release();
                    mCamera = null;
                }
                mSharedPreferences.edit().putBoolean(Constants.SP_KEY_IS_FLASH_OPENED, false).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Lg.e(tag, "onAccuracyChanged");
    }

    public class ShakeBinder extends Binder {

        public ShakeService getService() {
            return ShakeService.this;
        }
    }

    ;
}