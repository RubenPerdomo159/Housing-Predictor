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
                            .setSlowMo(80)
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                            .setViewportSize(1366, 768)
            );

            Page pageObj = context.newPage();
            pageObj.navigate(url);

            // Esperar carga inicial
            try {
                pageObj.waitForLoadState(LoadState.DOMCONTENTLOADED);
                pageObj.waitForTimeout(2000);
            } catch (Exception ignored) {}

            // Aceptar cookies si aparece el banner
            try {
                pageObj.locator("button:has-text('Aceptar'), button:has-text('Acepto'), [id*='accept'], [class*='accept']")
                        .first().click(new Locator.ClickOptions().setTimeout(5000));
                pageObj.waitForTimeout(1000);
            } catch (Exception ignored) {}

            // Esperar a que aparezcan las tarjetas con múltiples selectores posibles
            boolean cardsFound = false;
            String[] waitSelectors = {
                    "article[class*='re-CardPack']",
                    "article[class*='re-Card']",
                    "[class*='re-CardPackMinimal']",
                    "[class*='re-CardPackPremium']",
                    "article",
                    "[class*='Card'][class*='result']"
            };

            for (String sel : waitSelectors) {
                try {
                    pageObj.waitForSelector(sel, new Page.WaitForSelectorOptions().setTimeout(8000));
                    cardsFound = true;
                    System.out.println("Tarjetas encontradas con selector: " + sel);
                    break;
                } catch (Exception ignored) {}
            }

            if (!cardsFound) {
                System.err.println("No se encontraron tarjetas en: " + url);
                browser.close();
                return results;
            }

            // Scroll para cargar contenido lazy
            for (int i = 0; i < 5; i++) {
                pageObj.mouse().wheel(0, 2500);
                pageObj.waitForTimeout(800);
            }

            // Intentar obtener tarjetas con selectores en orden de especificidad
            List<ElementHandle> items = new ArrayList<>();
            String[] cardSelectors = {
                    "article[class*='re-CardPack']",
                    "article[class*='re-Card']",
                    "[class*='re-CardPackMinimal']",
                    "[class*='re-CardPackPremium']",
                    "article"
            };

            for (String sel : cardSelectors) {
                items = pageObj.querySelectorAll(sel);
                if (!items.isEmpty()) {
                    System.out.println("Usando selector '" + sel + "': " + items.size() + " tarjetas");
                    break;
                }
            }

            if (items.isEmpty()) {
                System.err.println("No se extrajeron tarjetas con ningún selector.");
                browser.close();
                return results;
            }

            for (ElementHandle item : items) {
                FotocasaProperty p = new FotocasaProperty();

                // --- Precio ---
                p.precio = extractPrice(item);
                if (p.precio == 0) continue;

                // --- URL ---
                p.url = extractUrl(item);
                if (p.url == null || p.url.isEmpty()) continue;

                // --- propertyCode a partir de la URL ---
                p.propertyCode = "FC-" + Integer.toHexString(p.url.hashCode());

                // --- Texto completo para extracción por regex ---
                String rawText;
                try {
                    rawText = item.innerText();
                } catch (Exception e) {
                    rawText = "";
                }
                String text = rawText.toLowerCase();

                // --- Metros ---
                Matcher mM2 = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*m[²2]").matcher(text);
                if (mM2.find()) {
                    p.metros = Double.parseDouble(mM2.group(1).replace(",", "."));
                }

                // --- Habitaciones ---
                Matcher mHab = Pattern.compile("(\\d+)\\s*hab").matcher(text);
                if (mHab.find()) {
                    p.habitaciones = Integer.parseInt(mHab.group(1));
                }

                // --- Baños ---
                Matcher mBath = Pattern.compile("(\\d+)\\s*ba[ñn]").matcher(text);
                if (mBath.find()) {
                    p.bathrooms = Integer.parseInt(mBath.group(1));
                }

                // --- Dirección / Ubicación ---
                p.ubicacion = extractAddress(item, rawText);

                // --- Planta ---
                Matcher mFloor = Pattern.compile(
                        "(bajo|ático|atico|\\d+\\.?ª?\\s*planta|entresuelo|principal|semisótano|semisotano)",
                        Pattern.CASE_INSENSITIVE
                ).matcher(text);
                if (mFloor.find()) {
                    p.floor = mFloor.group(1).trim();
                }

                // --- Tipo de propiedad ---
                if (text.contains("ático") || text.contains("atico")) p.propertyType = "ático";
                else if (text.contains("chalet")) p.propertyType = "chalet";
                else if (text.contains("dúplex") || text.contains("duplex")) p.propertyType = "dúplex";
                else if (text.contains("estudio")) p.propertyType = "estudio";
                else if (text.contains("casa")) p.propertyType = "casa";
                else if (text.contains("piso")) p.propertyType = "piso";
                else p.propertyType = "piso";

                // --- Características booleanas ---
                p.exterior = text.contains("exterior");
                p.hasLift = text.contains("ascensor");
                p.hasSwimmingPool = text.contains("piscina");
                p.hasTerrace = text.contains("terraza") || text.contains("balcón") || text.contains("balcon");
                p.hasAirConditioning = text.contains("aire acondicionado") || text.contains("a/c") || text.contains("ac ");
                p.hasGarden = text.contains("jardín") || text.contains("jardin");
                p.hasBoxRoom = text.contains("trastero");
                p.hasParkingSpace = text.contains("garaje") || text.contains("parking") || text.contains("aparcamiento");
                p.newDevelopment = text.contains("obra nueva") || text.contains("promoción") || text.contains("a estrenar");

                p.capturedAt = java.time.Instant.now().toString();

                results.add(p);
                publishProperty(p);
            }

            browser.close();
        } catch (Exception e) {
            System.err.println("Error en el scraper de Fotocasa: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Fotocasa: " + results.size() + " propiedades extraídas.");
        return results;
    }

    private double extractPrice(ElementHandle item) {
        // Intentar múltiples selectores para el precio
        String[] priceSelectors = {
                "[class*='re-CardPrice'] span",
                "[class*='CardPrice']",
                "[class*='price']",
                "[data-testid='price']",
                "span[class*='Price']"
        };

        for (String sel : priceSelectors) {
            try {
                ElementHandle el = item.querySelector(sel);
                if (el != null) {
                    String txt = el.innerText().trim();
                    if (!txt.isEmpty() && txt.contains("€")) {
                        return parsePrice(txt);
                    }
                }
            } catch (Exception ignored) {}
        }

        // Fallback: buscar en el texto completo
        try {
            String fullText = item.innerText();
            Matcher m = Pattern.compile("([\\d.]+(?:[.,]\\d+)?)\\s*€").matcher(fullText.replace(".", "").replace(",", ""));
            if (m.find()) {
                return Double.parseDouble(m.group(1).replaceAll("[^\\d]", ""));
            }
        } catch (Exception ignored) {}

        return 0;
    }

    private double parsePrice(String text) {
        try {
            // Eliminar todo excepto dígitos
            String clean = text.replaceAll("[^\\d]", "");
            if (clean.isEmpty()) return 0;
            double val = Double.parseDouble(clean);
            // Sanity check: precios de viviendas en España entre 10.000 y 10.000.000
            if (val < 10000 || val > 10000000) return 0;
            return val;
        } catch (Exception e) {
            return 0;
        }
    }

    private String extractUrl(ElementHandle item) {
        // Buscar el primer enlace que parezca un listing de propiedad
        try {
            List<ElementHandle> links = item.querySelectorAll("a[href]");
            for (ElementHandle link : links) {
                String href = link.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    if (href.contains("/vivienda/") || href.contains("/comprar/") || href.startsWith("/es/")) {
                        return href.startsWith("http") ? href : "https://www.fotocasa.es" + href;
                    }
                }
            }
            // Si no encontramos uno específico, usar el primero
            ElementHandle firstLink = item.querySelector("a[href]");
            if (firstLink != null) {
                String href = firstLink.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    return href.startsWith("http") ? href : "https://www.fotocasa.es" + href;
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String extractAddress(ElementHandle item, String rawText) {
        // Intentar selectores específicos de Fotocasa para la dirección
        String[] addressSelectors = {
                "[class*='re-CardTitle']",
                "[class*='CardTitle']",
                "[class*='CardSubTitle']",
                "[class*='re-CardSubTitle']",
                "[class*='address']",
                "h3", "h2"
        };

        for (String sel : addressSelectors) {
            try {
                ElementHandle el = item.querySelector(sel);
                if (el != null) {
                    String txt = el.innerText().trim();
                    if (!txt.isEmpty() && txt.length() > 3) {
                        return txt;
                    }
                }
            } catch (Exception ignored) {}
        }

        // Fallback: buscar en líneas del texto
        for (String line : rawText.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.length() < 4) continue;
            String lower = trimmed.toLowerCase();
            if (lower.contains("calle") || lower.contains("avenida") || lower.contains("paseo") ||
                    lower.contains("carretera") || lower.contains("urb.") || lower.contains("en ")) {
                if (!lower.contains("€") && !lower.matches(".*\\d+\\s*m[²2].*") && !lower.contains("hab")) {
                    return trimmed;
                }
            }
        }
        return "";
    }

    private void publishProperty(FotocasaProperty p) {
        try {
            JsonObject wrapper = new JsonObject();
            wrapper.addProperty("ts", p.capturedAt);
            wrapper.addProperty("ss", "FotocasaScraper");
            wrapper.add("payload", JsonParser.parseString(gson.toJson(p)));
            publisher.publish(wrapper.toString());
        } catch (Exception e) {
            System.err.println("Error publicando propiedad Fotocasa: " + e.getMessage());
        }
    }
}
