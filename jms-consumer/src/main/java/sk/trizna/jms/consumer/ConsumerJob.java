package sk.trizna.jms.consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsumerJob {
	
//	@Scheduled(fixedRate = 1000)
	/**
	 * read & process from queue
	 */
	public static void doSomething() {
		try {
            // Create a ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();
            
            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Create a Destination (Queue or Topic)
            Destination destination = session.createQueue("TEST.QUEUE");
            
            // Create a MessageConsumer
            MessageConsumer consumer = session.createConsumer(destination);
            
            // Receive a message
            Message message = consumer.receive(1000); // Wait for 1 second
            
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message: " + textMessage.getText());
            }
            
            // Clean up
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
