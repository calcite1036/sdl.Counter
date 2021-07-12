package jp.ac.titech.itpro.sdl.walkcounter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
    private TextView v1, v2, v3, v4;

    private SharedPreferences prefs;

    Handler handler = new Handler();

    double height = 175.0;
    double weight = 85.0;
    double normaDay = 0;

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
        v4 = new TextView(this);
        v4 = findViewById(R.id.textView3_4);
        v4.setText("ここに身長と体重を表示");
    }

    public void getDataFromDB(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable(){
            @Override
            public void run(){
                List<stepData> data = new ArrayList<>();
                try{data = sd.getDataForDay();}
                catch (Exception e) { Log.d(TAG,"cannot get data for an hour from db");}

                prefs = getSharedPreferences("app_setting", Context.MODE_PRIVATE);
                height = Double.parseDouble(prefs.getString("setting_height","175.0"));
                weight = Double.parseDouble(prefs.getString("setting_weight","85.0"));
                normaDay = Double.parseDouble(prefs.getString("setting_normaDay","0.0"));
                Log.d(TAG, "height:" + height + " weight:" + weight + " norma:" + normaDay);

                for(int i=0; i<data.toArray().length; i++){
                    long step = data.get(i).getSteps();
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
                        String station = getStation(sumDistance);
                        String food = getFood(sumKCal);
                        String norma;
                        if(normaDay <= sumDistance){
                            norma = "目標達成！";
                        }else{
                            norma = "目標まであと" + (normaDay-sumDistance) + "km";
                        }

                        sumDistance = Math.round(sumDistance*100.0)/100.0;
                        sumKCal = Math.round(sumKCal*100.0)/100.0;

                        v1.setText("直近1日の歩数:"+sumSteps+"\n直近1日の歩行距離:"+sumDistance+"km\n"+station+" 相当");
                        v2.setText("直近1日の消費カロリー"+sumKCal+"kcal\n"+food+" 相当");
                        v3.setText("1日の歩行目標:"+normaDay+"km\n" + norma);
                        v4.setText("身長:"+height+"cm\n体重:"+weight+"kg");
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

    public String getStation(double d){
        if(d >= 552.6){return "東京～新大阪";}
        else if(d >= 513.6){return "東京～京都";}
        else if(d >= 366.0){return "東京～名古屋";}
        else if(d >= 257.1){return "東京～浜松";}
        else if(d >= 180.2){return "東京～静岡";}
        else if(d >= 104.6){return "東京～熱海";}
        else if(d >= 83.9){return "東京～小田原";}
        else if(d >= 58.6){return "東京～茅ヶ崎";}
        else if(d >= 51.1){return "東京～藤沢";}
        else if(d >= 47.4){return "東京～八王子";}
        else if(d >= 46.5){return "東京～大船";}
        else if(d >= 39.2){return "東京～千葉";}
        else if(d >= 37.5){return "東京～立川";}
        else if(d >= 31.6){return "東京～幕張";}
        else if(d >= 30.3){return "東京～大宮";}
        else if(d >= 28.8){return "東京～横浜";}
        else if(d >= 26.7){return "東京～津田沼";}
        else if(d >= 23.2){return "東京～船橋";}
        else if(d >= 18.2){return "東京～川崎";}
        else if(d >= 9.2){return "東京～大井町";}
        else if(d >= 6.8){return "東京～品川";}
        else if(d >= 4.6){return "東京～田町";}
        else if(d >= 3.6){return "東京～上野";}
        else if(d >= 3.1){return "東京～浜松町";}
        else if(d >= 2.6){return "東京～御茶ノ水";}
        else if(d >= 2.0){return "東京～秋葉原";}
        else if(d >= 1.9){return "東京～新橋";}
        else if(d >= 1.3){return "東京～神田";}
        else if(d >= 0.8){return "東京～有楽町";}
        else {return "----";}
    }

    public String getFood(double c){
        if(c >= 4500){return "カツ丼5杯以上";}
        else if(c >= 3600){return "カツ丼4杯";}
        else if(c >= 2700){return "カツ丼3杯";}
        else if(c >= 1786){return "カツ丼2杯";}
        else if(c >= 1300){return "ピザ丸ごと1枚";}
        else if(c >= 954){return "ビーフカレー1皿";}
        else if(c >= 893){return "カツ丼1杯";}
        else if(c >= 805){return "天丼1杯";}
        else if(c >= 754){return "チャーハン1人前";}
        else if(c >= 728){return "ハヤシライス1皿";}
        else if(c >= 687){return "鯖の味噌煮(定食)1膳";}
        else if(c >= 649){return "鉄火丼1杯";}
        else if(c >= 597){return "ミートソーススパゲティ1皿";}
        else if(c >= 553){return "お好み焼き1枚";}
        else if(c >= 524){return "ビッグマック1個";}
        else if(c >= 500){return "ポテトチップス(塩)1袋";}
        else if(c >= 471){return "ハンバーグ1個";}
        else if(c >= 457){return "ミラノ風ドリア1皿";}
        else if(c >= 423){return "餃子1人前";}
        else if(c >= 401){return "塩ラーメン1杯";}
        else if(c >= 382){return "きつねうどん1杯";}
        else if(c >= 368){return "チーズバーガー1個";}
        else if(c >= 324){return "かけそば1杯";}
        else if(c >= 300){return "ハンバーガー1個";}
        else if(c >= 270){return "白米1杯";}
        else if(c >= 225){return "カルピス500ml";}
        else if(c >= 200){return "肉まん1個";}
        else if(c >= 177){return "6枚切り食パン1枚";}
        else if(c >= 160){return "カステラ1切れ";}
        else if(c >= 120){return "うまい棒3本";}
        else if(c >= 108){return "りんご2個";}
        else if(c >= 88){return "ワイン1杯";}
        else if(c >= 80){return "じゃがいも1個";}
        else if(c >= 71){return "カフェオレ1杯";}
        else if(c >= 54){return "りんご1個";}
        else if(c >= 40){return "うまい棒1本";}
        else if(c >= 30){return "チロルチョコ1個";}
        else if(c >= 17){return "ところてん";}
        else{return "ところてん以下";}

    }
}
