package org.ulpgc.dacd;

import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;

public class OverpassService {

    private final OverpassClient client;
    private final OverpassQueryBuilder queryBuilder;
    private final OSMMapper mapper;
    private final Gson gson = new Gson();

    public OverpassService(OverpassClient client, OverpassQueryBuilder queryBuilder, OSMMapper mapper) {
        this.client = client;
        this.queryBuilder = queryBuilder;
        this.mapper = mapper;
    }

    public List<Place> findNearby(double lat, double lon, String amenity, int radius) throws Exception {
        String query = queryBuilder.buildAmenityQuery(lat, lon, radius, amenity);

        String json = client.executeQuery(query);
        OverpassResponse response = gson.fromJson(json, OverpassResponse.class);
        return response.getElements().stream()
                .map(mapper::toPlace)
                .collect(Collectors.toList());
    }

    public int countNearby(double lat, double lon, String amenity, int radius) throws Exception {
        return findNearby(lat, lon, amenity, radius).size();
    }
}