package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.feeder.IdealistaPropertyService;
import org.ulpgc.dacd.model.IdealistaProperty;
import org.ulpgc.dacd.persistence.IdealistaPropertyStore;

import java.util.List;

public class IdealistaController {

    private final IdealistaPropertyService service;
    private final IdealistaPropertyStore store;

    public IdealistaController(IdealistaPropertyService service,
                               IdealistaPropertyStore store) {
        this.service = service;
        this.store = store;
    }

    public void run(String locationId, String locationName, int page) throws Exception {
        List<IdealistaProperty> properties =
                service.getProperties(locationId, locationName, page);

        for (IdealistaProperty p : properties) {
            store.save(p);
        }
    }
}

