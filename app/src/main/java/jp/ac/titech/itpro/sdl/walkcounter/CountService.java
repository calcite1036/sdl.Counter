package jp.ac.titech.itpro.sdl.walkcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.app.Service;

import static jp.ac.titech.itpro.sdl.walkcounter.MainActivity.TAG;

public class CountService extends Service implements SensorEventListener {
    SharedPreferences prefs;
    private SensorManager manager;
    private Sensor stepCounter;
    long prevSteps=0;


    protected void sendSteps(Long Steps){
        Intent broadcast = new Intent();
        broadcast.putExtra("Steps", Steps);
        broadcast.setAction("SEND_STEPS");
        getBaseContext().sendBroadcast(broadcast);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager == null) {
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show();
        }
        stepCounter = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter == null) {
            Toast.makeText(this, R.string.toast_no_stepcounter, Toast.LENGTH_LONG).show();
        }
        manager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);

        prefs = getSharedPreferences("Data",Context.MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Log.d("CountService","CountService Started");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SharedPreferences.Editor editor = prefs.edit();
        Sensor sensor = event.sensor;
        float[] values = event.values;
        long curSteps = 0;

        if(sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            Log.d("type_step_counter",String.valueOf(values[0]));
            if(prevSteps != 0){
                curSteps += (long) event.values[0] - prevSteps;
            }
            Log.d("CountService/prevSteps",String.valueOf(prevSteps));
            prevSteps = (long) event.values[0];
            Log.d("CountService/curSteps:",String.valueOf(curSteps));
            sendSteps(curSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: accuracy=" + accuracy);
    }

    @Override
    public void onDestroy() {
        manager.unregisterListener(this);
        super.onDestroy();
    }
}
