package org.ulpgc.dacd.controller.feeder;

import org.ulpgc.dacd.model.IdealistaProperty;
import java.util.List;

public interface PropertyFeeder {
    List<IdealistaProperty> getProperties(String locationId, String locationName, int page) throws Exception;
}
