package com.maxosoft.stepmeter.data;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Window {
    private List<RawDataEntry> accelerometerData;
    private List<RawDataEntry> gyroscopeData;

    public Window() {
        accelerometerData = new ArrayList<>();
        gyroscopeData = new ArrayList<>();
    }

    public Window(List<RawDataEntry> allEntries) {
        accelerometerData = new ArrayList<>();
        gyroscopeData = new ArrayList<>();
        for (RawDataEntry entry: allEntries) {
            if (entry.getSensorType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerData.add(entry);
            } else if (entry.getSensorType() == Sensor.TYPE_GYROSCOPE) {
                gyroscopeData.add(entry);
            }
        }
    }

    public Date getStart(int sensorType) {
       if (sensorType == Sensor.TYPE_ACCELEROMETER) {
           return accelerometerData.get(0).getDate();
       } else {
           return gyroscopeData.get(0).getDate();
       }
    }

    public Date getEnd(int sensorType) {
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            return accelerometerData.get(accelerometerData.size() - 1).getDate();
        } else {
            return gyroscopeData.get(gyroscopeData.size() - 1).getDate();
        }
    }

    public int getEntryCount(int sensorType) {
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            return accelerometerData.size();
        } else {
            return gyroscopeData.size();
        }
    }

    public void addAccelerometerEntry(RawDataEntry entry) {
        accelerometerData.add(entry);
    }

    public void addGyroscopeEntry(RawDataEntry entry) {
        gyroscopeData.add(entry);
    }


    public List<RawDataEntry> getAccelerometerData() {
        return accelerometerData;
    }

    public void setAccelerometerData(List<RawDataEntry> accelerometerData) {
        this.accelerometerData = accelerometerData;
    }

    public List<RawDataEntry> getGyroscopeData() {
        return gyroscopeData;
    }

    public void setGyroscopeData(List<RawDataEntry> gyroscopeData) {
        this.gyroscopeData = gyroscopeData;
    }
}
