package com.maxosoft.stepmeter.factory;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;

public class ClassifierFactory {

    public static Classifier getClassifier() {
        return new J48();
    }
}
