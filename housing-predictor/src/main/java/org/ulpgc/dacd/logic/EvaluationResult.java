package org.ulpgc.dacd.logic;

public record EvaluationResult(
        String propertyCode,
        double realPrice,
        double expectedPrice,
        double difference,
        String status
) {}
