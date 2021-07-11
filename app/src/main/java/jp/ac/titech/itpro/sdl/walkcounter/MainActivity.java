package jp.ac.titech.itpro.sdl.walkcounter;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import androidx.appcompat.app.AppCompatActivity;

import jp.ac.titech.itpro.sdl.walkcounter.db.*;

public class MainActivity extends TabActivity {
    final static String TAG = MainActivity.class.getSimpleName();
    private appDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = appDatabaseSingleton.getInstance(getApplicationContext());
        initTabs();
    }

    protected void initTabs(){
        TabHost tabhost = getTabHost();
        Intent intent;

        TabSpec tab1 = tabhost.newTabSpec("tab1");
        tab1.setIndicator("万歩計");
        intent = new Intent().setClass(this, Activity1.class);
        tab1.setContent(intent);
        tabhost.addTab(tab1);

        TabSpec tab2 = tabhost.newTabSpec("tab2");
        tab2.setIndicator("直近1時間のデータ");
        intent = new Intent().setClass(this, Activity2.class);
        tab2.setContent(intent);
        tabhost.addTab(tab2);

        TabSpec tab3 = tabhost.newTabSpec("tab3");
        tab3.setIndicator("直近1日のデータ");
        intent = new Intent().setClass(this, Activity3.class);
        tab3.setContent(intent);
        tabhost.addTab(tab3);

        tabhost.setCurrentTab(0);
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
    public void onSaveInstanceState(Bundle outState){
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}