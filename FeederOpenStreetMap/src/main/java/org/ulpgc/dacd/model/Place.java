package org.ulpgc.dacd.model;

import org.ulpgc.dacd.model.OverpassModels.Element;
import java.util.Map;

public class Place {

    public String name;
    public String type;
    public double lat;
    public double lon;

    @Override
    public String toString() {
        return type + ": " + name + " (" + lat + ", " + lon + ")";
    }

    public static class Mapper {

        public Place toPlace(Element element) {
            Place place = new Place();
            place.lat = element.getLat();
            place.lon = element.getLon();

            Map<String, String> tags = element.getTags();

            place.name = (tags != null)
                    ? tags.getOrDefault("name", "Sin nombre")
                    : "Sin nombre";

            if (tags != null) {
                place.type = tags.getOrDefault("amenity",
                        tags.getOrDefault("highway",
                                tags.getOrDefault("shop", "unknown")));
            } else {
                place.type = "unknown";
            }

            return place;
        }
    }
}