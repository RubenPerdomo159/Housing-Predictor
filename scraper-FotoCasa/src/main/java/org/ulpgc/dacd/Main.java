package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.FotocasaController;
import org.ulpgc.dacd.controller.feeder.FotocasaScraperService;
import org.ulpgc.dacd.controller.persistence.SQLiteFotocasaPropertyStore;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbPath = args.length > 0 ? args[0] : "fotocasa.db";

        FotocasaController controller = new FotocasaController(
                new FotocasaScraperService(),
                new SQLiteFotocasaPropertyStore(dbPath)
        );

        controller.execute();
    }
}
