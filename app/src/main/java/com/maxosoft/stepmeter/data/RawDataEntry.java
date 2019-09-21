package com.maxosoft.stepmeter.data;

import android.hardware.Sensor;

import java.util.Date;

public class RawDataEntry {
    private Integer sensorType;
    private Date date;
    private Float x;
    private Float y;
    private Float z;

    public RawDataEntry() {}

    public RawDataEntry(String inputLine) {
        String[] tokens = inputLine.replaceAll(",", ".").split(" ");
        if (tokens.length == 5) {
            try {
                this.sensorType = tokens[0].equals("a") ? Sensor.TYPE_ACCELEROMETER : Sensor.TYPE_GYROSCOPE;
                this.date = new Date(Long.valueOf(tokens[1]));
                this.x = Float.valueOf(tokens[2]);
                this.y = Float.valueOf(tokens[3]);
                this.z = Float.valueOf(tokens[4]);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(inputLine);
            }
        }
    }

    public Integer getSensorType() {
        return sensorType;
    }

    public void setSensorType(Integer sensorType) {
        this.sensorType = sensorType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }
}
