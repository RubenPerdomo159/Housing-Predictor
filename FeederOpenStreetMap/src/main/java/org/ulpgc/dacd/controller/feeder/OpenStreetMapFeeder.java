package org.ulpgc.dacd.controller.feeder;

import com.google.gson.Gson;
import org.ulpgc.dacd.model.OverpassService;
import org.ulpgc.dacd.model.Place;

import java.util.List;

public class OpenStreetMapFeeder {

    private final OverpassService service;
    private final OpenStreetMapPublisher publisher;
    private final Gson gson = new Gson();

    public OpenStreetMapFeeder(OverpassService service, OpenStreetMapPublisher publisher) {
        this.service = service;
        this.publisher = publisher;
    }

    public void fetchAndPublish(double lat, double lon, String amenity, int radius) throws Exception {
        List<Place> places = service.findNearby(lat, lon, amenity, radius);

        for (Place place : places) {
            String json = gson.toJson(place);
            publisher.publish(json);
        }
    }
}
