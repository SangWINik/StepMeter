package com.maxosoft.stepmeter.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class ClassificationHelper {

    public static Instances getSourceData(File fileCsv) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(fileCsv.getAbsolutePath());
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    public static Evaluation getClassificationResults(Classifier classifier, Instances data) throws Exception {
        classifier.buildClassifier(data);
        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(classifier, data, 10, new Random(1));
        return evaluation;
    }

    public static List<Window> getAllWindows(File[] ownerFiles, File[] otherFiles, FeatureSuit featureSuit) {
        List<Window> allWindows = new ArrayList<>();
        for (File file : ownerFiles) {
            allWindows.addAll(ClassificationHelper.getWindowsFromFile(file, true, featureSuit));
        }
        for (File file : otherFiles) {
            allWindows.addAll(ClassificationHelper.getWindowsFromFile(file, false, featureSuit));
        }

        return allWindows;
    }

    private static List<Window> getWindowsFromFile(File file, boolean isOwner, FeatureSuit featureSuit) {
        List<Window> windows = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));

            List<List<RawDataEntry>> currentWindows = new ArrayList<>();
            currentWindows.add(new ArrayList<>());

            String line = reader.readLine();
            RawDataEntry rawEntry = new RawDataEntry(line);
            long currentWindowStart = rawEntry.getDate().getTime();
            while (line != null && !line.isEmpty() && !line.equals("interrupted")) {
                rawEntry = new RawDataEntry(line);

                // adding next window
                if (rawEntry.getDate().getTime() - currentWindowStart > Window.WINDOW_OFFSET * 1000) {
                    currentWindows.add(new ArrayList<>());
                    currentWindowStart = rawEntry.getDate().getTime();
                }

                for (List<RawDataEntry> cw : currentWindows) {
                    cw.add(rawEntry);
                }

                // deleting complete window
                if (rawEntry.getDate().getTime() - currentWindows.get(0).get(0).getDate().getTime() > Window.WINDOW_SIZE * 1000) {
                    Window complete = new Window(currentWindows.get(0), isOwner, featureSuit);
                    windows.add(complete);
                    currentWindows.remove(0);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windows;
    }
}
