package com.maxosoft.stepmeter.data;

import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.factory.FeatureSuitFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class ClassificationHelper {

    public static Instances getSourceData(File fileCsv) throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setNominalAttributes("last");
        loader.setFieldSeparator(",");
        loader.setMissingValue("");
        loader.setSource(fileCsv);
        Instances data = loader.getDataSet();
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

    public static List<Window> getWindowsFromFile(File file, boolean isOwner, FeatureSuit featureSuit) {
        List<Window> windows = new ArrayList<>();
        BufferedReader reader = null;
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
                    complete.setDateStart(currentWindows.get(0).get(0).getDate());
                    complete.setDateEnd(currentWindows.get(0).get(currentWindows.get(0).size() - 1).getDate());
                    windows.add(complete);
                    currentWindows.remove(0);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignore) {}
            }
        }
        return windows;
    }

    public static double classifyWindow(Classifier classifier, DataWindowDto windowDto) {
        double result = -1;
        try {
            FeatureSuit featureSuit = FeatureSuitFactory.getDefault(true);
            ArrayList<Attribute> attributeList = new ArrayList<>();

            Map<FeatureProvider.Feature, Attribute> map = new HashMap<>();
            for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
                Attribute attribute = new Attribute(feature.getName());
                map.put(feature, attribute);
                attributeList.add(attribute);
            }

            ArrayList<String> classVal = new ArrayList<>();
            classVal.add("1");
            classVal.add("0");
            attributeList.add(new Attribute("isOwner",classVal));

            // Declare Instances which is required since I want to use classification/Prediction
            Instances dataset = new Instances("whatever", attributeList, 0);
            Instance inst_co = new DenseInstance(dataset.numAttributes());
            for (FeatureProvider.Feature feature: featureSuit.getFeatureList()) {
                inst_co.setValue(map.get(feature), windowDto.getFeature(feature));
            }
            dataset.add(inst_co);
            dataset.setClassIndex(dataset.numAttributes()-1);

            //Will print 0 if it's a "yes", and 1 if it's a "no"
            System.out.println(classifier.classifyInstance(dataset.instance(0)));
            //Here I call dataset.instance(0) since there is only one instance added in the dataset, if you do add another one you can use dataset.instance(0), etc.
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static Instances classify(Classifier classifier, File file) {
        try {


            CSVLoader loader = new CSVLoader();
            loader.setNominalAttributes("last");
            loader.setFieldSeparator(",");
            loader.setMissingValue("");
            loader.setSource(file);
            // load unlabeled data
//            ConverterUtils.DataSource source = new ConverterUtils.DataSource(file.getAbsolutePath());
            Instances unlabeled = loader.getDataSet();

            // set class attribute
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            // label instances
            for (int i = 0; i < unlabeled.numInstances(); i++) {
                double clsLabel = classifier.classifyInstance(unlabeled.instance(i));
                labeled.instance(i).setClassValue(clsLabel);
            }

            return labeled;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
