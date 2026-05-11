package org.ulpgc.dacd.messaging;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.Event;
import org.ulpgc.dacd.model.Datamart;

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
                        Event event = gson.fromJson(json, Event.class);

                        datamart.registerEvent(event);

                        System.out.println("Evento recibido y registrado: " + event.getPayload().getPropertyCode());
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
