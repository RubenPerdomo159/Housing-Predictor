package org.ulpgc.dacd.eventstorebuilder;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventWriter {
    private final File directory;

    public EventWriter(File directory) {
        this.directory = directory;
    }

    public void writeEvent(String topic, JsonObject event) {

        // Carpeta raíz del proyecto
        String basePath = directory.getAbsolutePath();

        // Fecha YYYYMMDD
        LocalDate date = LocalDate.parse(ts.substring(0, 10));
        String day = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Ruta completa
        String folderPath = basePath + "/eventstore/" + topic + "/" + ss;
        File folder = new File(folderPath);
        folder.mkdirs();

        File file = new File(folderPath + "/" + day + ".events");

        System.out.println("Guardando en: " + file.getAbsolutePath());

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(json + "\n");
        }
    }
}
