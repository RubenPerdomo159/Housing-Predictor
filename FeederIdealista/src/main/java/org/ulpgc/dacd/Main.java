package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.IdealistaController;
import org.ulpgc.dacd.controller.feeder.IdealistaApiClient;
import org.ulpgc.dacd.controller.feeder.IdealistaFeeder;
import org.ulpgc.dacd.controller.persistence.SQLiteIdealistaPropertyStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbPath = args.length > 0 ? args[0] : "idealista.db";

        IdealistaController controller = new IdealistaController(
                new IdealistaFeeder(new IdealistaApiClient()),
                new SQLiteIdealistaPropertyStore(dbPath)
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

        System.out.println("FeederIdealista ejecutándose periódicamente...");
    }
}
