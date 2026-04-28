package org.ulpgc.dacd;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OverpassClient {

    private final HttpClient httpClient;
    private final String baseUrl = "https://overpass.kumi.systems/api/interpreter";

    public OverpassClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String executeQuery(String query) throws Exception {

        int maxRetries = 3;
        int delay = 2000;

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        for (int i = 0; i < maxRetries; i++) {

            String body = "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body)) // Usar la variable 'body'
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            int code = response.statusCode();

            // ✅ ÉXITO
            if (code == 200) {
                return response.body();
            }

            // ❗ solo retry en estos casos
            if (code == 429 || code == 504) {
                System.out.println("Retry " + (i + 1));
                Thread.sleep(delay);
                continue;
            }

            // ❌ cualquier otro error → romper
            throw new Exception("Error HTTP: " + code + "\n" + response.body());
        }

        throw new Exception("No se pudo completar la petición");
    }
}