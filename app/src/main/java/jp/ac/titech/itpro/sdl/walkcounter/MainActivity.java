package jp.ac.titech.itpro.sdl.walkcounter;

import java.util.*;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    final static String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE = 1000;

    private long steps=0;

    SharedPreferences prefs;
    private BarChart barChart;
    private ArrayList<Long> x = new ArrayList<>();
    private ArrayList<Long> y = new ArrayList<>();

    ArrayList<BarEntry> entryList = new ArrayList<>();

    protected class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle extras = intent.getExtras();
            steps += extras.getLong("Steps");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE);
        if(savedInstanceState != null){
            x = (ArrayList<Long>) savedInstanceState.getSerializable("x");
            y = (ArrayList<Long>) savedInstanceState.getSerializable("y");
        }
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        prefs = getSharedPreferences("Data", Context.MODE_PRIVATE);

        UpdateReceiver receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SEND_STEPS");
        registerReceiver(receiver, filter);

        x.add((long)1);
        y.add((long)1);
        for(int i=0; i<x.toArray().length; i++) {
            entryList.add(new BarEntry(x.get(i), y.get(i)));
        }

        Log.d(TAG, "starting service");
        Intent intent=new Intent(getApplication(),CountService.class);
        startService(intent);

        BarDataSet barDataSet = new BarDataSet(entryList, "steps");
        barDataSet.setColor(Color.BLUE);

        BarData barData = new BarData(barDataSet);

        barChart = findViewById(R.id.barChartExample);
        barChart.setData(barData);

        barChart.setVisibleXRangeMaximum(60);
        barChart.getXAxis().setEnabled(true);
        barChart.getXAxis().setTextColor(Color.BLACK);

        Handler handler = new Handler();
        Runnable minStepCount = new Runnable(){
            private long min=0;
            public void run(){
                Log.d("cycle",String.valueOf(steps));
                BarData data = barChart.getData();
                IBarDataSet set = data.getDataSetByIndex(0);
                x.add(min);
                y.add(steps);
                data.addEntry(new BarEntry((float)min, (float)steps),0);
                min++;
                steps = 0;

                data.notifyDataChanged();
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                barChart.setVisibleXRangeMaximum(60);
                barChart.moveViewToX(data.getDataSetCount() - 61);
                handler.postDelayed(this,60000);
            }
        };
        handler.post(minStepCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: accuracy=" + accuracy);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable("x",x);
        outState.putSerializable("y",y);
    }

    @Override
    public void onDestroy(){
        Intent intent=new Intent(getApplication(),CountService.class);
        stopService(intent);
        super.onDestroy();
    }
}