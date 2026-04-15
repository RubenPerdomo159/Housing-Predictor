package org.ulpgc.dacd;

import org.ulpgc.dacd.controller.IdealistaController;
import org.ulpgc.dacd.controller.feeder.IdealistaApiClient;
import org.ulpgc.dacd.controller.feeder.IdealistaFeeder;
import org.ulpgc.dacd.controller.persistence.SQLiteIdealistaPropertyStore;

public class Main {
    public static void main(String[] args) throws Exception {

        IdealistaController controller = new IdealistaController(
                new IdealistaFeeder(new IdealistaApiClient()),
                new SQLiteIdealistaPropertyStore("idealista.db")
        );

        controller.execute();
    }
}
