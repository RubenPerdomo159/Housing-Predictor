package org.ulpgc.dacd;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.ArrayList;
import java.util.List;

public class IdealistaScraper {

    private static final String BASE_URL =
            "https://www.idealista.com/venta-viviendas/las-palmas-de-gran-canaria-las-palmas/";

    public List<IdealistaProperty> scrape(int numPages) {

        List<IdealistaProperty> results = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {

            for (int i = 1; i <= numPages; i++) {

                String url = (i == 1)
                        ? BASE_URL
                        : BASE_URL + "pagina-" + i + ".htm";

                System.out.println("Scrapeando: " + url);

                driver.get(url);

                // Esperar a que cargue la página
                Thread.sleep(5000);

                // 📜 SCROLL (simular usuario)
                JavascriptExecutor js = (JavascriptExecutor) driver;

                js.executeScript("window.scrollBy(0, 400)");
                Thread.sleep(3000);

                js.executeScript("window.scrollBy(0, 800)");
                Thread.sleep(4000);

                js.executeScript("window.scrollBy(0, 1200)");
                Thread.sleep(5000);

                List<WebElement> items = driver.findElements(By.cssSelector("article.item"));

                for (WebElement item : items) {
                    try {
                        IdealistaProperty property = IdealistaParser.parse(item);
                        results.add(property);
                        System.out.println(property);
                    } catch (Exception e) {
                        System.out.println("Error parseando un item");
                    }
                }

                int delay = 30000 + (int)(Math.random() * 60000); // 30–90 segundos
                System.out.println("Esperando " + delay + " ms...");
                Thread.sleep(delay);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return results;
    }
}
