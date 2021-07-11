package jp.ac.titech.itpro.sdl.walkcounter.db;

import androidx.room.*;

@Database(entities = {stepData.class}, version = 1, exportSchema = false)
public abstract class appDatabase extends RoomDatabase {
    public abstract stepDao getstepDao();
}
