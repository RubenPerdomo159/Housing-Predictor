package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.ApiController;
import org.ulpgc.dacd.messaging.EventConsumer;
import org.ulpgc.dacd.model.Datamart;
import org.ulpgc.dacd.model.HistoricalEventLoader;
import org.ulpgc.dacd.model.InMemoryDatamart;

public class BusinessUnitApp {

    public static void main(String[] args) {

        String brokerUrl = "tcp://localhost:61616";

        Datamart datamart = new InMemoryDatamart();

        // 1. Cargar históricos
        HistoricalEventLoader loader = new HistoricalEventLoader();
        loader.loadHistoricalEvents("data", datamart);

        // 2. Consumidores en tiempo real
        EventConsumer idealistaConsumer =
                new EventConsumer(brokerUrl, "Idealista", datamart, "business-unit-idealista");

        EventConsumer fotocasaConsumer =
                new EventConsumer(brokerUrl, "Fotocasa", datamart, "business-unit-fotocasa");


        idealistaConsumer.start();
        fotocasaConsumer.start();

        System.out.println("Business Unit iniciada con históricos + tiempo real.");

        ApiController api = new ApiController();
        api.start(datamart);

        WebServer.start();
    }
}
