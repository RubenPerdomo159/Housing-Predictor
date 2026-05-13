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
                        String ss = root.get("ss").getAsString();

                        Event event;

                        // Caso Fotocasa
                        if ("fotocasa".equalsIgnoreCase(ss)) {

                            JsonObject payloadNode = root.getAsJsonObject("payload");
                            FotocasaProperty f = gson.fromJson(payloadNode, FotocasaProperty.class);

                            Payload p = new Payload();

                            String code = Integer.toHexString(f.url.hashCode());
                            p.setPropertyCode("FC-" + code);

                            p.setPrice(f.precio);
                            p.setSize(f.metros);
                            p.setRooms(f.habitaciones);
                            p.setAddress(f.ubicacion);
                            p.setUrl(f.url);
                            p.setCapturedAt(f.capturedAt);

                            p.setNeighborhood("Desconocido");
                            p.setDistrict("Desconocido");
                            p.setMunicipality("Las Palmas de Gran Canaria");
                            p.setProvince("Las Palmas");

                            p.setBathrooms(0);
                            p.setFloor("0");
                            p.setExterior(true);
                            p.setPropertyType("flat");
                            p.setStatus("unknown");

                            p.setHasLift(false);
                            p.setHasSwimmingPool(false);
                            p.setHasTerrace(false);
                            p.setHasAirConditioning(false);
                            p.setHasGarden(false);
                            p.setHasBoxRoom(false);
                            p.setHasParkingSpace(false);
                            p.setNewDevelopment(false);

                            event = new Event();
                            event.setTs(root.get("ts").getAsString());
                            event.setSs("fotocasa");
                            event.setPayload(p);

                        } else {
                            // Idealista
                            event = gson.fromJson(json, Event.class);
                        }

                        datamart.registerEvent(event);

                        System.out.println("Evento recibido y registrado: " +
                                event.getPayload().getPropertyCode());
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
}
