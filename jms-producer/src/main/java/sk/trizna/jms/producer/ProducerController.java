package sk.trizna.jms.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class ProducerController {

	@GetMapping("/test")
	public void test() {
		try {
            // Create a ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();
            
            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Create a Destination (Queue or Topic)
            Destination destination = session.createQueue("TEST.QUEUE_REQ");
            Destination replyDestination = session.createQueue("TEST.QUEUE_RESP");
            
            // Create a MessageProducer
            MessageProducer producer = session.createProducer(destination);
            
            // Create a message
//            TextMessage message = session.createTextMessage("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:tatrabanka:ESB:services:SRContactApi:002\" xmlns:urn1=\"urn:tatrabanka:types:EATypes:003\"><soapenv:Header/><soapenv:Body><urn:ManageContactsRequest><urn1:RequestAuditInfo><ChannelID>BRN</ChannelID><AppName>IFCA</AppName><WorkstationID>WBCUNI004</WorkstationID><PostTime>2023-02-01T09:28:34.968+02:00</PostTime><ClientID>4412397195922</ClientID><ReferenceID>1254d6b5c5991aedf21f08dde460b35e62790905</ReferenceID><SessionID>1254d6b5c5991aedf21f08dde460b35e62790905</SessionID><Subversion>0</Subversion></urn1:RequestAuditInfo><urn:BrandID>001</urn:BrandID><urn:BrandClientID>50418202202</urn:BrandClientID><urn:Version>7</urn:Version><urn:ContactsToUpdate><urn:ContactToUpdate><urn:ContactID>68280231623</urn:ContactID><urn:Phone><urn:Number>908400249</urn:Number></urn:Phone></urn:ContactToUpdate></urn:ContactsToUpdate></urn:ManageContactsRequest></soapenv:Body></soapenv:Envelope>");
            TextMessage message = session.createTextMessage("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body xmlns:ns=\"http://dummy.namespace\"><urn:MyRequestDto xmlns:urn=\"http://example.com/\"><username>Janko</username><password>test</password></urn:MyRequestDto></soapenv:Body></soapenv:Envelope>");
            message.setJMSReplyTo(replyDestination);
            // Send the message
            producer.send(message);
            System.out.println("Request sent: " + message.getText());
            
            // Clean up
            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
