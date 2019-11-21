package com.maxosoft.stepmeter.data;

import java.util.ArrayList;
import java.util.List;

public class LimitedList<E extends ITimestampedItem> extends ArrayList<E> {
    private Long lengthInMillis;

    public LimitedList(long lengthInMillis) {
        this.lengthInMillis = lengthInMillis;
    }

    @Override
    public boolean add(E e) {
        long entryTime = e.getTime().getTime();
        List<E> toRemove = new ArrayList<>();
        for (E el: this) {
            if (entryTime - el.getTime().getTime() > this.lengthInMillis) {
                toRemove.add(el);
            }
        }

        toRemove.forEach(this::remove);

        return super.add(e);
    }
}
