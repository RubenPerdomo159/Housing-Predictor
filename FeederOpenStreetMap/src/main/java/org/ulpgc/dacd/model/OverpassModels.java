package org.ulpgc.dacd.model;

import java.util.List;
import java.util.Map;

public class OverpassModels {

    public static class Element {
        private long id;
        private double lat;
        private double lon;
        private Center center;
        private Map<String, String> tags;

        public static class Center {
            public double lat;
            public double lon;
        }

        public long getId() { return id; }

        public double getLat() {
            return lat != 0 ? lat : (center != null ? center.lat : 0);
        }

        public double getLon() {
            return lon != 0 ? lon : (center != null ? center.lon : 0);
        }

        public Map<String, String> getTags() { return tags; }
    }

    public static class OverpassResponse {
        private List<Element> elements;
        public List<Element> getElements() { return elements; }
    }
}
