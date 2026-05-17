package org.ulpgc.dacd.model;


public class Event {
    private String ts;
    private String ss;
    private Payload payload;

    public String getSs() {
        return ss;
    }

    public String getTs() {
        return ts;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public String getSource() {
        return ss;
    }
}

