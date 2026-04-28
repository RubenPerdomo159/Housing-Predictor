package org.ulpgc.dacd;

import org.ulpgc.dacd.model.Place;

import java.util.Map;

public class OSMMapper {
    public Place toPlace(Element element) {
        Place place = new Place();
        place.lat = element.getLat();
        place.lon = element.getLon();
        Map<String, String> tags = element.getTags();

        place.name = (tags != null) ? tags.getOrDefault("name", "Sin nombre") : "Sin nombre";

        // Intenta obtener el tipo de varias etiquetas comunes
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