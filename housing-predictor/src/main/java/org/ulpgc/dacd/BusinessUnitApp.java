package org.ulpgc.dacd;

import org.ulpgc.dacd.view.ApiController;
import org.ulpgc.dacd.messaging.EventConsumer;
import org.ulpgc.dacd.controller.Datamart;
import org.ulpgc.dacd.controller.HistoricalEventLoader;
import org.ulpgc.dacd.controller.InMemoryDatamart;

public class BusinessUnitApp {

    public static void main(String[] args) {

        String brokerUrl = "tcp://localhost:61616";

        Datamart datamart = new InMemoryDatamart();

        HistoricalEventLoader loader = new HistoricalEventLoader();
        loader.loadHistoricalEvents("data", datamart);

        EventConsumer idealistaConsumer =
                new EventConsumer(brokerUrl, "Idealista", datamart, "business-unit-idealista");

        EventConsumer fotocasaConsumer =
                new EventConsumer(brokerUrl, "Fotocasa", datamart, "business-unit-fotocasa");

        idealistaConsumer.start();
        fotocasaConsumer.start();

        System.out.println("Business Unit iniciada con históricos + tiempo real.");

        ApiController api = new ApiController();
        api.start(datamart);
    }
}
