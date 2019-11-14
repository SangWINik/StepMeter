package com.maxosoft.stepmeter.dto;

import java.util.Date;
import java.util.List;

public class RecordingSessionDto {
    private Long id;
    private Long accountId;
    private String deviceId;
    private Date dateStart;
    private Date dateEnd;
    private List<DataWindowDto> dataWindows;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<DataWindowDto> getDataWindows() {
        return dataWindows;
    }

    public void setDataWindows(List<DataWindowDto> dataWindows) {
        this.dataWindows = dataWindows;
    }
}
