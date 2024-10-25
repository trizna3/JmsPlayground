package sk.trizna.jms.consumer.listener;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {
    
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        System.out.println("Hello from LoggingHandler!");
        
//        if (outboundProperty) {
//            System.out.println("Outbound message:");
//        } else {
//            System.out.println("Inbound message:");
//        }
//        
//        // Log the SOAP message
//        try {
//			System.out.println(context.getMessage().getSOAPBody().getTextContent());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        
        return true; // continue processing
    }

    public boolean handleFault(SOAPMessageContext context) {
        System.out.println("Fault occurred:");
        System.out.println(context.getMessage().toString());
        return true; // continue processing
    }

    public void close(MessageContext context) {
        // Clean up resources if necessary
    }

    public Set<QName> getHeaders() {
        return null; // Return null to handle all headers
    }
}
