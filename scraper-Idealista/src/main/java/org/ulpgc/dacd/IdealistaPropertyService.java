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
        // listcommercialproperties funciona perfe
        String url = "https://idealista7.p.rapidapi.com/listhomes?"
                + "locationId=" + locationId
                + "&numPage=" + page
                + "&maxItems=40"
                + "&operation=sale"
                + "&location=es"
                + "&locale=es"
                + "&order=relevance"
                + "&locationName=Las%20Palmas%20de%20Gran%20Canaria";

        String json = api.get(url);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray list = root.getAsJsonArray("elementList");

        List<IdealistaProperty> results = new ArrayList<>();

        for (JsonElement el : list) {
            JsonObject obj = el.getAsJsonObject();

            IdealistaProperty p = new IdealistaProperty(); // ✅ Ahora sí tiene constructor vacío

            if (obj.has("priceInfo") && obj.getAsJsonObject("priceInfo").has("price")) {
                p.precio = obj.has("price") ? obj.get("price").getAsDouble() : 0;
            } else {
                p.precio = 0;
            }
            p.metros      = obj.has("size")    ? obj.get("size").getAsDouble()    : 0;
            p.habitaciones = obj.has("rooms") ? obj.get("rooms").getAsInt() : 0;
            p.ubicacion   = obj.has("address") ? obj.get("address").getAsString() : "N/A";
            p.url = obj.has("url") ? obj.get("url").getAsString() : "";
            results.add(p);
        }

        return results;


    }
}