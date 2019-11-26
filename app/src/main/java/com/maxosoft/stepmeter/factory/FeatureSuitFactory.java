package com.maxosoft.stepmeter.factory;

import com.maxosoft.stepmeter.data.FeatureSuit;

public class FeatureSuitFactory {
    public static FeatureSuit getDefault(boolean includeGyroscope) {
        if (includeGyroscope) {
            return FeatureSuit.SUIT_2;
        } else {
            return FeatureSuit.ALL_ACC;
        }
    }
}
