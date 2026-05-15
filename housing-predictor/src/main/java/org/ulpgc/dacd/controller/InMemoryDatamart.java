package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.model.Event;

import java.util.*;

public class InMemoryDatamart implements Datamart {

    private final Map<String, List<Event>> propertiesByNeighborhood = new HashMap<>();
    private final Map<String, Event> eventsByPropertyCode = new HashMap<>();


    @Override
    public void registerEvent(Event event) {
        if (event == null || event.getPayload() == null) return;

        String code = event.getPayload().getPropertyCode();
        if (code == null || code.isEmpty()) {
            System.err.println("[Datamart] Evento descartado: propertyCode nulo. ss=" + event.getSs());
            return;
        }

        String neighborhood = event.getPayload().getNeighborhood();
        if (neighborhood == null || neighborhood.isEmpty()) neighborhood = "UNKNOWN";

        propertiesByNeighborhood
                .computeIfAbsent(neighborhood, k -> new ArrayList<>())
                .add(event);

        eventsByPropertyCode.put(code, event);

        System.out.println("[Datamart] Registrado: " + code
                + " | barrio=" + neighborhood
                + " | precio=" + event.getPayload().getPrice() + "€");
    }


    @Override
    public double getAveragePricePerSquareMeter(String neighborhood) {
        List<Event> list = propertiesByNeighborhood.getOrDefault(neighborhood, List.of());
        if (list.isEmpty()) return 0;

        return list.stream()
                .mapToDouble(e -> e.getPayload().getPrice() / e.getPayload().getSize())
                .average()
                .orElse(0);
    }

    @Override
    public Event getEventByPropertyCode(String code) {
        return eventsByPropertyCode.get(code);
    }

    public Set<String> getAllNeighborhoods() {
        return propertiesByNeighborhood.keySet();
    }


    @Override
    public List<Event> getAllProperties() {
        return propertiesByNeighborhood.values()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public List<Event> getPropertiesInNeighborhood(String neighborhood) {
        return propertiesByNeighborhood.getOrDefault(neighborhood, List.of());
    }

}
