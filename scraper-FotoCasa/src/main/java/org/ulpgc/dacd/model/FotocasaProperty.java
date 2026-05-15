package org.ulpgc.dacd.model;

public class FotocasaProperty {
    public String propertyCode;
    public double precio;
    public double metros;
    public int habitaciones;
    public String ubicacion;
    public String url;
    public String capturedAt;
    public int bathrooms;
    public String floor;
    public boolean exterior;
    public String propertyType;
    public boolean hasLift;
    public boolean hasSwimmingPool;
    public boolean hasTerrace;
    public boolean hasAirConditioning;
    public boolean hasGarden;
    public boolean hasBoxRoom;
    public boolean hasParkingSpace;
    public boolean newDevelopment;

    @Override
    public String toString() {
        return String.format(
                "Precio: %.0f€ | Metros: %.0fm² | Hab: %d | Baños: %d | Planta: %s | Tipo: %s | URL: %s",
                precio, metros, habitaciones, bathrooms,
                floor != null ? floor : "N/A",
                propertyType != null ? propertyType : "N/A",
                url
        );
    }
}
