package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.FotocasaController;
import org.ulpgc.dacd.controller.feeder.FotocasaScraperService;
import org.ulpgc.dacd.controller.persistence.SQLiteFotocasaPropertyStore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbPath = args.length > 0 ? args[0] : "fotocasa.db";

        FotocasaController controller = new FotocasaController(
                new FotocasaScraperService(),
                new SQLiteFotocasaPropertyStore(dbPath)
        );

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            try {
                controller.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.MINUTES);

        System.out.println("Scraper Fotocasa ejecutándose periódicamente...");
    }
}
