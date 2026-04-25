package org.ulpgc.dacd.eventstorebuilder;

import com.google.gson.JsonObject;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        File baseDirectory = new File("data");
        EventWriter writer = new EventWriter(baseDirectory);

        String brokerUrl = "tcp://localhost:61616";

        EventConsumer consumer = new EventConsumer(brokerUrl);
        consumer.start((topic, jsonEvent) -> {
            writer.writeEvent(topic, jsonEvent);
        });
        System.out.println("EventStoreBuilder iniciado correctamente.");
    }
}