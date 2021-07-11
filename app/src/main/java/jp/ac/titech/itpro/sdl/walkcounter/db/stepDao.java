package jp.ac.titech.itpro.sdl.walkcounter.db;

import androidx.room.*;

import java.util.List;

@Dao
public interface stepDao {
    @Query("SELECT * FROM step_data order by id asc")
    List<stepData> getDataForAll();

    @Query("SELECT * FROM step_data order by id desc limit 288")
    List<stepData> getDataForDay();

    @Query("SELECT * FROM step_data order by id desc limit 12")
    List<stepData> getDataForHour();

    @Query("DELETE FROM step_data")
    void deleteAll();

    @Insert
    void insert(stepData sd);

    @Update
    void update(stepData sd);

    @Delete
    void delete(stepData sd);
}
