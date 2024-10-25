package sk.trizna.jms.consumer.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.transport.jms.spec.JMSSpecConstants;
import org.apache.cxf.transport.local.LocalConduit;
import org.apache.cxf.transport.local.LocalDestination;
import org.apache.cxf.transport.local.LocalTransportFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import sk.trizna.jms.consumer.dto.MyRequestDto;
import sk.trizna.jms.consumer.service.ConsumerService;
import sk.trizna.jms.consumer.service.ConsumerServiceImpl;

public class CustomListener implements MessageListener{

//	private Bus localBus;
//	private MyMessageObserver messageObserver;
//	private EndpointInfo endpointInfo;
	
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String messageText = ((TextMessage) message).getText();
				System.out.println("Accepted message: " + messageText);
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    		DocumentBuilder builder = factory.newDocumentBuilder();
	    		Document document = builder.parse(new ByteArrayInputStream(messageText.getBytes(StandardCharsets.UTF_8)));
	    		Element envelopeElement = document.getDocumentElement();
	    		NamedNodeMap attributes = envelopeElement.getAttributes();
	            for (int i = 0; i < attributes.getLength(); i++) {
	                Node attribute = attributes.item(i);
	                System.out.println("envelope namespace: " + attribute.getNodeValue());
	            }
	            
	            Node bodyElement = envelopeElement.getChildNodes().item(1);
	            attributes = bodyElement.getAttributes();
	            for (int i = 0; i < attributes.getLength(); i++) {
	                Node attribute = attributes.item(i);
	                System.out.println("body namespace: " + attribute.getNodeValue());
	            }
	            
	            Node requestElement = bodyElement.getFirstChild();
	            attributes = requestElement.getAttributes();
	            for (int i = 0; i < attributes.getLength(); i++) {
	                Node attribute = attributes.item(i);
	                System.out.println("request namespace: " + attribute.getNodeValue());
	            }
	            
	            JAXBContext jaxbContext = JAXBContext.newInstance(MyRequestDto.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<MyRequestDto> request = unmarshaller.unmarshal(requestElement, MyRequestDto.class);
                System.out.println(request.getValue());
                
                createService();
                ConsumerService service = lookupService();
                service.manage(request.getValue());
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createService() {
		JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
		factory.setServiceClass(ConsumerService.class);
//		factory.setServiceName(new QName("ConsumerService"));
		factory.setServiceBean(new ConsumerServiceImpl());
        factory.setAddress("local://ConsumerService"); // Use local address
        factory.getHandlers().add(new LoggingHandler());
//		factory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
//		factory.setHandlers(null);
//		factory.setBus(BusFactory.newInstance().createBus());
		// register local transport on bus
		
//		Bus localBus = BusFactory.newInstance().createBus();
//		localBus.getExtension(LocalTransportFactory.class); // Ensure LocalTransportFactory is present
//		
//		LocalTransportFactory transportFactory = new LocalTransportFactory();
//		DestinationFactoryManager dfm = localBus.getExtension(DestinationFactoryManager.class);
//		dfm.registerDestinationFactory(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID, transportFactory);
//		ConduitInitiatorManager cim = localBus.getExtension(ConduitInitiatorManager.class);
//		cim.registerConduitInitiator(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID, transportFactory);
//		// interceptors for conduit selection
//		factory.getOutInterceptors().add(new ConduitSetterInterceptor());
//		factory.getOutFaultInterceptors().add(new ConduitSetterInterceptor());
//		
//		factory.setBus(localBus);
		
		// create & start
		Server s = factory.create();
		
//		try {
//			ServiceInfo serviceInfo = new ServiceInfo();
//	        EndpointInfo endpointInfo = new EndpointInfo(serviceInfo, "local://ConsumerService");
//	        endpointInfo.setAddress("local://ConsumerService");
//			Destination destination = transportFactory.getDestination(endpointInfo, localBus);
//			destination.setMessageObserver(new MyMessageObserver());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		Endpoint endpoint = s.getEndpoint();
//		EndpointImpl endpointImpl = (EndpointImpl) endpoint;
//		endpointImpl.setMessageObserver(new MyMessageObserver());
//		try {
//			ServiceInfo serviceInfo = new ServiceInfo();
//	
//	        // Create EndpointInfo for your service
//	        EndpointInfo endpointInfo = new EndpointInfo(serviceInfo, "local://ConsumerService");
//	
//	        // Create LocalTransportFactory
//	        LocalTransportFactory localTransport = new LocalTransportFactory();
//	
//	        // Get the destination
//	        LocalDestination destination;
//			
//			destination = (LocalDestination) localTransport.getDestination(endpointInfo, localBus);
//		
//	        // Create and set the MessageObserver
//	        MyMessageObserver observer = new MyMessageObserver();
//	        destination.setMessageObserver(observer);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		s.start();
	}
	
	private ConsumerService lookupService() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(ConsumerService.class);
//		factory.setServiceName(new QName("ConsumerService"));
        factory.setAddress("local://ConsumerService"); // Use local address
//		factory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
		
//		Bus localBus = BusFactory.newInstance().createBus();
//		localBus.getExtension(LocalTransportFactory.class); // Ensure LocalTransportFactory is present
//		
//		LocalTransportFactory transportFactory = new LocalTransportFactory();
//		DestinationFactoryManager dfm = localBus.getExtension(DestinationFactoryManager.class);
//		dfm.registerDestinationFactory(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID, transportFactory);
//		ConduitInitiatorManager cim = localBus.getExtension(ConduitInitiatorManager.class);
//		cim.registerConduitInitiator(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID, transportFactory);
//		
//		// interceptors for conduit selection
//		factory.getOutInterceptors().add(new ConduitSetterInterceptor());
//		factory.getOutFaultInterceptors().add(new ConduitSetterInterceptor());
//		
//		factory.setBus(localBus);
		
		ConsumerService service = (ConsumerService) factory.create();
		
//		try {
//			ServiceInfo serviceInfo = new ServiceInfo();
//			EndpointInfo endpointInfo = new EndpointInfo(serviceInfo, "local://ConsumerService");
//			endpointInfo.setAddress("local://ConsumerService");
//			Destination destination = transportFactory.getDestination(endpointInfo, localBus);
//			destination.setMessageObserver(new MyMessageObserver());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		try {
//			ServiceInfo serviceInfo = new ServiceInfo();
//			EndpointInfo endpointInfo = new EndpointInfo(serviceInfo, "local://ConsumerService");
//			transportFactory.getDestination(endpointInfo, localBus);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println();
		return service;
	}
	
//	private Bus getLocalBus() {
//		if (localBus == null) {
//			localBus = BusFactory.newInstance().createBus();
//			localBus.getExtension(LocalTransportFactory.class); // Ensure LocalTransportFactory is present
//		}
//		return localBus;
//	}
//	
//	private MessageObserver getMessageObserver() {
//		if (messageObserver == null) {
//			messageObserver = new MyMessageObserver();
//		}
//		return messageObserver;
//	}
//	
//	public EndpointInfo getEndpointInfo() {
//		if (endpointInfo == null) {
//			ServiceInfo serviceInfo = new ServiceInfo();
//	        endpointInfo = new EndpointInfo(serviceInfo, "local://ConsumerService");
//		}
//		return endpointInfo;
//	}


}
