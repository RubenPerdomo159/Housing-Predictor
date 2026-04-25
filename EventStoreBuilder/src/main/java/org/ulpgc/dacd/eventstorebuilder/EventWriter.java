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
        String basePath = directory.getAbsolutePath();
        String ts = event.get("ts").getAsString();

        LocalDate date = LocalDate.parse(ts.substring(0, 10));
        String day = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String folderPath = basePath + "/eventstore/" + topic + "/" + day;
        File folder = new File(folderPath);
        folder.mkdirs();

        File file = new File(folderPath + "/" + day + ".events");
        System.out.println("Guardando en: " + file.getAbsolutePath());

        String json = event.toString();

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(json + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
