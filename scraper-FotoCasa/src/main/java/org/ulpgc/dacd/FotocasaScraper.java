package org.ulpgc.dacd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FotocasaScraper {

    private static final String URL =
            "https://www.fotocasa.es/es/comprar/viviendas/las-palmas-de-gran-canaria/todas-las-zonas/l";

    public void scrape() {
        try {
            Document doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .header("Accept-Language", "es-ES,es;q=0.9")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Connection", "keep-alive")
                    .cookie("fc_device", "web")
                    .timeout(15000)
                    .get();

            Elements cards = doc.select("div.re-CardPackPremium, div.re-CardPackStandard");

            System.out.println("Encontrados " + cards.size() + " anuncios.");

            for (Element card : cards) {
                String title = card.select(".re-Card-title").text();
                String price = card.select(".re-Card-price").text();
                String link = "https://www.fotocasa.es" + card.select("a[href]").attr("href");

                System.out.println("---------------");
                System.out.println("Título: " + title);
                System.out.println("Precio: " + price);
                System.out.println("URL: " + link);
            }

        } catch (Exception e) {
            System.out.println("Error al hacer scraping:");
            e.printStackTrace();
        }
    }

}

