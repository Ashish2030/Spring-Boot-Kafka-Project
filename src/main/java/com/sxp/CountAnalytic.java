package com.sxp;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class CountAnalytic {


    private ObjectId id;
    private String projectId;
    private String journeyId;
    private String channel;
    private String type;
    @BsonProperty(value = "date")
    private String date;
    private long starts;
    private long aborts;
    private long ends;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStarts() {
        return starts;
    }

    public void setStarts(long starts) {
        this.starts = starts;
    }

    public long getAborts() {
        return aborts;
    }

    public void setAborts(long aborts) {
        this.aborts = aborts;
    }

    public long getEnds() {
        return ends;
    }

    public void setEnds(long ends) {
        this.ends = ends;
    }
}
