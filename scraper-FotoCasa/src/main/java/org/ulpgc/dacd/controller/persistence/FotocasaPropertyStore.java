package org.ulpgc.dacd.controller.persistence;

import org.ulpgc.dacd.model.FotocasaProperty;
import java.util.List;

public interface FotocasaPropertyStore {
    void store(List<FotocasaProperty> properties) throws Exception;
    int getLastPage() throws Exception;
    void saveLastPage(int page) throws Exception;
}
