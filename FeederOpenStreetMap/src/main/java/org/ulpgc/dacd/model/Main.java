package org.ulpgc.dacd.model;

import org.ulpgc.dacd.controller.OpenStreetMapController;
import org.ulpgc.dacd.controller.feeder.OpenStreetMapFeeder;
import org.ulpgc.dacd.controller.feeder.OpenStreetMapPublisher;
import org.ulpgc.dacd.model.OverpassService;
import org.ulpgc.dacd.controller.feeder.OverpassApiClient;
import org.ulpgc.dacd.model.Place.*;

public class Main {
    public static void main(String[] args) throws Exception {

        OverpassApiClient api = new OverpassApiClient();
        Mapper mapper = new Mapper();
        OverpassService service = new OverpassService(api, mapper);

        OpenStreetMapPublisher publisher = new OpenStreetMapPublisher();
        OpenStreetMapFeeder feeder = new OpenStreetMapFeeder(service, publisher);

        OpenStreetMapController controller = new OpenStreetMapController(feeder);

        controller.execute();
    }
}