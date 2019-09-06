package com.maxosoft.stepmeter.util;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class FileUtil {

    public static FileOutputStream openFileOutputStream() {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(generateFileName());
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

    private static String generateFileName() {
        return new Date().getTime() + ".txt";
    }
}
