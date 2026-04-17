package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.IdealistaController;
import org.ulpgc.dacd.controller.feeder.IdealistaApiClient;
import org.ulpgc.dacd.controller.feeder.IdealistaFeeder;
import org.ulpgc.dacd.controller.persistence.SQLiteIdealistaPropertyStore;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbPath = args.length > 0 ? args[0] : "idealista.db";

        IdealistaController controller = new IdealistaController(
                new IdealistaFeeder(new IdealistaApiClient()),
                new SQLiteIdealistaPropertyStore(dbPath)
        );

        controller.execute();
    }
}
