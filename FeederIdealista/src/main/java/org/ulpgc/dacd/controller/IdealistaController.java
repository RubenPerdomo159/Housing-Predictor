package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.IdealistaPropertyFeeder;
import org.ulpgc.dacd.controller.persistence.IdealistaPropertyStore;
import org.ulpgc.dacd.model.IdealistaProperty;

import java.util.List;

public class IdealistaController {

    private final IdealistaPropertyFeeder feeder;
    private final IdealistaPropertyStore store;

    public IdealistaController(IdealistaPropertyFeeder feeder, IdealistaPropertyStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void execute() throws Exception {

        String locationId = "0-EU-ES-35-01-001-016";
        String locationName = "Las Palmas de Gran Canaria";

        for (int page = 1; page <= 3; page++) {
            List<IdealistaProperty> props = feeder.getProperties(locationId, locationName, page);
            store.store(props);
            System.out.println("Página " + page + " guardada.");
        }
    }
}
