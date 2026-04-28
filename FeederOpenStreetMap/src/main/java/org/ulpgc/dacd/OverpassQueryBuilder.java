package org.ulpgc.dacd;

import java.util.Locale;

public class OverpassQueryBuilder {

    public String buildAmenityQuery(double lat, double lon, int radius, String amenity) {
        String tag = amenity.equals("bus_stop") ? "highway" : "amenity";

        // [timeout:30] le da margen al servidor
        // nwr busca nodos, caminos y relaciones
        // out center nos da la coordenada central si es un edificio
        return String.format(Locale.US,
                "[out:json][timeout:30];" +
                        "nwr[\"%s\"=\"%s\"](around:%d,%f,%f);" +
                        "out center;",
                tag, amenity, radius, lat, lon);
    }

    public String buildRestaurantsQuery(double lat, double lon, int radius) {
        return buildAmenityQuery(lat, lon, radius, "restaurant");
    }

    public String buildSchoolsQuery(double lat, double lon, int radius) {
        return buildAmenityQuery(lat, lon, radius, "school");
    }

    public String buildTransportQuery(double lat, double lon, int radius) {
        return buildAmenityQuery(lat, lon, radius, "bus_stop");
    }

    public String buildSupermarketsQuery(double lat, double lon, int radius) {
        return String.format(
                "[out:json];node(around:%d,%f,%f)[shop=supermarket];out;",
                radius, lat, lon
        );
    }

}