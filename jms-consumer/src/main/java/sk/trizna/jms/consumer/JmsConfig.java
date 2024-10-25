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
        container.setDestinationName("TEST.QUEUE_REQ");
//        container.setDestination(jndiDestination());
        container.setMessageListener(messageListenerAdapter());
        return container;
    }
    
//    /**
//     * Destination alternative using JNDI reference
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public Destination jndiDestination() {
//    	try {
//	        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
//	        factory.setJndiName("java:/comp/myQueue");
//	        factory.setProxyInterface(Destination.class);
//	        factory.setLookupOnStartup(true);
//	        factory.afterPropertiesSet();
//	        return (Destination) factory.getObject();
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    	return null;
//    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new CustomListener(connectionFactory()));
//        return new MessageListenerAdapter(new CustomListener(jndiConnectionFactory()));
    }
    
    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }
    
//    /**
//     * ConnectionFactory alternative using JNDI reference
//     * @return
//     */
//    @Bean
//    public ConnectionFactory jndiConnectionFactory() {
//    	try {
//	        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
//	        factory.setJndiName("java:/comp/ConnectionFactory");
//	        factory.setProxyInterface(ConnectionFactory.class);
//	        factory.setLookupOnStartup(true);
//	        factory.afterPropertiesSet();
//	        return (ConnectionFactory) factory.getObject();
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    	return null;
//    }
}