package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.model.Event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryDatamart implements Datamart {

    private final Map<String, List<Event>> propertiesByNeighborhood = new HashMap<>();
    private final Map<String, Event> eventsByPropertyCode = new HashMap<>();

    @Override
    public void registerEvent(Event event) {
        if (event == null || event.getPayload() == null) return;

        String code = event.getPayload().getPropertyCode();
        if (code == null || code.isEmpty()) return;

        event.setLastSeen(LocalDateTime.now());
        eventsByPropertyCode.put(code, event);

        String neighborhood = event.getPayload().getNeighborhood();
        if (neighborhood == null || neighborhood.isEmpty()) neighborhood = "UNKNOWN";
        propertiesByNeighborhood
                .computeIfAbsent(neighborhood, k -> new ArrayList<>())
                .removeIf(e -> e.getPayload().getPropertyCode().equals(code));

        propertiesByNeighborhood.get(neighborhood).add(event);
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

    @Override
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

    public void cleanupOldProperties(Duration maxAge) {
        LocalDateTime limit = LocalDateTime.now().minus(maxAge);

        eventsByPropertyCode.entrySet().removeIf(e ->
                e.getValue().getLastSeen().isBefore(limit)
        );
        propertiesByNeighborhood.values().forEach(list ->
                list.removeIf(ev -> ev.getLastSeen().isBefore(limit))
        );
    }
}
