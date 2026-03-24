package org.ulpgc.dacd;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

public class FotocasaScraperService {

    public List<FotocasaProperty> getProperties(int page) throws Exception {

        String url = "https://www.fotocasa.es/es/comprar/viviendas/las-palmas-de-gran-canaria/todas-las-zonas/l/" + page;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        // ⏳ Espera a que cargue JS
        Thread.sleep(5000);

        List<FotocasaProperty> results = new ArrayList<>();

        List<WebElement> items = driver.findElements(By.cssSelector("article"));

        for (WebElement item : items) {

            FotocasaProperty p = new FotocasaProperty();

            try {
                // Precio
                String precioText = item.findElement(By.cssSelector("[data-testid='price']")).getText();
                p.precio = parseDouble(precioText);
            } catch (Exception e) {
                p.precio = 0;
            }

            try {
                // Features (metros + habitaciones)
                String features = item.findElement(By.cssSelector("[data-testid='features']")).getText();

                if (features.contains("m²")) {
                    p.metros = extractNumber(features);
                }

                if (features.contains("hab")) {
                    p.habitaciones = (int) extractNumber(features);
                }

            } catch (Exception e) {
                p.metros = 0;
                p.habitaciones = 0;
            }

            try {
                // Ubicación
                p.ubicacion = item.findElement(By.cssSelector("[data-testid='location']")).getText();
            } catch (Exception e) {
                p.ubicacion = "N/A";
            }

            try {
                // URL
                String href = item.findElement(By.cssSelector("a")).getAttribute("href");
                p.url = href;
            } catch (Exception e) {
                p.url = "";
            }

            results.add(p);
        }

        driver.quit();

        return results;
    }

    // 🔧 Helpers

    private double parseDouble(String text) {
        return Double.parseDouble(text.replaceAll("[^0-9]", ""));
    }

    private double extractNumber(String text) {
        String num = text.replaceAll("[^0-9]", " ").trim().split(" ")[0];
        return Double.parseDouble(num);
    }
}