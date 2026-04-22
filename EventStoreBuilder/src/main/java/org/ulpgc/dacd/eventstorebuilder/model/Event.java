package org.ulpgc.dacd.eventstorebuilder.model;

public class Event {
    private String ts;
    private String ss;

    // El resto del payload se guarda como JSON crudo
    private String payload;

    public Event(String ts, String ss, String payload) {
        this.ts = ts;
        this.ss = ss;
        this.payload = payload;
    }

    public String getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public String getPayload() {
        return payload;
    }
}
