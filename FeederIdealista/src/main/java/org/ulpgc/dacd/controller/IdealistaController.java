package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.PropertyFeeder;
import org.ulpgc.dacd.controller.persistence.IdealistaPropertyStore;
import org.ulpgc.dacd.model.IdealistaProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IdealistaController {

    private final PropertyFeeder feeder;
    private final IdealistaPropertyStore store;

    public IdealistaController(PropertyFeeder feeder, IdealistaPropertyStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void execute() throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String locationId = "0-EU-ES-35-01-001-016";
        String locationName = "Las Palmas de Gran Canaria";

        int lastPage = store.getLastPage();
        int nextPage = lastPage + 1;

        List<IdealistaProperty> props = feeder.getProperties(locationId, locationName, nextPage);

        for (IdealistaProperty p : props) {
            p.capturedAt = timestamp;
        }

        store.store(props);
        store.saveLastPage(nextPage);

        System.out.println("Página " + nextPage + " guardada a las " + timestamp);
    }
}
