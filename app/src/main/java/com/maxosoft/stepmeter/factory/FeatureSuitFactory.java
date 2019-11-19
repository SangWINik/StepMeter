package com.maxosoft.stepmeter.factory;

import com.maxosoft.stepmeter.data.FeatureSuit;

public class FeatureSuitFactory {
    public static FeatureSuit getDefault(boolean includeGyroscope) {
        if (includeGyroscope) {
            return FeatureSuit.ALL;
        } else {
            return FeatureSuit.ALL_ACC;
        }
    }
}
