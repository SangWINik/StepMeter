package com.maxosoft.stepmeter.dto;

import com.maxosoft.stepmeter.data.FeatureProvider;
import com.maxosoft.stepmeter.data.FeatureSuit;
import com.maxosoft.stepmeter.data.Window;

import java.lang.reflect.Field;

public class DataWindowDto {
    private Long id;
    private Long sessionId;
    private Float accMinX;
    private Float accMaxX;
    private Float accMinY;
    private Float accMaxY;
    private Float accMinZ;
    private Float accMaxZ;
    private Float accMeanX;
    private Float accMeanY;
    private Float accMeanZ;
    private Float accVarX;
    private Float accVarY;
    private Float accVarZ;
    private Float accDevX;
    private Float accDevY;
    private Float accDevZ;
    private Float accSkewX;
    private Float accSkewY;
    private Float accSkewZ;
    private Float accKurtX;
    private Float accKurtY;
    private Float accKurtZ;
    private Float accZeroX;
    private Float accZeroY;
    private Float accZeroZ;

    private Float gyrMinX;
    private Float gyrMaxX;
    private Float gyrMinY;
    private Float gyrMaxY;
    private Float gyrMinZ;
    private Float gyrMaxZ;
    private Float gyrMeanX;
    private Float gyrMeanY;
    private Float gyrMeanZ;
    private Float gyrVarX;
    private Float gyrVarY;
    private Float gyrVarZ;
    private Float gyrDevX;
    private Float gyrDevY;
    private Float gyrDevZ;
    private Float gyrSkewX;
    private Float gyrSkewY;
    private Float gyrSkewZ;
    private Float gyrKurtX;
    private Float gyrKurtY;
    private Float gyrKurtZ;
    private Float gyrZeroX;
    private Float gyrZeroY;
    private Float gyrZeroZ;

    public DataWindowDto() {
    }

