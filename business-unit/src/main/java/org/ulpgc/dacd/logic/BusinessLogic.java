package org.ulpgc.dacd.logic;

import org.ulpgc.dacd.model.Datamart;
import org.ulpgc.dacd.model.Event;

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
        double realPrice = event.getPayload().getPrice();
        double expected = calculateExpectedPrice(event);
        double diff = realPrice - expected;

        String status;
        if (diff < -5000) status = "infravalorada";
        else if (diff > 5000) status = "sobrevalorada";
        else status = "precio justo";

        return new EvaluationResult(
                event.getPayload().getPropertyCode(),
                realPrice,
                expected,
                diff,
                status
        );
    }
}
