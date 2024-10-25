package sk.trizna.jms.consumer;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import sk.trizna.jms.consumer.listener.CustomListener;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public MessageListenerContainer messageListener(ConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName("TEST.QUEUE"); // Your queue name
        container.setMessageListener(messageListenerAdapter());
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new CustomListener());
    }
    
    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616"); // Adjust as necessary
    }
}