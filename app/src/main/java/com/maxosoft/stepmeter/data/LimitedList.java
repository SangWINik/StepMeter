package com.maxosoft.stepmeter.data;

import java.util.ArrayList;

public class LimitedList<E extends ITimestampedItem> extends ArrayList<E> {
    private Long lengthInMillis;

    public LimitedList(long lengthInMillis) {
        this.lengthInMillis = lengthInMillis;
    }

    @Override
    public boolean add(E e) {
        long entryTime = e.getTime().getTime();
        for (E el: this) {
            if (entryTime - el.getTime().getTime() > this.lengthInMillis) {
                this.remove(el);
            }
        }

        return super.add(e);
    }
}
