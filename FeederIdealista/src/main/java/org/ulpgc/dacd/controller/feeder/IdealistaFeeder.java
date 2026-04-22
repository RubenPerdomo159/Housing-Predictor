package org.ulpgc.dacd.controller.feeder;

import com.google.gson.*;
import org.ulpgc.dacd.model.IdealistaProperty;

import java.util.ArrayList;
import java.util.List;

public class IdealistaFeeder implements PropertyFeeder {

    private final IdealistaApiClient api;
    private final IdealistaPublisher publisher;

    public IdealistaFeeder(IdealistaApiClient api) throws Exception {
        this.api = api;
        this.publisher = new IdealistaPublisher();
    }

    @Override
    public List<IdealistaProperty> getProperties(String locationId, String locationName, int page) throws Exception {

        String url = "https://idealista7.p.rapidapi.com/listhomes?"
                + "locationId=" + locationId
                + "&numPage=" + page
                + "&maxItems=40"
                + "&operation=sale"
                + "&location=es"
                + "&locale=es"
                + "&order=relevance"
                + "&locationName=" + locationName.replace(" ", "%20");

        String json = api.get(url);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray list = root.getAsJsonArray("elementList");

        List<IdealistaProperty> results = new ArrayList<>();

        for (JsonElement el : list) {
            JsonObject obj = el.getAsJsonObject();

            IdealistaProperty p = new IdealistaProperty();
            p.propertyCode = obj.get("propertyCode").getAsString();
            results.add(p);

            // Crear evento JSON
            JsonObject event = new JsonObject();
            event.addProperty("ts", java.time.Instant.now().toString());
            event.addProperty("ss", "IdealistaFeeder");
            event.add("payload", obj);

            // Publicar evento
            publisher.publish(event.toString());
        }

        return results;
    }
}
