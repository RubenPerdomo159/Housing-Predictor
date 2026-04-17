package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.FotocasaScraperService;
import org.ulpgc.dacd.controller.persistence.FotocasaPropertyStore;
import org.ulpgc.dacd.model.FotocasaProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FotocasaController {

    private final FotocasaScraperService scraper;
    private final FotocasaPropertyStore store;

    public FotocasaController(FotocasaScraperService scraper, FotocasaPropertyStore store) {
        this.scraper = scraper;
        this.store = store;
    }

    public void execute() throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        int lastPage = store.getLastPage();
        int nextPage = lastPage + 1;

        List<FotocasaProperty> props = scraper.getProperties(nextPage);

        for (FotocasaProperty p : props) {
            p.capturedAt = timestamp;
        }

        store.store(props);
        store.saveLastPage(nextPage);

        System.out.println("Página " + nextPage + " guardada a las " + timestamp);
    }
}
