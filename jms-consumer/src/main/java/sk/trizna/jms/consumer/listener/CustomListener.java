package sk.trizna.jms.consumer.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.w3c.dom.Node;

import sk.trizna.jms.consumer.dto.MyRequestDto;
import sk.trizna.jms.consumer.dto.MyResponseDto;
import sk.trizna.jms.consumer.service.ConsumerService;
import sk.trizna.jms.consumer.service.ConsumerServiceImpl;

public class CustomListener implements MessageListener{

	private ConnectionFactory connectionFactory;
	private ConsumerService serviceProxy;
	private MessageFactory soapMessageFactory;
	private Marshaller respMarshaller;
	private Unmarshaller reqUnmarshaller;
	
	public CustomListener(ConnectionFactory connectionFactory) {
		super();
		this.connectionFactory = connectionFactory;
		createService();
	}
	
	public void onMessage(Message message) {
		Long currMillis = System.currentTimeMillis();
		try {
			if (message instanceof TextMessage) {
				String messageText = ((TextMessage) message).getText();
				System.out.println("Accepted message: " + messageText);
				SOAPMessage soapMessage = getSoapMessageFactory().createMessage(null, new ByteArrayInputStream(messageText.getBytes()));
				Node requestElement = soapMessage.getSOAPBody().getFirstChild();
	            System.out.println("request namespace: " + requestElement.getNamespaceURI());
	            
                JAXBElement<MyRequestDto> request = getReqUnmarshaller().unmarshal(requestElement, MyRequestDto.class);
                
                // invoke service
                Object response = getServiceProxy().sayHello(request.getValue());
                
                sendResponse(((ActiveMQQueue)message.getJMSReplyTo()).getQueueName(),marshallResponse(response));
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Message processed in " + (System.currentTimeMillis()-currMillis) + " millis");
	}
	
	private Object marshallResponse(Object o) throws JAXBException, SOAPException, IOException {
		
		// create empty soap envelope
        SOAPMessage soapMessage = getSoapMessageFactory().createMessage();
        SOAPBody soapBody = soapMessage.getSOAPBody();
        
        // create soap body & marshall response object into it
        SOAPElement responseElement = soapBody.addBodyElement(
        		soapMessage.getSOAPPart().getEnvelope().createName("MyResponseDto", "urn", "http://example.com/")
        		);
        getRespMarshaller().marshal(o, responseElement);

        // marshall SOAPMessage (soap envelope) into xml string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
	}
	
	private void sendResponse(String replyToQueue, Object response) {
	    try {
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();
            
            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Create a MessageProducer
            MessageProducer producer = session.createProducer(session.createQueue(replyToQueue));
            
            // Create a message
            TextMessage message = session.createTextMessage(String.valueOf(response));
            // Send the message
            producer.send(message);
            System.out.println("Response sent: " + message.getText());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void createService() {
		// create service
		JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
		factory.setServiceClass(ConsumerService.class);
		factory.setServiceBean(new ConsumerServiceImpl());
        factory.setAddress("local://ConsumerService");
        factory.getHandlers().add(new LoggingHandler());
        factory.create();
		
		// create proxy to that service
		JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
		proxyFactory.setServiceClass(ConsumerService.class);
		proxyFactory.setAddress("local://ConsumerService");
        serviceProxy = (ConsumerService) proxyFactory.create();
		
	}
	
	private ConsumerService getServiceProxy() {
		return serviceProxy;
	}
	
	private MessageFactory getSoapMessageFactory() throws SOAPException {
		if (soapMessageFactory == null) {
			soapMessageFactory = MessageFactory.newInstance();
		}
		return soapMessageFactory;
	}
	
	private Marshaller getRespMarshaller() throws JAXBException {
		if (respMarshaller == null) {
			JAXBContext jaxbContext = JAXBContext.newInstance(MyResponseDto.class);
			respMarshaller = jaxbContext.createMarshaller();
			respMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}
		return respMarshaller;
	}
	
	private Unmarshaller getReqUnmarshaller() throws JAXBException {
		if (reqUnmarshaller == null) {
			JAXBContext jaxbContext = JAXBContext.newInstance(MyRequestDto.class);
			reqUnmarshaller = jaxbContext.createUnmarshaller();
		}
		return reqUnmarshaller;
	}
}
