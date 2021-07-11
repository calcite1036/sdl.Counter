package jp.ac.titech.itpro.sdl.walkcounter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.ac.titech.itpro.sdl.walkcounter.db.*;

import static android.text.InputType.TYPE_CLASS_NUMBER;

public class Activity4 extends Activity{
    final static String TAG = Activity4.class.getSimpleName();
    private EditText e1, e2, e3, e4;
    private Button send;
    private String height, weight, normaHour, normaDay;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity4);
        e1 = new EditText(this);
        e1 = findViewById(R.id.editHeight);
        e1.setInputType(TYPE_CLASS_NUMBER);
        e2 = new EditText(this);
        e2 = findViewById(R.id.editWeight);
        e2.setInputType(TYPE_CLASS_NUMBER);
        e3 = new EditText(this);
        e3 = findViewById(R.id.editNormaForHour);
        e3.setInputType(TYPE_CLASS_NUMBER);
        e4 = new EditText(this);
        e4 = findViewById(R.id.editNormaForDay);
        e4.setInputType(TYPE_CLASS_NUMBER);
        send = new Button(this);
        send = findViewById(R.id.button_send);
        send.setOnClickListener(v -> {
            height = e1.getText().toString();
            weight = e2.getText().toString();
            normaHour = e3.getText().toString();
            normaDay = e4.getText().toString();
            prefs = getSharedPreferences("app_setting", Context.MODE_PRIVATE);
            editor = prefs.edit();
            editor.putString("setting_height",height);
            editor.putString("setting_weight",weight);
            editor.putString("setting_normaHour",normaHour);
            editor.putString("setting_normaDay",normaDay);
            editor.commit();
            Log.d(TAG, "set settings");
        });
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

}
