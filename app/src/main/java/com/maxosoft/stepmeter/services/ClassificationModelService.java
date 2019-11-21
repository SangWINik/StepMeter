package com.maxosoft.stepmeter.services;

import android.content.Context;

import com.maxosoft.stepmeter.api.DataApiService;
import com.maxosoft.stepmeter.data.ClassificationHelper;
import com.maxosoft.stepmeter.dto.DataWindowDto;
import com.maxosoft.stepmeter.factory.ClassifierFactory;
import com.maxosoft.stepmeter.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class ClassificationModelService {
    private static final String MODEL_FILE_NAME = "/currentModel.model";

    private DataApiService dataApiService;

    private Context context;
    private String modelPath;

    public ClassificationModelService(Context context) {
        this.context = context;
        this.modelPath = context.getFilesDir().getAbsolutePath() + "/model";

        this.dataApiService = new DataApiService(this.context);
    }

    public Classifier getModel() {
        try {
            if (new File(modelPath + MODEL_FILE_NAME).exists()) {
                return (Classifier) SerializationHelper.read(modelPath + MODEL_FILE_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Classifier saveOrUpdateModelForAccount(Long accountId) {
        try {
            List<DataWindowDto> ownerData = dataApiService.getDataWindowsForAccount(accountId);
            List<DataWindowDto> otherData = dataApiService.getDataWindowsExceptAccount(accountId);
            Classifier classifier = ClassifierFactory.getClassifier();
            File tmpDataFile = FileUtil.createDataFile(this.context.getFilesDir().getAbsolutePath(), ownerData, otherData);

            Instances data = ClassificationHelper.getSourceData(tmpDataFile);
            classifier.buildClassifier(data);

            Evaluation evaluation = new Evaluation(data);
            evaluation.crossValidateModel(classifier, data, 10, new Random());
            System.out.println(classifier.getClass().toString() + "evaluation");
            System.out.println(evaluation.toSummaryString());

            this.saveModel(classifier);
            tmpDataFile.delete();

            return classifier;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getModelLastUpdateDate() {
        File file = new File(modelPath + MODEL_FILE_NAME);
        if (file.exists()) {
            return new Date(file.lastModified());
        }
        return null;
    }

    private void saveModel(Classifier classifier) throws Exception {
        new File(modelPath).mkdirs();
        new File(modelPath + MODEL_FILE_NAME).createNewFile();
        SerializationHelper.write(modelPath + MODEL_FILE_NAME, classifier);
    }
}
