package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.FotocasaScraperService;
import org.ulpgc.dacd.model.FotocasaProperty;

import java.util.List;

public class FotocasaController {

    private final FotocasaScraperService scraper;

    public FotocasaController(FotocasaScraperService scraper) {
        this.scraper = scraper;
    }

    public void execute() throws Exception {

        String timestamp = java.time.Instant.now().toString();

        int page = 1;

        List<FotocasaProperty> props = scraper.getProperties(page);

        System.out.println(
                "Se obtuvieron " + props.size() +
                        " propiedades a las " + timestamp
        );
    }
}