    public DataWindowDto(Window window, Long sessionId) {
        this.sessionId = sessionId;

        this.accMinX = window.getFeatures().get(FeatureProvider.Feature.MIN_X_ACC.name());
        this.accMaxX = window.getFeatures().get(FeatureProvider.Feature.MAX_X_ACC.name());
        this.accMinY = window.getFeatures().get(FeatureProvider.Feature.MIN_Y_ACC.name());
        this.accMaxY = window.getFeatures().get(FeatureProvider.Feature.MAX_Y_ACC.name());
        this.accMinZ = window.getFeatures().get(FeatureProvider.Feature.MIN_Z_ACC.name());
        this.accMaxZ = window.getFeatures().get(FeatureProvider.Feature.MAX_Z_ACC.name());
        this.accMeanX = window.getFeatures().get(FeatureProvider.Feature.MEAN_X_ACC.name());
        this.accMeanY = window.getFeatures().get(FeatureProvider.Feature.MEAN_Y_ACC.name());
        this.accMeanZ = window.getFeatures().get(FeatureProvider.Feature.MEAN_Z_ACC.name());
        this.accVarX = window.getFeatures().get(FeatureProvider.Feature.VAR_X_ACC.name());
        this.accVarY = window.getFeatures().get(FeatureProvider.Feature.VAR_Y_ACC.name());
        this.accVarZ = window.getFeatures().get(FeatureProvider.Feature.VAR_Z_ACC.name());
        this.accDevX = window.getFeatures().get(FeatureProvider.Feature.DEV_X_ACC.name());
        this.accDevY = window.getFeatures().get(FeatureProvider.Feature.DEV_Y_ACC.name());
        this.accDevZ = window.getFeatures().get(FeatureProvider.Feature.DEV_Z_ACC.name());
        this.accSkewX = window.getFeatures().get(FeatureProvider.Feature.SKEW_X_ACC.name());
        this.accSkewY = window.getFeatures().get(FeatureProvider.Feature.SKEW_Y_ACC.name());
        this.accSkewZ = window.getFeatures().get(FeatureProvider.Feature.SKEW_Z_ACC.name());
        this.accKurtX = window.getFeatures().get(FeatureProvider.Feature.KURT_X_ACC.name());
        this.accKurtY = window.getFeatures().get(FeatureProvider.Feature.KURT_Y_ACC.name());
        this.accKurtZ = window.getFeatures().get(FeatureProvider.Feature.KURT_Z_ACC.name());
        this.accZeroX = window.getFeatures().get(FeatureProvider.Feature.ZERO_X_ACC.name());
        this.accZeroY = window.getFeatures().get(FeatureProvider.Feature.ZERO_Y_ACC.name());
        this.accZeroZ = window.getFeatures().get(FeatureProvider.Feature.ZERO_Z_ACC.name());

        this.gyrMinX = window.getFeatures().get(FeatureProvider.Feature.MIN_X_GYR.name());
        this.gyrMaxX = window.getFeatures().get(FeatureProvider.Feature.MAX_X_GYR.name());
        this.gyrMinY = window.getFeatures().get(FeatureProvider.Feature.MIN_Y_GYR.name());
        this.gyrMaxY = window.getFeatures().get(FeatureProvider.Feature.MAX_Y_GYR.name());
        this.gyrMinZ = window.getFeatures().get(FeatureProvider.Feature.MIN_Z_GYR.name());
        this.gyrMaxZ = window.getFeatures().get(FeatureProvider.Feature.MAX_Z_GYR.name());
        this.gyrMeanX = window.getFeatures().get(FeatureProvider.Feature.MEAN_X_GYR.name());
        this.gyrMeanY = window.getFeatures().get(FeatureProvider.Feature.MEAN_Y_GYR.name());
        this.gyrMeanZ = window.getFeatures().get(FeatureProvider.Feature.MEAN_Z_GYR.name());
        this.gyrVarX = window.getFeatures().get(FeatureProvider.Feature.VAR_X_GYR.name());
        this.gyrVarY = window.getFeatures().get(FeatureProvider.Feature.VAR_Y_GYR.name());
        this.gyrVarZ = window.getFeatures().get(FeatureProvider.Feature.VAR_Z_GYR.name());
        this.gyrDevX = window.getFeatures().get(FeatureProvider.Feature.DEV_X_GYR.name());
        this.gyrDevY = window.getFeatures().get(FeatureProvider.Feature.DEV_Y_GYR.name());
        this.gyrDevZ = window.getFeatures().get(FeatureProvider.Feature.DEV_Z_GYR.name());
        this.gyrSkewX = window.getFeatures().get(FeatureProvider.Feature.SKEW_X_GYR.name());
        this.gyrSkewY = window.getFeatures().get(FeatureProvider.Feature.SKEW_Y_GYR.name());
        this.gyrSkewZ = window.getFeatures().get(FeatureProvider.Feature.SKEW_Z_GYR.name());
        this.gyrKurtX = window.getFeatures().get(FeatureProvider.Feature.KURT_X_GYR.name());
        this.gyrKurtY = window.getFeatures().get(FeatureProvider.Feature.KURT_Y_GYR.name());
        this.gyrKurtZ = window.getFeatures().get(FeatureProvider.Feature.KURT_Z_GYR.name());
        this.gyrZeroX = window.getFeatures().get(FeatureProvider.Feature.ZERO_X_GYR.name());
        this.gyrZeroY = window.getFeatures().get(FeatureProvider.Feature.ZERO_Y_GYR.name());
        this.gyrZeroZ = window.getFeatures().get(FeatureProvider.Feature.ZERO_Z_GYR.name());
    }

