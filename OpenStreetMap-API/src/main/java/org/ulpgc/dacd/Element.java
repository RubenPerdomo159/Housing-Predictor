package org.ulpgc.dacd;

import java.util.Map;

public class Element {
    private long id;
    private double lat;
    private double lon;
    private Center center; // <--- Añade esto
    private Map<String, String> tags;

    // Clase interna para el centro de los polígonos
    public static class Center {
        public double lat;
        public double lon;
    }

    public long getId() { return id; }

    // Modifica los getters para que si lat/lon son 0, use los del center
    public double getLat() { return lat != 0 ? lat : (center != null ? center.lat : 0); }
    public double getLon() { return lon != 0 ? lon : (center != null ? center.lon : 0); }
    public Map<String, String> getTags() { return tags; }
}