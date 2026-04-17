package org.ulpgc.dacd.model;

public class IdealistaProperty {
    public double precio;
    public double precioM2;
    public double precioAnterior;
    public double bajadaPrecio;
    public double bajadaPorcentaje;
    public String address;
    public String neighborhood;
    public String district;
    public String municipality;
    public String province;
    public String locationId;
    public double latitude;
    public double longitude;
    public double metros;
    public int habitaciones;
    public int bathrooms;
    public String floor;
    public boolean exterior;
    public String propertyType;
    public String status;
    public boolean hasLift;
    public boolean hasSwimmingPool;
    public boolean hasTerrace;
    public boolean hasAirConditioning;
    public boolean hasGarden;
    public boolean hasBoxRoom;
    public boolean hasParkingSpace;
    public boolean newDevelopment;
    public String url;
    public String propertyCode;
    public String capturedAt;

    @Override
    public String toString() {
        return "\n--- PROPIEDAD ---" +
                "\nPrecio total: " + precio + "€" +
                "\nPrecio/m²: " + precioM2 + "€" +
                "\nPrecio anterior: " + precioAnterior + "€" +
                "\nBajada: " + bajadaPrecio + "€ (" + bajadaPorcentaje + "%)" +
                "\nDirección: " + address +
                "\nBarrio: " + neighborhood +
                "\nDistrito: " + district +
                "\nMunicipio: " + municipality +
                "\nProvincia: " + province +
                "\nLocationId: " + locationId +
                "\nCoordenadas: " + latitude + ", " + longitude +
                "\nMetros: " + metros + "m²" +
                "\nHabitaciones: " + habitaciones +
                "\nBaños: " + bathrooms +
                "\nPlanta: " + floor +
                "\nExterior: " + exterior +
                "\nTipo: " + propertyType +
                "\nEstado: " + status +
                "\nAscensor: " + hasLift +
                "\nPiscina: " + hasSwimmingPool +
                "\nTerraza: " + hasTerrace +
                "\nAire acondicionado: " + hasAirConditioning +
                "\nJardín: " + hasGarden +
                "\nTrastero: " + hasBoxRoom +
                "\nGaraje: " + hasParkingSpace +
                "\nObra nueva: " + newDevelopment +
                "\nURL: " + url;
    }
}
