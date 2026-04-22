package org.ulpgc.dacd.eventstorebuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.function.BiConsumer;

public class EventConsumer {

    private final String url;

    public EventConsumer(String url) {
        this.url = url;
    }

    public void start(BiConsumer<String, JsonObject> eventConsumer) throws Exception {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(url);
        Connection connection = factory.createConnection();
        connection.setClientID("EventStoreBuilder"); // Suscripción durable
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Suscribirse a los dos topics
        Topic idealistaTopic = session.createTopic("Idealista");
        Topic fotocasaTopic = session.createTopic("Fotocasa");

        MessageConsumer idealistaConsumer =
                session.createDurableSubscriber(idealistaTopic, "IdealistaSub");

        MessageConsumer fotocasaConsumer =
                session.createDurableSubscriber(fotocasaTopic, "FotocasaSub");

        idealistaConsumer.setMessageListener(m->onMessage(m, eventConsumer));
        fotocasaConsumer.setMessageListener(m->onMessage(m, eventConsumer));

        System.out.println("EventStoreBuilder escuchando topics...");
    }

    public void onMessage(Message message, BiConsumer<String, JsonObject> eventConsumer) {
        try {
            if (message instanceof TextMessage textMessage) {
                String topic = message.getJMSDestination().toString().replace("topic://", "");
                String json = textMessage.getText();
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                eventConsumer.accept(topic, obj);
                System.out.println("Evento recibido y guardado: " + json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
