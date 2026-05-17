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

        app.get("/api/properties", ctx -> {
            Integer minPrice = ctx.queryParamAsClass("minPrice", Integer.class).getOrDefault(null);
            Integer maxPrice = ctx.queryParamAsClass("maxPrice", Integer.class).getOrDefault(null);
            Integer minSize = ctx.queryParamAsClass("minSize", Integer.class).getOrDefault(null);
            Integer maxSize = ctx.queryParamAsClass("maxSize", Integer.class).getOrDefault(null);
            Integer rooms = ctx.queryParamAsClass("rooms", Integer.class).getOrDefault(null);
            String type = ctx.queryParam("type");
            String zone = ctx.queryParam("zone");
            Boolean undervalued = ctx.queryParamAsClass("undervalued", Boolean.class).getOrDefault(false);

            var result = datamart.getAllProperties().stream()
                    .filter(p -> minPrice == null || p.getPayload().getPrice() >= minPrice)
                    .filter(p -> maxPrice == null || p.getPayload().getPrice() <= maxPrice)
                    .filter(p -> minSize == null || p.getPayload().getSize() >= minSize)
                    .filter(p -> maxSize == null || p.getPayload().getSize() <= maxSize)
                    .filter(p -> rooms == null || p.getPayload().getRooms() == rooms)
                    .filter(p -> type == null || type.isEmpty() ||
                            p.getPayload().getPropertyType().equalsIgnoreCase(type))
                    .filter(p -> zone == null || zone.isEmpty() ||
                            p.getPayload().getNeighborhood().equalsIgnoreCase(zone) ||
                            p.getPayload().getDistrict().equalsIgnoreCase(zone) ||
                            p.getPayload().getMunicipality().equalsIgnoreCase(zone))
                    .filter(p -> {
                        if (!undervalued) return true;

                        var eval = logic.evaluateProperty(p);
                        double estimated = eval.expectedPrice();
                        double real = p.getPayload().getPrice();

                        return estimated > real;
                    })
                    .toList();

            ctx.json(result);
        });

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
