package org.ulpgc.dacd.controller.feeder;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class IdealistaPublisher {

    private final MessageProducer producer;
    private final Session session;

    public IdealistaPublisher() throws Exception {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");

        Connection connection = factory.createConnection();
        connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic("Idealista");
        this.producer = session.createProducer(topic);
    }

    public void publish(String json) throws Exception {
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
        System.out.println("Evento publicado: " + json);
    }
}
