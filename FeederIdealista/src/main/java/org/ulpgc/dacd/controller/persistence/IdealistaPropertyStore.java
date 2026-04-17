package org.ulpgc.dacd.controller.persistence;

import org.ulpgc.dacd.model.IdealistaProperty;
import java.util.List;

public interface IdealistaPropertyStore {
    void store(List<IdealistaProperty> properties) throws Exception;
    int getLastPage() throws Exception;
    void saveLastPage(int page) throws Exception;
}
