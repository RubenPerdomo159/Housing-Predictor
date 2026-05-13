package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.FotocasaScraperService;
import org.ulpgc.dacd.model.FotocasaProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FotocasaController {

    private final FotocasaScraperService scraper;

    public FotocasaController(FotocasaScraperService scraper) {
        this.scraper = scraper;
    }

    public void execute() throws Exception {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        int page = 1;

        List<FotocasaProperty> props = scraper.getProperties(page);

        for (FotocasaProperty p : props) {
            p.capturedAt = timestamp;
        }

        System.out.println(
                "Se obtuvieron " + props.size() +
                        " propiedades a las " + timestamp
        );
    }
}