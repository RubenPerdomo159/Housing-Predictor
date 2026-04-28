package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.OpenStreetMapFeeder;

public class OpenStreetMapController {

    private final OpenStreetMapFeeder feeder;

    public OpenStreetMapController(OpenStreetMapFeeder feeder) {
        this.feeder = feeder;
    }

    public void execute() throws Exception {
        // Aquí defines qué quieres pedir a OSM
        double lat = 28.1235;
        double lon = -15.4363;
        int radius = 500;
        String amenity = "restaurant";

        feeder.fetchAndPublish(lat, lon, amenity, radius);
    }
}
