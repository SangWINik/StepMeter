package com.maxosoft.stepmeter.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RawDataCollection {
    private static final int WINDOW_SIZE = 5; // window size in seconds
    private static final int WINDOW_OFFSET = 2; // offset in seconds

    private List<RawDataEntry> rawDataEntryList;

    public RawDataCollection() {}

    public RawDataCollection(List<RawDataEntry> entries) {
        this.rawDataEntryList = entries;
    }

    public List<Window> getWindows() {
        List<Window> windows = new ArrayList<>();
        if (rawDataEntryList != null && !rawDataEntryList.isEmpty()) {
            Collections.sort(rawDataEntryList, new Comparator<RawDataEntry>() {
                @Override
                public int compare(RawDataEntry rawDataEntry, RawDataEntry t1) {
                    return rawDataEntry.getDate().compareTo(t1.getDate());
                }
            });
            long dataStart = rawDataEntryList.get(0).getDate().getTime();
            long dataEnd = rawDataEntryList.get(rawDataEntryList.size() - 1).getDate().getTime();
            long currentStart = dataStart;
            while (currentStart + WINDOW_SIZE*1000 < dataEnd) {
                List<RawDataEntry> forWindow = this.getForWindow(currentStart, currentStart + WINDOW_SIZE*1000);
                windows.add(new Window(forWindow));

                currentStart += WINDOW_OFFSET*1000;
            }
        }

        return windows;
    }

    public List<RawDataEntry> getRawDataEntryList() {
        return rawDataEntryList;
    }

    public void setRawDataEntryList(List<RawDataEntry> rawDataEntryList) {
        this.rawDataEntryList = rawDataEntryList;
    }

    private List<RawDataEntry> getForWindow(long start, long end) {
        List<RawDataEntry> entries = new ArrayList<>();
        for (RawDataEntry entry: rawDataEntryList) {
            if (entry.getDate().getTime() >= start && entry.getDate().getTime() <= end) {
                entries.add(entry);
            }
            if (entry.getDate().getTime() > end) {
                break;
            }
        }
        return entries;
    }
}
