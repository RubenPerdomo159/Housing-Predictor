package org.ulpgc.dacd.messaging;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.controller.Datamart;
import org.ulpgc.dacd.model.Event;
import org.ulpgc.dacd.model.FotocasaProperty;
import org.ulpgc.dacd.model.Payload;

import javax.jms.*;

public class EventConsumer {

    private final String brokerUrl;
    private final String topicName;
    private final Datamart datamart;
    private final Gson gson = new Gson();
    private final String clientId;

    public EventConsumer(String brokerUrl, String topicName, Datamart datamart, String clientId) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.datamart = datamart;
        this.clientId = clientId;
    }

    public void start() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        String json = textMessage.getText();
                        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                        String ss = root.has("ss") ? root.get("ss").getAsString() : "";

                        Event event;

                        if (ss.toLowerCase().contains("fotocasa")) {
                            event = fotocasaToEvent(root);
                        } else {
                            event = gson.fromJson(json, Event.class);
                        }

                        if (event != null && event.getPayload() != null
                                && event.getPayload().getPropertyCode() != null) {
                            datamart.registerEvent(event);
                            System.out.println("Evento registrado [" + ss + "]: "
                                    + event.getPayload().getPropertyCode());
                        } else {
                            System.err.println("Evento descartado por falta de propertyCode. ss=" + ss);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println("EventConsumer escuchando en topic: " + topicName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Event fotocasaToEvent(JsonObject root) {
        try {
            Gson gson = new Gson();

            if (!root.has("payload") || root.get("payload").isJsonNull()) {
                System.err.println("[Fotocasa] Evento sin campo 'payload', descartado. JSON: " + root);
                return null;
            }

            JsonObject payloadNode = root.getAsJsonObject("payload");
            FotocasaProperty f = gson.fromJson(payloadNode, FotocasaProperty.class);

            if (f == null) {
                System.err.println("[Fotocasa] No se pudo deserializar el payload: " + payloadNode);
                return null;
            }

            System.out.println("[Fotocasa] Parseando propiedad: propertyCode=" + f.propertyCode
                    + " | url=" + f.url + " | precio=" + f.precio + " | metros=" + f.metros);

            Payload p = new Payload();

            // 1. propertyCode explícito en el JSON (scraper nuevo)
            if (f.propertyCode != null && !f.propertyCode.isEmpty()) {
                p.setPropertyCode(f.propertyCode);

                // 2. fallback: generar desde la URL (eventos históricos sin propertyCode)
            } else if (f.url != null && !f.url.isEmpty()) {
                p.setPropertyCode("FC-" + Integer.toHexString(Math.abs(f.url.hashCode())));

                // 3. último recurso: hash de precio + metros + capturedAt para no descartar el evento
            } else {
                String fallbackSeed = f.precio + "_" + f.metros + "_" + f.capturedAt;
                p.setPropertyCode("FC-" + Integer.toHexString(Math.abs(fallbackSeed.hashCode())));
                System.err.println("[Fotocasa] Advertencia: sin URL ni propertyCode, usando hash de campos: " + p.getPropertyCode());
            }

            p.setPrice(f.precio);
            p.setSize(f.metros);
            p.setRooms(f.habitaciones);
            p.setAddress(f.ubicacion != null ? f.ubicacion : "");
            p.setUrl(f.url != null ? f.url : "");
            p.setCapturedAt(f.capturedAt != null ? f.capturedAt : java.time.Instant.now().toString());

            if (f.metros > 0) {
                p.setPriceM2(f.precio / f.metros);
            }

            p.setNeighborhood("Las Palmas - General");
            p.setDistrict("Las Palmas");
            p.setMunicipality("Las Palmas de Gran Canaria");
            p.setProvince("Las Palmas");

            p.setBathrooms(f.bathrooms);
            p.setFloor(f.floor != null ? f.floor : "");
            p.setExterior(f.exterior);
            p.setPropertyType(f.propertyType != null ? f.propertyType : "piso");
            p.setStatus("active");

            p.setHasLift(f.hasLift);
            p.setHasSwimmingPool(f.hasSwimmingPool);
            p.setHasTerrace(f.hasTerrace);
            p.setHasAirConditioning(f.hasAirConditioning);
            p.setHasGarden(f.hasGarden);
            p.setHasBoxRoom(f.hasBoxRoom);
            p.setHasParkingSpace(f.hasParkingSpace);
            p.setNewDevelopment(f.newDevelopment);

            Event event = new Event();
            event.setTs(root.has("ts") && !root.get("ts").isJsonNull()
                    ? root.get("ts").getAsString()
                    : java.time.Instant.now().toString());
            event.setSs("FotocasaScraper");
            event.setPayload(p);

            System.out.println("[Fotocasa] Evento listo: propertyCode=" + p.getPropertyCode()
                    + " | precio=" + p.getPrice() + "€ | metros=" + p.getSize() + "m²");
            return event;

        } catch (Exception e) {
            System.err.println("[Fotocasa] Error convirtiendo evento: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
