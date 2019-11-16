package com.maxosoft.stepmeter.util;

import com.maxosoft.stepmeter.data.FeatureSuit;
import com.maxosoft.stepmeter.data.RawDataEntry;
import com.maxosoft.stepmeter.data.Window;
import com.maxosoft.stepmeter.dto.DataWindowDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class FileUtil {

    public static FileOutputStream openFileOutputStream(String filesDir) {
        FileOutputStream outputStream = null;
        try {
            new File(filesDir + "/collect").mkdirs();
            File file = new File(filesDir + "/collect/" + generateFileName());
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream;
    }

    public static void writeLine(FileOutputStream outputStream, String content) {
        try {
            String contentLine = content + "\n";
            outputStream.write(contentLine.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeFileOutputStream(FileOutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File[] getFilesFromDirectory(String directory) {
        File dir = new File(directory);
        return dir.listFiles();
    }

    public static List<RawDataEntry> getRawDataFromFile(File file) {
        List<RawDataEntry> entries = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null && !line.equals("interrupted")) {
                entries.add(new RawDataEntry(line));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static File createCSVFile(String filesDir, List<Window> dataWindows) {
        FileOutputStream outputStream = null;
        try {
            new File(filesDir + "/data/All").mkdirs();
            File file = new File(filesDir + "/data/All/data.csv");
            outputStream = new FileOutputStream(file);
            outputStream.write(Window.getFeatureHeader(dataWindows.get(0).getFeatureSuit()).getBytes());
            for (Window window: dataWindows) {
                outputStream.write(window.getFeaturesLine().getBytes());
            }
            outputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createDataFile(String filesDir, List<DataWindowDto> userData, List<DataWindowDto> otherData) {
        FileOutputStream outputStream = null;
        try {
            FeatureSuit featureSuit = FeatureSuit.ALL_ACC;
            long countWithGyr = userData.stream().filter(DataWindowDto::includesGyroscope).count();
            if ((float) countWithGyr / userData.size() >= 0.5) {
                featureSuit = FeatureSuit.ALL;
                userData = userData.stream().filter(DataWindowDto::includesGyroscope).collect(Collectors.toList());
                otherData = otherData.stream().filter(DataWindowDto::includesGyroscope).collect(Collectors.toList());
            }

            new File(filesDir + "/data/All").mkdirs();
            File file = new File(filesDir + "/data/All/data.csv");
            outputStream = new FileOutputStream(file);
            outputStream.write(DataWindowDto.getHeader(featureSuit).getBytes());
            for (DataWindowDto window: userData) {
                outputStream.write(window.getCommaSeparated(true, featureSuit).getBytes());
            }
            for (DataWindowDto window: otherData) {
                outputStream.write(window.getCommaSeparated(false, featureSuit).getBytes());
            }
            outputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String generateFileName() {
        return new Date() + ".txt";
    }
}
