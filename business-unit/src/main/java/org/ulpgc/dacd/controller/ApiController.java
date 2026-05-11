package org.ulpgc.dacd.controller;

import io.javalin.Javalin;
import org.ulpgc.dacd.logic.BusinessLogic;
import org.ulpgc.dacd.model.Datamart;
import org.ulpgc.dacd.model.Event;
import org.ulpgc.dacd.model.InMemoryDatamart;

public class ApiController {

    private Javalin app;

    public void start(Datamart datamart) {

        BusinessLogic logic = new BusinessLogic(datamart);

        app = Javalin.create().start(7000);

        app.get("/ping", ctx -> ctx.result("API funcionando"));

        app.get("/stats/{neighborhood}", ctx -> {
            String neighborhood = ctx.pathParam("neighborhood");
            double avg = datamart.getAveragePricePerSquareMeter(neighborhood);
            ctx.json(avg);
        });

        app.get("/valuation/{propertyCode}", ctx -> {
            String code = ctx.pathParam("propertyCode");

            Event event = datamart.getEventByPropertyCode(code);

            if (event == null) {
                ctx.status(404).result("No existe esa propiedad");
                return;
            }

            ctx.json(logic.evaluateProperty(event));
        });

        app.get("/neighborhoods", ctx -> {
            ctx.json(datamart.getAllNeighborhoods());
        });

        app.get("/properties", ctx -> {
            ctx.json(datamart.getAllProperties());
        });

        app.get("/properties/{neighborhood}", ctx -> {
            String neighborhood = ctx.pathParam("neighborhood");
            ctx.json(datamart.getPropertiesInNeighborhood(neighborhood));
        });





        System.out.println("API REST iniciada en puerto 7000");
    }
}
