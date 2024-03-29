package com.maxosoft.stepmeter.data;

import android.hardware.Sensor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FeatureProvider {
    interface FeatureMiner {
        Float get(List<RawDataEntry> data);
    }

    public enum Feature {
        MIN_X_ACC("accMinX", FeatureProvider::getMinXAcc), MAX_X_ACC("accMaxX", FeatureProvider::getMaxXAcc),
        MIN_Y_ACC("accMinY", FeatureProvider::getMinYAcc), MAX_Y_ACC("accMaxY", FeatureProvider::getMaxYAcc),
        MIN_Z_ACC("accMinZ", FeatureProvider::getMinZAcc), MAX_Z_ACC("accMaxZ", FeatureProvider::getMaxZAcc),
        MEAN_X_ACC("accMeanX", FeatureProvider::getMeanXAcc),
        MEAN_Y_ACC("accMeanY", FeatureProvider::getMeanYAcc),
        MEAN_Z_ACC("accMeanZ", FeatureProvider::getMeanZAcc),
        VAR_X_ACC("accVarX", FeatureProvider::getVarXAcc),
        VAR_Y_ACC("accVarY", FeatureProvider::getVarYAcc),
        VAR_Z_ACC("accVarZ", FeatureProvider::getVarZAcc),
        DEV_X_ACC("accDevX", FeatureProvider::getDevXAcc),
        DEV_Y_ACC("accDevY", FeatureProvider::getDevYAcc),
        DEV_Z_ACC("accDevZ", FeatureProvider::getDevZAcc),
        SKEW_X_ACC("accSkewX", FeatureProvider::getSkewXAcc),
        SKEW_Y_ACC("accSkewY", FeatureProvider::getSkewYAcc),
        SKEW_Z_ACC("accSkewZ", FeatureProvider::getSkewZAcc),
        KURT_X_ACC("accKurtX", FeatureProvider::getKurtXAcc),
        KURT_Y_ACC("accKurtY", FeatureProvider::getKurtYAcc),
        KURT_Z_ACC("accKurtZ", FeatureProvider::getKurtZAcc),
        ZERO_X_ACC("accZeroX", FeatureProvider::getZeroXAcc),
        ZERO_Y_ACC("accZeroY", FeatureProvider::getZeroYAcc),
        ZERO_Z_ACC("accZeroZ", FeatureProvider::getZeroZAcc),
        MIN_X_GYR("gyrMinX", FeatureProvider::getMinXGyr), MAX_X_GYR("gyrMaxX", FeatureProvider::getMaxXGyr),
        MIN_Y_GYR("gyrMinY", FeatureProvider::getMinYGyr), MAX_Y_GYR("gyrMaxY", FeatureProvider::getMaxYGyr),
        MIN_Z_GYR("gyrMinZ", FeatureProvider::getMinZGyr), MAX_Z_GYR("gyrMaxZ", FeatureProvider::getMaxZGyr),
        MEAN_X_GYR("gyrMeanX", FeatureProvider::getMeanXGyr),
        MEAN_Y_GYR("gyrMeanY", FeatureProvider::getMeanYGyr),
        MEAN_Z_GYR("gyrMeanZ", FeatureProvider::getMeanZGyr),
        VAR_X_GYR("gyrVarX", FeatureProvider::getVarXGyr),
        VAR_Y_GYR("gyrVarY", FeatureProvider::getVarYGyr),
        VAR_Z_GYR("gyrVarZ", FeatureProvider::getVarZGyr),
        DEV_X_GYR("gyrDevX", FeatureProvider::getDevXGyr),
        DEV_Y_GYR("gyrDevY", FeatureProvider::getDevYGyr),
        DEV_Z_GYR("gyrDevZ", FeatureProvider::getDevZGyr),
        SKEW_X_GYR("gyrSkewX", FeatureProvider::getSkewXGyr),
        SKEW_Y_GYR("gyrSkewY", FeatureProvider::getSkewYGyr),
        SKEW_Z_GYR("gyrSkewZ", FeatureProvider::getSkewZGyr),
        KURT_X_GYR("gyrKurtX", FeatureProvider::getKurtXGyr),
        KURT_Y_GYR("gyrKurtY", FeatureProvider::getKurtYGyr),
        KURT_Z_GYR("gyrKurtZ", FeatureProvider::getKurtZGyr),
        ZERO_X_GYR("gyrZeroX", FeatureProvider::getZeroXGyr),
        ZERO_Y_GYR("gyrZeroY", FeatureProvider::getZeroYGyr),
        ZERO_Z_GYR("gyrZeroZ", FeatureProvider::getZeroZGyr);

        private String name;
        private FeatureMiner miner;

        Feature(String name, FeatureMiner featureMiner) {
            this.name = name;
            this.miner = featureMiner;
        }

        public String getName() {
            return this.name;
        }

        public Float getValue(List<RawDataEntry> data) {
            return miner.get(data);
        }
    }

    public static Float getFeature(List<RawDataEntry> data, Feature feature) {
        return feature.getValue(data);
    }

    // GENERAL FEATURES
    private static Float getMin(Collection<Float> values) {
        return values.stream().min(Comparator.comparing(Float::floatValue)).orElse(null);
    }

    private static Float getMax(Collection<Float> values) {
        return values.stream().max(Comparator.comparing(Float::floatValue)).orElse(null);
    }

    private static Float getMean(Collection<Float> values) {
        return (float) values.stream().filter(Objects::nonNull).mapToDouble(Float::doubleValue).average().orElse(0);
    }

    private static Float getVariance(Collection<Float> values) {
        float mean = getMean(values);
        return (1f/(values.size() - 1))*
                (float)values.stream().map(val -> Math.pow(val - mean, 2)).mapToDouble(Double::doubleValue).sum();
    }

    private static Float getDeviation(Collection<Float> values) {
        return (float) Math.sqrt(getVariance(values));
    }

    private static Float getSkew(Collection<Float> values) {
        float mean = getMean(values);
        float dev = getDeviation(values);
        return ((1f/values.size())*
                ((float)values.stream().map(val -> Math.pow(val - mean, 3)).mapToDouble(Double::doubleValue).sum()))/
                (dev*dev*dev);
    }

    private static Float getKurt(Collection<Float> values) {
        float mean = getMean(values);
        float dev = getDeviation(values);
        return ((1f/values.size())*
                ((float)values.stream().map(val -> Math.pow(val - mean, 4)).mapToDouble(Double::doubleValue).sum()))/
                (dev*dev*dev*dev);
    }

    private static Float getZeroCrossingRate(List<Float> values) {
        int rate = 0;
        for (int i = 0; i < values.size() - 2; i++) {
            if (values.get(i) * values.get(i + 1) < 0) {
                rate++;
            }
        }
        return (float) rate;
    }

    // CONCRETE FEATURES
    private static Float getMinXAcc(List<RawDataEntry> data) {
        return getMin(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getMaxXAcc(List<RawDataEntry> data) {
        return getMax(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getMinYAcc(List<RawDataEntry> data) {
        return getMin(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getMaxYAcc(List<RawDataEntry> data) {
        return getMax(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getMinZAcc(List<RawDataEntry> data) {
        return getMin(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getMaxZAcc(List<RawDataEntry> data) {
        return getMax(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getMinXGyr(List<RawDataEntry> data) {
        return getMin(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getMaxXGyr(List<RawDataEntry> data) {
        return getMax(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getMinYGyr(List<RawDataEntry> data) {
        return getMin(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getMaxYGyr(List<RawDataEntry> data) {
        return getMax(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getMinZGyr(List<RawDataEntry> data) {
        return getMin(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getMaxZGyr(List<RawDataEntry> data) {
        return getMax(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getMeanXAcc(List<RawDataEntry> data) {
        return getMean(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getMeanYAcc(List<RawDataEntry> data) {
        return getMean(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getMeanZAcc(List<RawDataEntry> data) {
        return getMean(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getMeanXGyr(List<RawDataEntry> data) {
        return getMean(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getMeanYGyr(List<RawDataEntry> data) {
        return getMean(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getMeanZGyr(List<RawDataEntry> data) {
        return getMean(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getVarXAcc(List<RawDataEntry> data) {
        return getVariance(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getVarYAcc(List<RawDataEntry> data) {
        return getVariance(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getVarZAcc(List<RawDataEntry> data) {
        return getVariance(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getVarXGyr(List<RawDataEntry> data) {
        return getVariance(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getVarYGyr(List<RawDataEntry> data) {
        return getVariance(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getVarZGyr(List<RawDataEntry> data) {
        return getVariance(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getDevXAcc(List<RawDataEntry> data) {
        return getDeviation(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getDevYAcc(List<RawDataEntry> data) {
        return getDeviation(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getDevZAcc(List<RawDataEntry> data) {
        return getDeviation(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getDevXGyr(List<RawDataEntry> data) {
        return getDeviation(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getDevYGyr(List<RawDataEntry> data) {
        return getDeviation(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getDevZGyr(List<RawDataEntry> data) {
        return getDeviation(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getSkewXAcc(List<RawDataEntry> data) {
        return getSkew(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getSkewYAcc(List<RawDataEntry> data) {
        return getSkew(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getSkewZAcc(List<RawDataEntry> data) {
        return getSkew(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getSkewXGyr(List<RawDataEntry> data) {
        return getSkew(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getSkewYGyr(List<RawDataEntry> data) {
        return getSkew(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getSkewZGyr(List<RawDataEntry> data) {
        return getSkew(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getKurtXAcc(List<RawDataEntry> data) {
        return getKurt(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getKurtYAcc(List<RawDataEntry> data) {
        return getKurt(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getKurtZAcc(List<RawDataEntry> data) {
        return getKurt(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getKurtXGyr(List<RawDataEntry> data) {
        return getKurt(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getKurtYGyr(List<RawDataEntry> data) {
        return getKurt(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getKurtZGyr(List<RawDataEntry> data) {
        return getKurt(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getZeroXAcc(List<RawDataEntry> data) {
        return getZeroCrossingRate(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getZeroYAcc(List<RawDataEntry> data) {
        return getZeroCrossingRate(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getZeroZAcc(List<RawDataEntry> data) {
        return getZeroCrossingRate(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_ACCELEROMETER))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }

    private static Float getZeroXGyr(List<RawDataEntry> data) {
        return getZeroCrossingRate(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getX).collect(Collectors.toList()));
    }

    private static Float getZeroYGyr(List<RawDataEntry> data) {
        return getZeroCrossingRate(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getY).collect(Collectors.toList()));
    }

    private static Float getZeroZGyr(List<RawDataEntry> data) {
        return getZeroCrossingRate(data.stream().filter(e -> e.getSensorType().equals(Sensor.TYPE_GYROSCOPE))
                .map(RawDataEntry::getZ).collect(Collectors.toList()));
    }
}
