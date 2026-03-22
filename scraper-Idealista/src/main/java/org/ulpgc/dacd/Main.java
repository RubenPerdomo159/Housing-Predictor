package org.ulpgc.dacd;

public class Main {
    public static void main(String[] args) throws Exception {

        IdealistaApiClient api = new IdealistaApiClient();
        IdealistaLocationService loc = new IdealistaLocationService(api);
        IdealistaPropertyService prop = new IdealistaPropertyService(api);

        String locationId = loc.getLocationId("las palmas");
        System.out.println("Location ID: " + locationId);

        var properties = prop.getProperties(locationId, 1);

        properties.forEach(System.out::println);
    }
}


