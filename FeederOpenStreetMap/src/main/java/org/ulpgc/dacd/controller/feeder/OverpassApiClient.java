package org.ulpgc.dacd.controller.feeder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class OverpassApiClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl = "https://overpass.kumi.systems/api/interpreter";

    public String executeQuery(String query) throws Exception {

        int maxRetries = 3;
        int delay = 2000;

        for (int i = 0; i < maxRetries; i++) {

            String body = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            int code = response.statusCode();

            if (code == 200) return response.body();

            if (code == 429 || code == 504) {
                Thread.sleep(delay);
                continue;
            }

            throw new Exception("Error HTTP: " + code + "\n" + response.body());
        }

        throw new Exception("No se pudo completar la petición");
    }

    public String buildAmenityQuery(double lat, double lon, int radius, String amenity) {
        String tag = amenity.equals("bus_stop") ? "highway" : "amenity";

        return String.format(Locale.US,
                "[out:json][timeout:30];" +
                        "nwr[\"%s\"=\"%s\"](around:%d,%f,%f);" +
                        "out center;",
                tag, amenity, radius, lat, lon);
    }
}