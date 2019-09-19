package com.maxosoft.stepmeter.util;

import com.maxosoft.stepmeter.data.RawDataEntry;
import com.maxosoft.stepmeter.data.Window;

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

    public static List<Window> getWindowsFromFile(File file, boolean isOwner) {
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
                if (rawEntry.getDate().getTime() - currentWindowStart > Window.WINDOW_OFFSET*1000) {
                    currentWindows.add(new ArrayList<>());
                    currentWindowStart = rawEntry.getDate().getTime();
                }

                for (List<RawDataEntry> cw: currentWindows) {
                    cw.add(rawEntry);
                }

                // deleting complete window
                if (rawEntry.getDate().getTime() - currentWindows.get(0).get(0).getDate().getTime() > Window.WINDOW_SIZE*1000) {
                    Window complete = new Window(currentWindows.get(0), isOwner);
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
            outputStream.write(Window.getFeatureHeader().getBytes());
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

    private static String generateFileName() {
        return new Date() + ".txt";
    }
}
