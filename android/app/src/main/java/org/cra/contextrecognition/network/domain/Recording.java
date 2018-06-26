package org.cra.contextrecognition.network.domain;

import org.cra.contextrecognition.model.State;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Recording {
    private List<GyroRecord> data;
    private Date date;
    private State type;

    public List<GyroRecord> getData() {
        return data;
    }

    public void setData(List<GyroRecord> data) {
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public State getType() {
        return type;
    }

    public void setType(State type) {
        this.type = type;
    }


}
