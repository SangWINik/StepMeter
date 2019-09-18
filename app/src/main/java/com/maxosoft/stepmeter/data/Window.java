package com.maxosoft.stepmeter.data;

import android.hardware.Sensor;

import java.util.Comparator;
import java.util.List;

public class Window {
    public static final int WINDOW_SIZE = 5; // window size in seconds
    public static final int WINDOW_OFFSET = 2; // offset in seconds

    private boolean isOwner;
    private Float accMinX;
    private Float accMaxX;
    private Float accMinY;
    private Float accMaxY;
    private Float accMinZ;
    private Float accMaxZ;
    private Float gyrMinX;
    private Float gyrMaxX;
    private Float gyrMinY;
    private Float gyrMaxY;
    private Float gyrMinZ;
    private Float gyrMaxZ;

    public Window(List<RawDataEntry> allEntries, boolean isOwner) {
        accMinX = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .min(Comparator.comparing(RawDataEntry::getX)).get().getX();
        accMaxX = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .max(Comparator.comparing(RawDataEntry::getX)).get().getX();
        accMinY = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .min(Comparator.comparing(RawDataEntry::getY)).get().getY();
        accMaxY = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .max(Comparator.comparing(RawDataEntry::getY)).get().getY();
        accMinZ = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .min(Comparator.comparing(RawDataEntry::getZ)).get().getZ();
        accMaxZ = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .max(Comparator.comparing(RawDataEntry::getZ)).get().getZ();
        /*gyrMinX = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .min(Comparator.comparing(RawDataEntry::getX)).get().getX();
        gyrMaxX = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .max(Comparator.comparing(RawDataEntry::getX)).get().getX();
        gyrMinY = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .min(Comparator.comparing(RawDataEntry::getY)).get().getY();
        gyrMaxY = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .max(Comparator.comparing(RawDataEntry::getY)).get().getY();
        gyrMinZ = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .min(Comparator.comparing(RawDataEntry::getZ)).get().getZ();
        gyrMaxZ = allEntries.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .max(Comparator.comparing(RawDataEntry::getZ)).get().getZ();*/

        this.isOwner = isOwner;
    }

    public static String getFeatureHeader() {
        return "minX,maxX,minY,maxY,minZ,maxZ\n";
    }

    public String getFeaturesLine() {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
                getAccMinX(), getAccMaxX(),
                getAccMinY(), getAccMaxY(),
                getAccMinZ(), getAccMaxZ(),
                isOwner());
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public Float getAccMinX() {
        return accMinX;
    }

    public void setAccMinX(Float accMinX) {
        this.accMinX = accMinX;
    }

    public Float getAccMaxX() {
        return accMaxX;
    }

    public void setAccMaxX(Float accMaxX) {
        this.accMaxX = accMaxX;
    }

    public Float getAccMinY() {
        return accMinY;
    }

    public void setAccMinY(Float accMinY) {
        this.accMinY = accMinY;
    }

    public Float getAccMaxY() {
        return accMaxY;
    }

    public void setAccMaxY(Float accMaxY) {
        this.accMaxY = accMaxY;
    }

    public Float getAccMinZ() {
        return accMinZ;
    }

    public void setAccMinZ(Float accMinZ) {
        this.accMinZ = accMinZ;
    }

    public Float getAccMaxZ() {
        return accMaxZ;
    }

    public void setAccMaxZ(Float accMaxZ) {
        this.accMaxZ = accMaxZ;
    }

    public Float getGyrMinX() {
        return gyrMinX;
    }

    public void setGyrMinX(Float gyrMinX) {
        this.gyrMinX = gyrMinX;
    }

    public Float getGyrMaxX() {
        return gyrMaxX;
    }

    public void setGyrMaxX(Float gyrMaxX) {
        this.gyrMaxX = gyrMaxX;
    }

    public Float getGyrMinY() {
        return gyrMinY;
    }

    public void setGyrMinY(Float gyrMinY) {
        this.gyrMinY = gyrMinY;
    }

    public Float getGyrMaxY() {
        return gyrMaxY;
    }

    public void setGyrMaxY(Float gyrMaxY) {
        this.gyrMaxY = gyrMaxY;
    }

    public Float getGyrMinZ() {
        return gyrMinZ;
    }

    public void setGyrMinZ(Float gyrMinZ) {
        this.gyrMinZ = gyrMinZ;
    }

    public Float getGyrMaxZ() {
        return gyrMaxZ;
    }

    public void setGyrMaxZ(Float gyrMaxZ) {
        this.gyrMaxZ = gyrMaxZ;
    }
}
