package org.ulpgc.dacd.controller.feeder;

import com.google.gson.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.ulpgc.dacd.model.FotocasaProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FotocasaScraperService {

    private final FotocasaPublisher publisher;
    private final Gson gson = new Gson();

    public FotocasaScraperService() throws Exception {
        this.publisher = new FotocasaPublisher();
    }

    public List<FotocasaProperty> getProperties(int page) {

        String url = "https://www.fotocasa.es/es/comprar/viviendas/las-palmas-de-gran-canaria/todas-las-zonas/l/" + page;

        List<FotocasaProperty> results = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(100)
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36")
            );

            Page pageObj = context.newPage();

            pageObj.navigate(url);

            pageObj.waitForLoadState(LoadState.NETWORKIDLE);

            try {
                pageObj.locator("button:has-text('Aceptar')").first().click();
            } catch (Exception ignored) {}

            pageObj.waitForSelector("article",
                    new Page.WaitForSelectorOptions().setTimeout(15000));

            for (int i = 0; i < 6; i++) {
                pageObj.mouse().wheel(0, 3000);
                pageObj.waitForTimeout(1000);
            }

            List<ElementHandle> items = pageObj.querySelectorAll("article");

            for (ElementHandle item : items) {

                FotocasaProperty p = new FotocasaProperty();

                try {
                    ElementHandle priceEl = item.querySelector("[data-testid='price'], span:has-text('€')");
                    if (priceEl != null) {
                        p.precio = parseDouble(priceEl.innerText());
                    }
                } catch (Exception e) {
                    p.precio = 0;
                }

                try {
                    String text = item.innerText().toLowerCase();

                    Matcher m2 = Pattern.compile("(\\d+)\\s*m[²2]").matcher(text);
                    if (m2.find()) {
                        p.metros = Double.parseDouble(m2.group(1));
                    }

                    Matcher m3 = Pattern.compile("(\\d+)\\s*hab").matcher(text);
                    if (m3.find()) {
                        p.habitaciones = Integer.parseInt(m3.group(1));
                    }

                    String[] lines = text.split("\n");

                    for (String line : lines) {

                        line = line.trim();

                        if (line.isEmpty()) continue;

                        if (line.matches(".*\\d+\\s*€.*")) continue;

                        if (line.contains("hace") || line.contains("mes") || line.contains("días")) continue;

                        if (line.contains("top") || line.contains("+")) continue;

                        if (line.contains("foto") ||
                                line.contains("video") ||
                                line.contains("tour") ||
                                line.contains("partner") ||
                                line.contains("inmobiliaria") ||
                                line.contains("nuevo") ||
                                line.contains("líder") ||
                                line.contains("ref") ||
                                line.matches("\\d+/\\d+")) {
                            continue;
                        }

                        if (line.equals("·") || line.length() < 3) continue;

                        p.ubicacion = line;
                        break;
                    }

                    if (p.ubicacion == null) {
                        p.ubicacion = "N/A";
                    }

                } catch (Exception e) {
                    p.metros = 0;
                    p.habitaciones = 0;
                    p.ubicacion = "N/A";
                }

                try {
                    ElementHandle linkEl = item.querySelector("a");
                    if (linkEl != null) {
                        String href = linkEl.getAttribute("href");
                        if (href != null && !href.isEmpty()) {
                            p.url = "https://www.fotocasa.es" + href;
                        }
                    }
                } catch (Exception e) {
                    p.url = "";
                }

                if (p.precio == 0 || p.url == null || p.url.isEmpty()) {
                    continue;
                }

                // Añadir a la lista
                results.add(p);

                // Publicar en ActiveMQ
                try {
                    String json = gson.toJson(p);

                    JsonObject wrapper = new JsonObject();
                    wrapper.addProperty("ts", java.time.Instant.now().toString());
                    wrapper.addProperty("ss", "fotocasa");
                    wrapper.add("payload", com.google.gson.JsonParser.parseString(json));

                    publisher.publish(wrapper.toString());

                } catch (Exception e) {
                    System.err.println("Error publicando propiedad: " + e.getMessage());
                }
            }

            browser.close();
        }

        return results;
    }

    private double parseDouble(String text) {
        return Double.parseDouble(text.replaceAll("[^0-9]", ""));
    }
}
