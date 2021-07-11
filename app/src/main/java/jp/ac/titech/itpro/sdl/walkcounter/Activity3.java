package jp.ac.titech.itpro.sdl.walkcounter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.ac.titech.itpro.sdl.walkcounter.db.*;

public class Activity3 extends Activity {
    final static String TAG = Activity3.class.getSimpleName();
    private appDatabase db;
    private stepDao sd;
    private TextView v1, v2, v3;

    Handler handler = new Handler();

    double height = 175.0;
    double weight = 85.0;
    long sumSteps = 0;
    double sumDistance = 0;
    double sumKCal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3);
        db = appDatabaseSingleton.getInstance(getApplicationContext());
        sd = db.getstepDao();
        getDataFromDB();
        v1 = new TextView(this);
        v1 = findViewById(R.id.textView3_1);
        v1.setText("ここに歩行距離を表示");
        v2 = new TextView(this);
        v2 = findViewById(R.id.textView3_2);
        v2.setText("ここに消費カロリーを表示");
        v3 = new TextView(this);
        v3 = findViewById(R.id.textView3_3);
        v3.setText("ここに目標を表示");
    }

    public void getDataFromDB(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable(){
            @Override
            public void run(){
                List<stepData> data = new ArrayList<>();
                try{data = sd.getDataForDay();}
                catch (Exception e) { Log.d(TAG,"cannot get data for an hour from db");}
                for(int i=0; i<data.toArray().length; i++){
                    long step = data.get(i).getSteps();
                    Log.d(TAG, data.get(i).getMinute() + " " + step);
                    sumSteps += step;
                    if(step >= 600){
                        sumDistance += height * 0.5 * step;
                        sumKCal += 1.05 * weight * 5.0 / 12;
                    }else if(step >= 300){
                        sumDistance += height * 0.45 * step;
                        sumKCal += 1.05 * weight * 3.0 / 12;
                    }else if(step > 0){
                        sumDistance += height * 0.4 * step;
                        sumKCal += 1.05 * weight * (step/100.0) / 12;
                    }
                }
                sumDistance /= 100000;
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        String station = "神田";
                        String food = "うまい棒1本";
                        long target = 1;
                        long dist = 0;
                        v1.setText("直近1日の歩数:"+sumSteps+"\n直近1時間の歩行距離:"+sumDistance+"km\n"+"東京～"+station+" 相当");
                        v2.setText("直近1日の消費カロリー"+sumKCal+"kcal\n"+food+" 相当");
                        v3.setText("1日の歩行目標:"+target+"km\n目標まで"+dist+"km");
                        sumSteps = 0;
                        sumDistance = 0;
                        sumKCal = 0;
                    }
                });
            }
        });

    }
    @Override
    protected void onResume() {
        getDataFromDB();
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }
}
