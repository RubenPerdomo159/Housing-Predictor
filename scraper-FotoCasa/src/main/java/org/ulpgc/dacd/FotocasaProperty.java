package org.ulpgc.dacd;

public class FotocasaProperty {

    public double precio;
    public double metros;
    public int habitaciones;
    public String ubicacion;
    public String url;

    @Override
    public String toString() {
        return String.format(
                "Precio: %.0f€ | Metros: %.0fm² | Habitaciones: %d | Ubicación: %s | URL: %s",
                precio, metros, habitaciones, ubicacion, url
        );
    }
}