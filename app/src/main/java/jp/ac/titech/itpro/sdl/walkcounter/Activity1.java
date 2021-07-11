package jp.ac.titech.itpro.sdl.walkcounter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import jp.ac.titech.itpro.sdl.walkcounter.db.*;

public class Activity1 extends Activity implements SensorEventListener {
    final static String TAG = Activity1.class.getSimpleName();
    private final int REQUEST_CODE = 1000;

    private appDatabase db;
    private stepDao sd;
    private long steps=0;
    private long min;

    private BarChart barChart;
    private Button deleteButton;

    Handler handler = new Handler();
    ScheduledExecutorService se = Executors.newSingleThreadScheduledExecutor();
    Runnable minStepCount;

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
            min = savedInstanceState.getLong("min");
        }else{
            min=0;
        }
        setContentView(R.layout.activity1);
        Log.d(TAG, "onCreate");

        UpdateReceiver receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SEND_STEPS");
        registerReceiver(receiver, filter);

        db = appDatabaseSingleton.getInstance(getApplicationContext());
        sd = db.getstepDao();
        updateGraph();

        deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteData();
            }
        });

        Log.d(TAG, "starting service");
        Intent intent = new Intent(getApplication(), CountService.class);
        startService(intent);

        se.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Log.d(TAG , "合計" + steps + "歩歩いたので記録します");
                BarData data = barChart.getData();
                IBarDataSet set = data.getDataSetByIndex(0);
                insertData(String.valueOf(min),steps);
                updateGraph();
                min += 5;
                steps = 0;
            }
        }, 5, 5, TimeUnit.MINUTES);
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
        Log.d(TAG, "onSaveInstanceState");
        outState.putLong("min",min);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        Intent intent = new Intent(getApplication(), CountService.class);
        stopService(intent);
        handler.removeCallbacks(minStepCount);
        se.shutdown();
        super.onDestroy();
    }

    public void updateGraph(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run(){
                List<stepData> stepdata = null;
                List<String> mins = new ArrayList<>();
                ArrayList<BarEntry> entryList = new ArrayList<>();
                try{ stepdata = sd.getDataForAll();} catch(Exception e) { Log.e(TAG,"cannot get stepdata");}

                entryList.add(new BarEntry(0, 0));

                if(stepdata != null) {
                    for (int i = 0; i < stepdata.toArray().length; i++) {
                        entryList.add(new BarEntry(i+1, stepdata.get(i).getSteps()));
                        mins.add(stepdata.get(i).getMinute());
                    }
                }
                Log.d(TAG,"got " + stepdata.toArray().length +" stepdata from db");


                handler.post(new Runnable(){
                   @Override
                   public void run(){
                       Log.d(TAG ,"start to write graph");
                       BarDataSet barDataSet = new BarDataSet(entryList, "歩数");
                       barDataSet.setColor(Color.BLUE);

                       BarData barData = new BarData(barDataSet);

                       barChart = findViewById(R.id.barChartExample);
                       barChart.setData(barData);

                       barChart.setDrawValueAboveBar(false);
                       barChart.getDescription().setText("分");

                       barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                       barChart.getXAxis().setDrawGridLines(false);
                       barChart.getXAxis().setEnabled(false);
                       barChart.getXAxis().setTextColor(Color.BLACK);

                       barChart.getAxisLeft().setAxisMinimum(0);
                       barChart.getAxisRight().setEnabled(false);

                       barChart.setVisibleXRangeMaximum(12);
                       BarData data = barChart.getData();
                       data.setBarWidth((float)1);
                       //barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mins));
                       data.notifyDataChanged();
                       barChart.notifyDataSetChanged();
                       barChart.invalidate();
                       barChart.moveViewToX(data.getEntryCount());
                   }
                });
            }
        });
        executor.shutdown();
    }

    public void insertData(String m, long s){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run(){
                try{sd.insert(new stepData(m,s));}
                catch (Exception e){Log.e(TAG, "cannot write stepdata to db");}
                updateGraph();
            }
        });
        Log.d(TAG, "wrote stepdata to db");
        executor.shutdown();
    }

    public void deleteData(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run(){
                try{sd.deleteAll();}
                catch (Exception e){Log.e(TAG, "cannot delete stepdata");}
            }
        });
        Log.d(TAG, "deleted all stepdata");
        executor.shutdown();
    }
}