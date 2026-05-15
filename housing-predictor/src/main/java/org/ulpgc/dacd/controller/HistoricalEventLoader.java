package org.ulpgc.dacd.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.messaging.EventConsumer;
import org.ulpgc.dacd.model.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HistoricalEventLoader {

    private final Gson gson = new Gson();

    public void loadHistoricalEvents(String basePath, Datamart datamart) {
        File root = new File(basePath + "/eventstore");

        if (!root.exists()) {
            System.out.println("No existe la carpeta eventstore.");
            return;
        }

        for (File sourceFolder : root.listFiles()) {
            if (!sourceFolder.isDirectory()) continue;

            for (File subFolder : sourceFolder.listFiles()) {
                if (!subFolder.isDirectory()) continue;

                for (File dateFolder : subFolder.listFiles()) {
                    if (!dateFolder.isDirectory()) continue;

                    for (File file : dateFolder.listFiles()) {
                        if (file.getName().endsWith(".events")) {
                            loadFile(file, datamart);
                        }
                    }
                }
            }
        }
    }

    private void loadFile(File file, Datamart datamart) {
        System.out.println("Cargando histórico: " + file.getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int loaded = 0;
            int skipped = 0;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                Event event = parseLine(line);
                if (event != null && event.getPayload() != null
                        && event.getPayload().getPropertyCode() != null) {
                    datamart.registerEvent(event);
                    loaded++;
                } else {
                    skipped++;
                }
            }

            System.out.println("  → " + loaded + " eventos cargados, " + skipped + " descartados.");

        } catch (Exception e) {
            System.err.println("Error leyendo " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    private Event parseLine(String line) {
        try {
            JsonObject root = JsonParser.parseString(line).getAsJsonObject();
            String ss = root.has("ss") ? root.get("ss").getAsString() : "";

            if (ss.toLowerCase().contains("fotocasa")) {
                // El payload histórico tiene campos en español (FotocasaProperty)
                return EventConsumer.fotocasaToEvent(root);
            } else {
                // Idealista: el payload ya tiene los campos en inglés (Payload)
                return gson.fromJson(line, Event.class);
            }

        } catch (Exception e) {
            System.err.println("Línea inválida en histórico: " + e.getMessage());
            return null;
        }
    }
}
