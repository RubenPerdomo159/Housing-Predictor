package org.ulpgc.dacd;

import okhttp3.*;

public class IdealistaApiClient {

    private static final String API_KEY = "TU_API_KEY_DE_RAPIDAPI";
    private static final String HOST = "idealista7.p.rapidapi.com";

    private final OkHttpClient client = new OkHttpClient();

    public String get(String url) throws Exception {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
