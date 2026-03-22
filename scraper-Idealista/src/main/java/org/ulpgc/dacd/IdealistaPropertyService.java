package org.ulpgc.dacd;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class IdealistaPropertyService {

    private final IdealistaApiClient api;

    public IdealistaPropertyService(IdealistaApiClient api) {
        this.api = api;
    }

    public List<IdealistaProperty> getProperties(String locationId, int page) throws Exception {

        String url = "https://idealista7.p.rapidapi.com/properties/list?"
                + "locationId=" + locationId
                + "&numPage=" + page
                + "&maxItems=40";

        String json = api.get(url);

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray list = root.getAsJsonArray("elementList");

        List<IdealistaProperty> results = new ArrayList<>();

        for (JsonElement el : list) {
            JsonObject obj = el.getAsJsonObject();

            IdealistaProperty p = new IdealistaProperty();

            p.precio = obj.get("price").getAsString();
            p.metros = obj.get("size").getAsString();
            p.habitaciones = obj.get("rooms").getAsString();
            p.ubicacion = obj.get("address").getAsString();
            p.url = "https://www.idealista.com" + obj.get("url").getAsString();

            results.add(p);
        }

        return results;
    }
}
