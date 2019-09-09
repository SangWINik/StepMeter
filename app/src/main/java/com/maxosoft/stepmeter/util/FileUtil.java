package com.maxosoft.stepmeter.util;

import com.maxosoft.stepmeter.data.RawDataEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private static String generateFileName() {
        return new Date() + ".txt";
    }
}
