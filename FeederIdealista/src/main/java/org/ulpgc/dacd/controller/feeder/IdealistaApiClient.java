package org.ulpgc.dacd.controller.feeder;

import okhttp3.*;

public class IdealistaApiClient {

    private static final String API_KEY = "7942162fc6msh5c4f856c980a40bp11e02cjsn152eb3d2a1a8";
    private static final String HOST = "idealista7.p.rapidapi.com";

    private final OkHttpClient client = new OkHttpClient();

    public String get(String url) throws Exception {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        Response response = client.newCall(request).execute();  // ✅ dentro del método
        if (!response.isSuccessful()) {                         // ✅ dentro del método
            throw new Exception("Error HTTP: " + response.code() + " - " + response.body().string());
        }
        return response.body().string();                        // ✅ dentro del método
    }  // ← cierre del método get()

}  // ← cierre de la clase