    public static String getHeader(FeatureSuit featureSuit) {
        StringBuilder line = new StringBuilder();
        for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
            line.append(feature.name()).append(",");
        }
        line.append("isOwner").append("\n");
        return line.toString();
    }

    public String getCommaSeparated(boolean isOwner, FeatureSuit featureSuit) {
        StringBuilder line = new StringBuilder();
        for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
            line.append(this.getFeature(feature)).append(",");
        }
        line.append(isOwner).append("\n");
        return line.toString();
    }

    public static String getHeader(boolean includeGyroscope) {
        String header = "accMinX, accMaxX, accMinY, accMaxY, accMinZ, accMaxZ, accMeanX, accMeanY, accMeanZ, " +
                "accVarX, accVarY, accVarZ, accDevX, accDevY, accDevZ, accSkewX, accSkewY, accSkewZ," +
                "accKurtX, accKurtY, accKurtZ, accZeroX, accZeroY, accZeroZ";
        if (includeGyroscope) {
            header += ", gyrMinX, gyrMaxX, gyrMinY, gyrMaxY, gyrMinZ, gyrMaxZ, gyrMeanX, gyrMeanY, gyrMeanZ, " +
                    "gyrVarX, gyrVarY, gyrVarZ, gyrDevX, gyrDevY, gyrDevZ, gyrSkewX, gyrSkewY, gyrSkewZ," +
                    "gyrKurtX, gyrKurtY, gyrKurtZ, gyrZeroX, gyrZeroY, gyrZeroZ";
        }
        return header + ", isOwner\n";
    }

    public String getCommaSeparated(boolean isOwner, boolean includeGyroscope) {
        String values = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s," +
                        "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                this.getAccMinX(), this.getAccMaxX(),
                this.getAccMinY(), this.getAccMaxY(), this.getAccMinZ(), this.getAccMaxZ(),
                this.getAccMeanX(), this.getAccMeanY(), this.getAccMeanZ(),
                this.getAccVarX(), this.getAccVarY(), this.getAccVarZ(),
                this.getAccDevX(), this.getAccDevY(), this.getAccDevZ(),
                this.getAccSkewX(), this.getAccSkewY(), this.getAccSkewZ(),
                this.getAccKurtX(), this.getAccKurtY(), this.getAccKurtZ(),
                this.getAccZeroX(), this.getAccZeroY(), this.getAccZeroZ());
        if (includeGyroscope) {
            values += String.format(", %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s," +
                            "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                    this.getGyrMinX(), this.getGyrMaxX(),
                    this.getGyrMinY(), this.getGyrMaxY(), this.getGyrMinZ(), this.getGyrMaxZ(),
                    this.getGyrMeanX(), this.getGyrMeanY(), this.getGyrMeanZ(),
                    this.getGyrVarX(), this.getGyrVarY(), this.getGyrVarZ(),
                    this.getGyrDevX(), this.getGyrDevY(), this.getGyrDevZ(),
                    this.getGyrSkewX(), this.getGyrSkewY(), this.getGyrSkewZ(),
                    this.getGyrKurtX(), this.getGyrKurtY(), this.getGyrKurtZ(),
                    this.getGyrZeroX(), this.getGyrZeroY(), this.getGyrZeroZ());
        }

        return values + ", " + isOwner + "\n";
    }

    public boolean includesGyroscope() {
        return this.gyrMinX != null;
    }

    public Float getFeature(FeatureProvider.Feature feature) {
        try {
            String fieldName = feature.getName();
            Field field = this.getClass().getDeclaredField(fieldName);
            return (Float) field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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

    public Float getAccMeanX() {
        return accMeanX;
    }

    public void setAccMeanX(Float accMeanX) {
        this.accMeanX = accMeanX;
    }

    public Float getAccMeanY() {
        return accMeanY;
    }

    public void setAccMeanY(Float accMeanY) {
        this.accMeanY = accMeanY;
    }

    public Float getAccMeanZ() {
        return accMeanZ;
    }

    public void setAccMeanZ(Float accMeanZ) {
        this.accMeanZ = accMeanZ;
    }

    public Float getAccVarX() {
        return accVarX;
    }

    public void setAccVarX(Float accVarX) {
        this.accVarX = accVarX;
    }

    public Float getAccVarY() {
        return accVarY;
    }

    public void setAccVarY(Float accVarY) {
        this.accVarY = accVarY;
    }

    public Float getAccVarZ() {
        return accVarZ;
    }

    public void setAccVarZ(Float accVarZ) {
        this.accVarZ = accVarZ;
    }

    public Float getAccDevX() {
        return accDevX;
    }

    public void setAccDevX(Float accDevX) {
        this.accDevX = accDevX;
    }

    public Float getAccDevY() {
        return accDevY;
    }

    public void setAccDevY(Float accDevY) {
        this.accDevY = accDevY;
    }

    public Float getAccDevZ() {
        return accDevZ;
    }

    public void setAccDevZ(Float accDevZ) {
        this.accDevZ = accDevZ;
    }

    public Float getAccSkewX() {
        return accSkewX;
    }

    public void setAccSkewX(Float accSkewX) {
        this.accSkewX = accSkewX;
    }

    public Float getAccSkewY() {
        return accSkewY;
    }

    public void setAccSkewY(Float accSkewY) {
        this.accSkewY = accSkewY;
    }

    public Float getAccSkewZ() {
        return accSkewZ;
    }

    public void setAccSkewZ(Float accSkewZ) {
        this.accSkewZ = accSkewZ;
    }

    public Float getAccKurtX() {
        return accKurtX;
    }

    public void setAccKurtX(Float accKurtX) {
        this.accKurtX = accKurtX;
    }

    public Float getAccKurtY() {
        return accKurtY;
    }

    public void setAccKurtY(Float accKurtY) {
        this.accKurtY = accKurtY;
    }

    public Float getAccKurtZ() {
        return accKurtZ;
    }

    public void setAccKurtZ(Float accKurtZ) {
        this.accKurtZ = accKurtZ;
    }

    public Float getAccZeroX() {
        return accZeroX;
    }

    public void setAccZeroX(Float accZeroX) {
        this.accZeroX = accZeroX;
    }

    public Float getAccZeroY() {
        return accZeroY;
    }

    public void setAccZeroY(Float accZeroY) {
        this.accZeroY = accZeroY;
    }

    public Float getAccZeroZ() {
        return accZeroZ;
    }

    public void setAccZeroZ(Float accZeroZ) {
        this.accZeroZ = accZeroZ;
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

    public Float getGyrMeanX() {
        return gyrMeanX;
    }

    public void setGyrMeanX(Float gyrMeanX) {
        this.gyrMeanX = gyrMeanX;
    }

    public Float getGyrMeanY() {
        return gyrMeanY;
    }

    public void setGyrMeanY(Float gyrMeanY) {
        this.gyrMeanY = gyrMeanY;
    }

    public Float getGyrMeanZ() {
        return gyrMeanZ;
    }

    public void setGyrMeanZ(Float gyrMeanZ) {
        this.gyrMeanZ = gyrMeanZ;
    }

    public Float getGyrVarX() {
        return gyrVarX;
    }

    public void setGyrVarX(Float gyrVarX) {
        this.gyrVarX = gyrVarX;
    }

    public Float getGyrVarY() {
        return gyrVarY;
    }

    public void setGyrVarY(Float gyrVarY) {
        this.gyrVarY = gyrVarY;
    }

    public Float getGyrVarZ() {
        return gyrVarZ;
    }

    public void setGyrVarZ(Float gyrVarZ) {
        this.gyrVarZ = gyrVarZ;
    }

    public Float getGyrDevX() {
        return gyrDevX;
    }

    public void setGyrDevX(Float gyrDevX) {
        this.gyrDevX = gyrDevX;
    }

    public Float getGyrDevY() {
        return gyrDevY;
    }

    public void setGyrDevY(Float gyrDevY) {
        this.gyrDevY = gyrDevY;
    }

    public Float getGyrDevZ() {
        return gyrDevZ;
    }

    public void setGyrDevZ(Float gyrDevZ) {
        this.gyrDevZ = gyrDevZ;
    }

    public Float getGyrSkewX() {
        return gyrSkewX;
    }

    public void setGyrSkewX(Float gyrSkewX) {
        this.gyrSkewX = gyrSkewX;
    }

    public Float getGyrSkewY() {
        return gyrSkewY;
    }

    public void setGyrSkewY(Float gyrSkewY) {
        this.gyrSkewY = gyrSkewY;
    }

    public Float getGyrSkewZ() {
        return gyrSkewZ;
    }

    public void setGyrSkewZ(Float gyrSkewZ) {
        this.gyrSkewZ = gyrSkewZ;
    }

    public Float getGyrKurtX() {
        return gyrKurtX;
    }

    public void setGyrKurtX(Float gyrKurtX) {
        this.gyrKurtX = gyrKurtX;
    }

    public Float getGyrKurtY() {
        return gyrKurtY;
    }

    public void setGyrKurtY(Float gyrKurtY) {
        this.gyrKurtY = gyrKurtY;
    }

    public Float getGyrKurtZ() {
        return gyrKurtZ;
    }

    public void setGyrKurtZ(Float gyrKurtZ) {
        this.gyrKurtZ = gyrKurtZ;
    }

    public Float getGyrZeroX() {
        return gyrZeroX;
    }

    public void setGyrZeroX(Float gyrZeroX) {
        this.gyrZeroX = gyrZeroX;
    }

    public Float getGyrZeroY() {
        return gyrZeroY;
    }

    public void setGyrZeroY(Float gyrZeroY) {
        this.gyrZeroY = gyrZeroY;
    }

    public Float getGyrZeroZ() {
        return gyrZeroZ;
    }

    public void setGyrZeroZ(Float gyrZeroZ) {
        this.gyrZeroZ = gyrZeroZ;
    }
}
