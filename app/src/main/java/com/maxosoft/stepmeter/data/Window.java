package com.maxosoft.stepmeter.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Window {
    public static final int WINDOW_SIZE = 5; // window size in seconds
    public static final int WINDOW_OFFSET = 2; // offset in seconds

    private boolean isOwner;
    private FeatureSuit featureSuit;
    private Map<String, Float> features;

    public Window(List<RawDataEntry> allEntries, boolean isOwner, FeatureSuit featureSuit) {
        this.featureSuit = featureSuit;
        this.isOwner = isOwner;
        this.features = new HashMap<>();
        for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
            this.features.put(feature.name(), feature.getValue(allEntries));
        }
    }

    public static String getFeatureHeader(FeatureSuit featureSuit) {
        StringBuilder line = new StringBuilder();
        for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
            line.append(feature.name()).append(",");
        }
        line.append("res").append("\n");
        return line.toString();
    }

    public String getFeaturesLine() {
        StringBuilder line = new StringBuilder();
        for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
            line.append(features.get(feature.name())).append(",");
        }
        line.append(isOwner).append("\n");
        return line.toString();
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public FeatureSuit getFeatureSuit() {
        return featureSuit;
    }

    public void setFeatureSuit(FeatureSuit featureSuit) {
        this.featureSuit = featureSuit;
    }

    public Map<String, Float> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Float> features) {
        this.features = features;
    }
}
