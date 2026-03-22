package org.ulpgc.dacd;

import com.google.gson.*;

public class IdealistaLocationService {

    private final IdealistaApiClient api;

    public IdealistaLocationService(IdealistaApiClient api) {
        this.api = api;
    }

    public String getLocationId(String prefix) throws Exception {

        String url = "https://idealista7.p.rapidapi.com/auto-complete?prefix="
                + prefix + "&location=es&propertyType=homes";

        String json = api.get(url);

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray locations = root.getAsJsonArray("locations");

        return locations.get(0).getAsJsonObject().get("locationId").getAsString();
    }
}
