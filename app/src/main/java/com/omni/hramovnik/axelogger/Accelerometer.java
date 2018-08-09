package com.omni.hramovnik.axelogger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer implements SensorEventListener {
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];
        setValues(x,y,z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float lastX;
    private float lastY;
    private float lastZ;

    private synchronized void setValues(float x, float y, float z){
        lastX = x;
        lastY = y;
        lastZ = z;
    }

    public synchronized float getX(){
        return lastX;
    }
    public synchronized float getY(){
        return lastY;
    }
    public synchronized float getZ(){
        return lastZ;
    }
}
