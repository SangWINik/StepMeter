package com.maxosoft.stepmeter.data;

import com.maxosoft.stepmeter.data.FeatureProvider.Feature;

import java.util.Arrays;
import java.util.List;

import static com.maxosoft.stepmeter.data.FeatureProvider.Feature.*;

public enum FeatureSuit {
    MIN_MAX_ACC(MIN_X_ACC, MAX_X_ACC, MIN_Y_ACC, MAX_Y_ACC, MIN_Z_ACC, MAX_Z_ACC),
    GENERAL_CHARACTERISTICS_ACC(MEAN_X_ACC, DEV_X_ACC, VAR_X_ACC, SKEW_X_ACC, KURT_X_ACC, ZERO_X_ACC),
    ZERO_MEAN_ACC(MEAN_X_ACC, ZERO_X_ACC),
    ALL_ACC(MIN_X_ACC, MAX_X_ACC, MIN_Y_ACC, MAX_Y_ACC, MIN_Z_ACC, MAX_Z_ACC,
            MEAN_X_ACC, MEAN_Y_ACC, MEAN_Z_ACC,
            VAR_X_ACC, VAR_Y_ACC, VAR_Z_ACC,
            DEV_X_ACC, DEV_Y_ACC, DEV_Z_ACC,
            SKEW_X_ACC, SKEW_Y_ACC, SKEW_Z_ACC,
            KURT_X_ACC, KURT_Y_ACC, KURT_Z_ACC,
            ZERO_X_ACC, ZERO_Y_ACC, ZERO_Z_ACC);

    private Feature[] features;

    private FeatureSuit(Feature... features) {
        this.features = features;
    }

    public List<Feature> getFeatureList() {
        return Arrays.asList(this.features);
    }
}
