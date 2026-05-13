package org.ulpgc.dacd.controller;

import org.ulpgc.dacd.model.Event;

import java.util.List;

public interface Datamart {

    void registerEvent(Event event);

    double getAveragePricePerSquareMeter(String neighborhood);

    List<Event> getPropertiesInNeighborhood(String neighborhood);

    Event getEventByPropertyCode(String code);

    Object getAllNeighborhoods();

    List<Event> getAllProperties();
}
