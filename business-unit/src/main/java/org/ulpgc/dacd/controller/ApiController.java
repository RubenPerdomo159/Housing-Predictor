package org.ulpgc.dacd.controller;

import io.javalin.Javalin;
import org.ulpgc.dacd.logic.BusinessLogic;
import org.ulpgc.dacd.model.Datamart;
import org.ulpgc.dacd.model.Event;
import org.ulpgc.dacd.model.InMemoryDatamart;

import java.util.List;

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

        app.get("/property/{propertyCode}", ctx -> {
            String code = ctx.pathParam("propertyCode");
            Event event = datamart.getEventByPropertyCode(code);

            if (event == null) {
                ctx.status(404).json(java.util.Map.of("error", "No existe esa propiedad"));
                return;
            }

            ctx.json(event.getPayload()); // aquí va toda la ficha rica
        });

        app.get("/property/{propertyCode}/full", ctx -> {
            String code = ctx.pathParam("propertyCode");
            Event event = datamart.getEventByPropertyCode(code);

            if (event == null) {
                ctx.status(404).json(java.util.Map.of("error", "No existe esa propiedad"));
                return;
            }

            var valuation = logic.evaluateProperty(event);
            var explanation = logic.explainValuation(event, valuation);

            ctx.json(java.util.Map.of(
                    "details", event.getPayload(),
                    "valuation", valuation,
                    "explanation", explanation
            ));
        });

        app.get("/property/{propertyCode}/comparables", ctx -> {
            String code = ctx.pathParam("propertyCode");
            Event event = datamart.getEventByPropertyCode(code);

            if (event == null) {
                ctx.status(404).json(java.util.Map.of("error", "No existe esa propiedad"));
                return;
            }

            List<Event> comps = logic.getComparables(event);

            ctx.json(comps.stream()
                    .map(Event::getPayload)
                    .toList());
        });







        System.out.println("API REST iniciada en puerto 7000");
    }
}
