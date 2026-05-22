package org.ulpgc.dacd.model;
import java.time.LocalDateTime;

public class Event {
    private Payload payload;
    private transient LocalDateTime lastSeen;

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }

    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
}

