import org.ulpgc.dacd.IdealistaApiClient;
import org.ulpgc.dacd.IdealistaPropertyService;

public class Main {
    public static void main(String[] args) throws Exception {

        IdealistaApiClient api = new IdealistaApiClient();
        IdealistaPropertyService prop = new IdealistaPropertyService(api);

        // Hardcodeado mientras probamos
        String locationId = "0-EU-ES-35-01-001-016"; // Las Palmas de Gran Canaria

        System.out.println("Location ID: " + locationId);

        var properties = prop.getProperties(locationId, 1);
        properties.forEach(System.out::println);
    }
}