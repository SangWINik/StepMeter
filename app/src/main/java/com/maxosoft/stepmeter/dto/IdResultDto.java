package com.maxosoft.stepmeter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IdResultDto implements Serializable {
    private boolean isUser;
    private int windowCount;
    private int successCount;
    private Date startDate;
    private Date endDate;

    @JsonIgnore
    public String getStartDateFormatted() {
        String date = null;
        if (startDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return dateFormat.format(startDate);
        }
        return date;
    }

    @JsonIgnore
    public String getEndDateFormatted() {
        String date = null;
        if (endDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return dateFormat.format(endDate);
        }
        return date;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public int getWindowCount() {
        return windowCount;
    }

    public void setWindowCount(int windowCount) {
        this.windowCount = windowCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
