package jp.ac.titech.itpro.sdl.walkcounter.db;

import androidx.room.*;

@Entity(tableName = "step_data")
public class stepData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String minute;
    private long steps;

    public stepData(String minute, long steps){
        this.minute = minute;
        this.steps = steps;
    }

    public void setId(int id) { this.id = id; }

    public int getId(){
        return id;
    }

    public void setMinute(String minute){ this.minute = minute; }

    public String getMinute(){
        return minute;
    }

    public void setSteps() { this.steps = steps; }

    public long getSteps(){
        return steps;
    }

    public stepData update(String minute, int steps){
        this.minute = minute;
        this.steps = steps;
        return this;
    }
}
