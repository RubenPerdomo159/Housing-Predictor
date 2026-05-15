package org.ulpgc.dacd.view;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.ulpgc.dacd.controller.Datamart;
import org.ulpgc.dacd.logic.BusinessLogic;
import org.ulpgc.dacd.model.Event;

import java.util.List;

public class ApiController {

    private Javalin app;

    public void start(Datamart datamart) {

        BusinessLogic logic = new BusinessLogic(datamart);

        app = Javalin.create(config -> {

            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "view";
                staticFiles.location = Location.CLASSPATH;
            });

        }).start(7000);

        app.get("/", ctx -> ctx.redirect("/index.html"));

        // --- API REST ---
        app.get("/api/ping", ctx -> ctx.result("API funcionando"));

        app.get("/api/stats/{neighborhood}", ctx -> {
            String neighborhood = ctx.pathParam("neighborhood");
            double avg = datamart.getAveragePricePerSquareMeter(neighborhood);
            ctx.json(avg);
        });

        app.get("/api/valuation/{propertyCode}", ctx -> {
            String code = ctx.pathParam("propertyCode");
            Event event = datamart.getEventByPropertyCode(code);

            if (event == null) {
                ctx.status(404).result("No existe esa propiedad");
                return;
            }

            ctx.json(logic.evaluateProperty(event));
        });

        app.get("/api/neighborhoods", ctx -> ctx.json(datamart.getAllNeighborhoods()));

        app.get("/api/properties", ctx -> ctx.json(datamart.getAllProperties()));

        app.get("/api/properties/{neighborhood}", ctx -> {
            String neighborhood = ctx.pathParam("neighborhood");
            ctx.json(datamart.getPropertiesInNeighborhood(neighborhood));
        });

        app.get("/api/property/{propertyCode}", ctx -> {
            String code = ctx.pathParam("propertyCode");
            Event event = datamart.getEventByPropertyCode(code);

            if (event == null) {
                ctx.status(404).json(java.util.Map.of("error", "No existe esa propiedad"));
                return;
            }

            ctx.json(event.getPayload());
        });

        app.get("/api/property/{propertyCode}/full", ctx -> {
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

        app.get("/api/property/{propertyCode}/comparables", ctx -> {
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

        System.out.println("Servidor web + API iniciado en http://localhost:7000");
    }
}
