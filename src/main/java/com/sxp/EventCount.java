package com.sxp;

public class EventCount {
    long startCount = 0;
    long endCount = 0;
    long abortedCount = 0;

    @Override
    public String toString() {
        return "EventCount{" +
                "startCount=" + startCount +
                ", endCount=" + endCount +
                ", abortedCount=" + abortedCount +
                '}';
    }

    public long getStartCount() {
        return startCount;
    }

    public void setStartCount(long startCount) {
        this.startCount = startCount;
    }

    public long getEndCount() {
        return endCount;
    }

    public void setEndCount(long endCount) {
        this.endCount = endCount;
    }

    public long getAbortedCount() {
        return abortedCount;
    }

    public void setAbortedCount(long abortedCount) {
        this.abortedCount = abortedCount;
    }
}
