package com.example.myapexapp;


/**
 * Created by aishwarya on 12/4/17.
 */
public class PojoEvent {
    private String id;
    private String time;
    private String temperature;


    public PojoEvent ()
    {

    }

  @Override
    public String toString() {

      System.out.println();
        return "\n Sensor ID " + id + " recorded on " + time+ " a temperature of " + temperature +"C";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }




}
