package com.example.myapexapp;

/**
 * Created by aishwarya on 24/3/17.
 */
public class PojoEvent {
    public PojoEvent() {
    }

    private String id;
    private String time;

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


    private String temperature;




}