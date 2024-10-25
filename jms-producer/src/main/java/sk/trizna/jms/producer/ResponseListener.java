package sk.trizna.jms.producer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ResponseListener implements MessageListener{

	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String messageText = ((TextMessage) message).getText();
				System.out.println("Response accepted: " + messageText);

	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
