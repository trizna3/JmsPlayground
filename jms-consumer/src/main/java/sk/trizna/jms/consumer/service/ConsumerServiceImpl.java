package sk.trizna.jms.consumer.service;

import sk.trizna.jms.consumer.dto.MyRequestDto;

public class ConsumerServiceImpl implements ConsumerService {

	public String manage(MyRequestDto request) {
		System.out.println("Hello from ConsumerServiceImpl.manage!");
		return "Ahoj";
	}
}
