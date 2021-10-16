package com.sxp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FluentBitLog {

    float timestamp;
    String log;

    @JsonProperty("@timestamp")
    public float getTimestamp() {
        return timestamp;
    }

    @JsonProperty("@timestamp")
    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
