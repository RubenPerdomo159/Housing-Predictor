package org.ulpgc.dacd;

public class Main {
    public static void main(String[] args) throws Exception {

        FotocasaScraperService scraper = new FotocasaScraperService();

        var properties = scraper.getProperties(1);

        properties.forEach(System.out::println);
    }
}