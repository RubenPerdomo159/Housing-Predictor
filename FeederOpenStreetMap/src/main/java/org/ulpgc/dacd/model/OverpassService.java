package org.ulpgc.dacd.model;

import com.google.gson.Gson;
import org.ulpgc.dacd.controller.feeder.OverpassApiClient;
import org.ulpgc.dacd.model.OverpassModels.*;
import org.ulpgc.dacd.model.Place.Mapper;

import java.util.List;
import java.util.stream.Collectors;

public class OverpassService {

    private final OverpassApiClient api;
    private final Mapper mapper;
    private final Gson gson = new Gson();

    public OverpassService(OverpassApiClient api, Mapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    public List<Place> findNearby(double lat, double lon, String amenity, int radius) throws Exception {

        String query = api.buildAmenityQuery(lat, lon, radius, amenity);

        String json = api.executeQuery(query);

        OverpassResponse response = gson.fromJson(json, OverpassResponse.class);

        return response.getElements().stream()
                .map(mapper::toPlace)
                .collect(Collectors.toList());
    }
}
