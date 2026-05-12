package org.ulpgc.dacd.controller.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

                String text;
                try {
                    text = item.innerText().toLowerCase();
                } catch (Exception e) {
                    text = "";
                }

                try {
                    Matcher m2 = Pattern.compile("(\\d+)\\s*m[²2]").matcher(text);
                    if (m2.find()) p.metros = Double.parseDouble(m2.group(1));

                    Matcher m3 = Pattern.compile("(\\d+)\\s*habs?").matcher(text);
                    if (m3.find()) p.habitaciones = Integer.parseInt(m3.group(1));

                    Matcher m4 = Pattern.compile("(\\d+)\\s*bañ").matcher(text);
                    if (m4.find()) p.bathrooms = Integer.parseInt(m4.group(1));

                    String[] lines = text.split("\n");

                    for (String rawLine : lines) {
                        String line = rawLine.trim().toLowerCase();
                        if (line.isEmpty()) continue;

                        if (p.ubicacion == null) {

                            boolean esUbicacion =
                                    line.contains("calle") ||
                                            line.contains("avenida") ||
                                            line.contains("paseo") ||
                                            line.contains("carretera") ||
                                            line.contains("piso en") ||
                                            line.contains("ático en") ||
                                            line.contains("estudio en") ||
                                            line.contains("casa en") ||
                                            line.contains("chalet en");

                            boolean esBasura =
                                    line.matches("\\d+/\\d+") ||          // 1/47
                                            line.contains("líder de zona") ||
                                            line.contains("alquilado") ||
                                            line.contains("vídeo") ||
                                            line.contains("video") ||
                                            line.contains("foto") ||
                                            line.contains("imagen");

                            if (esUbicacion && !esBasura) {
                                p.ubicacion = rawLine.trim();
                            }
                        }

                        if (p.floor == null &&
                                (line.contains("planta") || line.contains("bajo") || line.contains("ático")
                                        || line.contains("entresuelo") || line.contains("principal"))) {
                            Matcher floorMatch = Pattern.compile("(bajo|ático|\\d+ª? planta|entresuelo|principal)")
                                    .matcher(line);
                            if (floorMatch.find()) {
                                p.floor = floorMatch.group(1);
                            }
                        }

                        if (p.propertyType == null) {
                            if (line.contains("piso")) p.propertyType = "piso";
                            else if (line.contains("ático")) p.propertyType = "ático";
                            else if (line.contains("casa")) p.propertyType = "casa";
                            else if (line.contains("dúplex")) p.propertyType = "dúplex";
                            else if (line.contains("estudio")) p.propertyType = "estudio";
                            else if (line.contains("chalet")) p.propertyType = "chalet";
                        }

                        if (line.contains("exterior")) p.exterior = true;
                        if (line.contains("ascensor")) p.hasLift = true;
                        if (line.contains("piscina")) p.hasSwimmingPool = true;
                        if (line.contains("terraza") || line.contains("balcón") || line.contains("balcon"))
                            p.hasTerrace = true;
                        if (line.contains("aire acondicionado")) p.hasAirConditioning = true;
                        if (line.contains("jardín") || line.contains("jardin")) p.hasGarden = true;
                        if (line.contains("trastero")) p.hasBoxRoom = true;
                        if (line.contains("garaje") || line.contains("parking"))
                            p.hasParkingSpace = true;
                        if (line.contains("obra nueva") || line.contains("promoción") || line.contains("a estrenar"))
                            p.newDevelopment = true;
                    }

                } catch (Exception ignored) {}

                try {
                    ElementHandle linkEl = item.querySelector("a");
                    if (linkEl != null) {
                        String href = linkEl.getAttribute("href");
                        if (href != null && !href.isEmpty()) {
                            p.url = href.startsWith("http")
                                    ? href
                                    : "https://www.fotocasa.es" + href;
                        }
                    }
                } catch (Exception e) {
                    p.url = "";
                }

                if (p.precio == 0 || p.url == null || p.url.isEmpty()) continue;

                results.add(p);

                try {
                    String json = gson.toJson(p);

                    JsonObject wrapper = new JsonObject();
                    wrapper.addProperty("ts", java.time.Instant.now().toString());
                    wrapper.addProperty("ss", "FotocasaScraper");
                    wrapper.add("payload", JsonParser.parseString(json));

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
