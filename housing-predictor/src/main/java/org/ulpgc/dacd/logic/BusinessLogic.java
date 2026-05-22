package org.ulpgc.dacd.logic;

import org.ulpgc.dacd.controller.Datamart;
import org.ulpgc.dacd.model.Event;
import org.ulpgc.dacd.model.Payload;

import java.util.List;

public class BusinessLogic {

    private final Datamart datamart;

    public BusinessLogic(Datamart datamart) {
        this.datamart = datamart;
    }

    public double calculateExpectedPrice(Event event) {
        String neighborhood = event.getPayload().getNeighborhood();
        double avgPriceM2 = datamart.getAveragePricePerSquareMeter(neighborhood);

        return avgPriceM2 * event.getPayload().getSize();
    }

    public EvaluationResult evaluateProperty(Event event) {
        double real = event.getPayload().getPrice();

        double expected = calculateComparableExpectedPrice(event);
        double diff = real - expected;

        String status;
        if (diff < -0.07 * expected) status = "infravalorada";
        else if (diff > 0.07 * expected) status = "sobrevalorada";
        else status = "precio justo";

        return new EvaluationResult(
                event.getPayload().getPropertyCode(),
                real,
                expected,
                diff,
                status
        );
    }

    public double calculateAdjustedExpectedPrice(Event event) {
        Payload p = event.getPayload();

        double base = datamart.getAveragePricePerSquareMeter(p.getNeighborhood()) * p.getSize();
        double factor = 1.0;

        if (p.isHasLift()) factor += 0.03;
        if (p.isHasTerrace()) factor += 0.05;
        if (p.isHasSwimmingPool()) factor += 0.07;
        if (p.isHasParkingSpace()) factor += 0.04;
        if (p.isExterior()) factor += 0.02;
        if (p.isNewDevelopment()) factor += 0.10;

        if (!p.isHasLift() && p.getFloor() != null && Integer.parseInt(p.getFloor()) > 3)
            factor -= 0.08;

        if (!p.isExterior()) factor -= 0.04;

        if (p.getSize() < 40) factor += 0.08;
        if (p.getSize() > 120) factor -= 0.05;

        return base * factor;
    }

    public String explainValuation(Event event, EvaluationResult result) {
        Payload p = event.getPayload();
        StringBuilder sb = new StringBuilder();

        double diff = result.difference();
        double expected = result.expectedPrice();

        List<Event> comps = getComparables(event);
        sb.append("Se han encontrado ").append(comps.size())
                .append(" viviendas comparables en el barrio. ");

        if (diff < -0.07 * expected) {
            sb.append("La vivienda está infravalorada porque ");
        } else if (diff > 0.07 * expected) {
            sb.append("La vivienda está sobrevalorada porque ");
        } else {
            sb.append("La vivienda tiene un precio justo porque ");
        }

        if (!p.isHasLift() && p.getFloor() != null && Integer.parseInt(p.getFloor()) > 3)
            sb.append("no tiene ascensor y está en una planta alta, ");

        if (!p.isExterior())
            sb.append("es interior, ");

        if (!p.isHasTerrace())
            sb.append("no tiene terraza, ");

        if (!p.isHasParkingSpace())
            sb.append("no tiene garaje, ");

        if (p.isNewDevelopment())
            sb.append("es obra nueva, ");

        double avg = datamart.getAveragePricePerSquareMeter(p.getNeighborhood());
        double priceM2 = p.getPrice() / p.getSize();

        if (priceM2 > avg)
            sb.append("el precio por m² está por encima de la media del barrio, ");
        else
            sb.append("el precio por m² está por debajo de la media del barrio, ");

        String explanation = sb.toString().trim();
        if (explanation.endsWith(",")) {
            explanation = explanation.substring(0, explanation.length() - 1) + ".";
        }

        return explanation;
    }

    public List<Event> getComparables(Event target) {
        Payload p = target.getPayload();

        List<Event> all = datamart.getPropertiesInNeighborhood(p.getNeighborhood());

        return all.stream()
                .filter(e -> !e.getPayload().getPropertyCode().equals(p.getPropertyCode()))
                .filter(e -> {
                    double size = e.getPayload().getSize();
                    return size >= p.getSize() * 0.8 && size <= p.getSize() * 1.2;
                })
                .filter(e -> e.getPayload().getRooms() == p.getRooms())
                .filter(e -> e.getPayload().getPropertyType().equals(p.getPropertyType()))
                .filter(e -> e.getPayload().isExterior() == p.isExterior())
                .filter(e -> e.getPayload().isHasLift() == p.isHasLift())
                .toList();
    }

    public double calculateComparableExpectedPrice(Event event) {
        List<Event> comps = getComparables(event);

        if (comps.isEmpty()) {
            return calculateAdjustedExpectedPrice(event);
        }

        double sum = 0;
        for (Event e : comps) {
            sum += e.getPayload().getPrice() / e.getPayload().getSize();
        }

        double avgM2 = sum / comps.size();
        return avgM2 * event.getPayload().getSize();
    }
}
