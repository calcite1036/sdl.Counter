package jp.ac.titech.itpro.sdl.walkcounter.db;

import android.content.Context;

import androidx.room.*;

public class appDatabaseSingleton {
    private static appDatabase instance = null;

    public static appDatabase getInstance(Context context){
        if(instance != null){
            return instance;
        }

        instance = Room.databaseBuilder(context, appDatabase.class, "steps").build();
        return instance;
    }
}
