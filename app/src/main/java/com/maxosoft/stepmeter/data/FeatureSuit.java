package com.maxosoft.stepmeter.data;

import com.maxosoft.stepmeter.data.FeatureProvider.Feature;

import java.util.Arrays;
import java.util.List;

import static com.maxosoft.stepmeter.data.FeatureProvider.Feature.*;

public enum FeatureSuit {
    MIN_MAX_ACC(MIN_X_ACC, MAX_X_ACC, MIN_Y_ACC, MAX_Y_ACC, MIN_Z_ACC, MAX_Z_ACC),
    GENERAL_CHARACTERISTICS_ACC(MEAN_X_ACC, DEV_X_ACC, VAR_X_ACC, SKEW_X_ACC, KURT_X_ACC, ZERO_X_ACC),
    ZERO_MEAN(MEAN_X_ACC, ZERO_X_ACC);

    private Feature[] features;

    private FeatureSuit(Feature... features) {
        this.features = features;
    }

    public List<Feature> getFeatureList() {
        return Arrays.asList(this.features);
    }
}
