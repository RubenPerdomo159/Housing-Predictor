package org.ulpgc.dacd.controller.feeder;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class FotocasaPublisher {

    private final MessageProducer producer;
    private final Session session;

    public FotocasaPublisher() throws Exception {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");

        Connection connection = factory.createConnection();
        connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Cambia únicamente el nombre del topic
        Topic topic = session.createTopic("Fotocasa");
        this.producer = session.createProducer(topic);
    }

    public void publish(String json) throws Exception {
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
        System.out.println("Evento publicado en Fotocasa: " + json);
    }
}

