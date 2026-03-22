package org.ulpgc.dacd;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class IdealistaParser {

    public static IdealistaProperty parse(WebElement item) {

        IdealistaProperty property = new IdealistaProperty();

        // 💰 Precio
        property.precio = item.findElement(By.cssSelector(".item-price")).getText();

        // 📍 Ubicación + URL
        WebElement link = item.findElement(By.cssSelector(".item-link"));
        property.ubicacion = link.getAttribute("title");
        property.url = "https://www.idealista.com" + link.getAttribute("href");

        // 📊 Detalles
        List<WebElement> detalles = item.findElements(By.cssSelector(".item-detail"));

        for (WebElement detalle : detalles) {
            String text = detalle.getText();

            if (text.contains("hab")) {
                property.habitaciones = text;
            } else if (text.contains("m²")) {
                property.metros = text;
            } else if (text.toLowerCase().contains("planta")) {
                property.planta = text;
            }
        }

        return property;
    }
}
