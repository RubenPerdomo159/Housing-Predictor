package org.ulpgc.dacd.model;

import com.google.gson.Gson;
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

        for (File sourceFolder : root.listFiles()) { // Idealista, Fotocasa
            if (!sourceFolder.isDirectory()) continue;

            for (File dateFolder : sourceFolder.listFiles()) { // 20260509
                if (!dateFolder.isDirectory()) continue;

                for (File file : dateFolder.listFiles()) { // 20260509.events
                    if (file.getName().endsWith(".events")) {
                        loadFile(file, datamart);
                    }
                }
            }
        }
    }

    private void loadFile(File file, Datamart datamart) {
        System.out.println("Cargando histórico: " + file.getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                Event event = gson.fromJson(line, Event.class);
                datamart.registerEvent(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
