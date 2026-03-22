package org.ulpgc.dacd;

public class Main {
    public static void main(String[] args) {
        IdealistaScraper scraper = new IdealistaScraper();
        // Empieza pequeño
        scraper.scrape(1);
    }
}