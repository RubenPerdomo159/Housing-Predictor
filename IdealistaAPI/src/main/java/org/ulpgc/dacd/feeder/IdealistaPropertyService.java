package org.ulpgc.dacd.feeder;

import com.google.gson.*;
import org.ulpgc.dacd.model.IdealistaProperty;

import java.util.ArrayList;
import java.util.List;

public class IdealistaPropertyService {

    private final IdealistaApiClient api;

    public IdealistaPropertyService(IdealistaApiClient api) {
        this.api = api;
    }




    // listcommercialproperties funciona perfe



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

            p.precio      = obj.has("price") ? obj.get("price").getAsDouble() : 0;
            p.precioM2    = obj.has("priceByArea") ? obj.get("priceByArea").getAsDouble() : 0;
            p.metros      = obj.has("size") ? obj.get("size").getAsDouble() : 0;
            p.habitaciones = obj.has("rooms") ? obj.get("rooms").getAsInt() : 0;
            p.bathrooms   = obj.has("bathrooms") ? obj.get("bathrooms").getAsInt() : 0;
            p.floor       = obj.has("floor") ? obj.get("floor").getAsString() : "N/A";
            p.exterior    = obj.has("exterior") && obj.get("exterior").getAsBoolean();
            p.propertyType = obj.has("propertyType") ? obj.get("propertyType").getAsString() : "N/A";
            p.status      = obj.has("status") ? obj.get("status").getAsString() : "N/A";
            p.address     = obj.has("address") ? obj.get("address").getAsString() : "N/A";
            p.neighborhood = obj.has("neighborhood") ? obj.get("neighborhood").getAsString() : "N/A";
            p.district    = obj.has("district") ? obj.get("district").getAsString() : "N/A";
            p.municipality = obj.has("municipality") ? obj.get("municipality").getAsString() : "N/A";
            p.province    = obj.has("province") ? obj.get("province").getAsString() : "N/A";
            p.locationId  = obj.has("locationId") ? obj.get("locationId").getAsString() : "N/A";
            p.latitude    = obj.has("latitude") ? obj.get("latitude").getAsDouble() : 0;
            p.longitude   = obj.has("longitude") ? obj.get("longitude").getAsDouble() : 0;
            p.hasLift     = obj.has("hasLift") && obj.get("hasLift").getAsBoolean();
            p.newDevelopment = obj.has("newDevelopment") && obj.get("newDevelopment").getAsBoolean();
            p.url         = obj.has("url") ? obj.get("url").getAsString() : "";

// Precio anterior y bajada
            if (obj.has("priceInfo")) {
                JsonObject priceInfo = obj.getAsJsonObject("priceInfo");
                if (priceInfo.has("price")) {
                    JsonObject priceObj = priceInfo.getAsJsonObject("price");
                    if (priceObj.has("priceDropInfo")) {
                        JsonObject drop = priceObj.getAsJsonObject("priceDropInfo");
                        p.precioAnterior  = drop.has("formerPrice") ? drop.get("formerPrice").getAsDouble() : 0;
                        p.bajadaPrecio    = drop.has("priceDropValue") ? drop.get("priceDropValue").getAsDouble() : 0;
                        p.bajadaPorcentaje = drop.has("priceDropPercentage") ? drop.get("priceDropPercentage").getAsDouble() : 0;
                    }
                }
            }

// Features
            if (obj.has("features")) {
                JsonObject features = obj.getAsJsonObject("features");
                p.hasSwimmingPool   = features.has("hasSwimmingPool") && features.get("hasSwimmingPool").getAsBoolean();
                p.hasTerrace        = features.has("hasTerrace") && features.get("hasTerrace").getAsBoolean();
                p.hasAirConditioning = features.has("hasAirConditioning") && features.get("hasAirConditioning").getAsBoolean();
                p.hasGarden         = features.has("hasGarden") && features.get("hasGarden").getAsBoolean();
                p.hasBoxRoom        = features.has("hasBoxRoom") && features.get("hasBoxRoom").getAsBoolean();
            }

// Parking
            if (obj.has("parkingSpace")) {
                JsonObject parking = obj.getAsJsonObject("parkingSpace");
                p.hasParkingSpace = parking.has("hasParkingSpace") && parking.get("hasParkingSpace").getAsBoolean();
            }

            results.add(p); // ← ESTA LÍNEA FALTABA
        }

        return results;


    }
}