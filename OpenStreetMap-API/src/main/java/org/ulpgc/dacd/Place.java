package org.ulpgc.dacd;

public class Place {
    public String name;
    public String type;
    public double lat;
    public double lon;
    @Override
    public String toString() {
        return type + ": " + name + " (" + lat + ", " + lon + ")";
    }
}