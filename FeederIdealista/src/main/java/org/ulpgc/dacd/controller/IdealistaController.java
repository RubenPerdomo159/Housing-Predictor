package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.controller.feeder.PropertyFeeder;
import org.ulpgc.dacd.model.IdealistaProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IdealistaController {

    private final PropertyFeeder feeder;

    public IdealistaController(PropertyFeeder feeder) {
        this.feeder = feeder;
    }

    public void execute() throws Exception {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String locationId = "0-EU-ES-35-01-001-016";
        String locationName = "Las Palmas de Gran Canaria";

        int page = 1;

        List<IdealistaProperty> props =
                feeder.getProperties(locationId, locationName, page);

        for (IdealistaProperty p : props) {
            p.capturedAt = timestamp;
        }

        System.out.println(
                "Se obtuvieron " + props.size() +
                        " propiedades a las " + timestamp
        );
    }
}