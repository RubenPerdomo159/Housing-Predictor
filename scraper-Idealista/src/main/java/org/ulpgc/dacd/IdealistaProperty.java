package org.ulpgc.dacd;

public class IdealistaProperty {

    public String precio;
    public String metros;
    public String habitaciones;
    public String planta;
    public String ubicacion;
    public String url;

    @Override
    public String toString() {
        return "Precio: " + precio +
                " | Metros: " + metros +
                " | Habitaciones: " + habitaciones +
                " | Planta: " + planta +
                " | Ubicación: " + ubicacion +
                " | URL: " + url;
    }
}
