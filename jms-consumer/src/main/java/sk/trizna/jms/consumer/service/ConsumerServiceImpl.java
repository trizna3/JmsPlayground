package sk.trizna.jms.consumer.service;

import sk.trizna.jms.consumer.dto.MyRequestDto;
import sk.trizna.jms.consumer.dto.MyResponseDto;

public class ConsumerServiceImpl implements ConsumerService {

	public MyResponseDto sayHello(MyRequestDto request) {
		System.out.println("Hello from ConsumerServiceImpl!");
		
		MyResponseDto resp = new MyResponseDto();
		resp.setMessage("Hello");
		return resp;
	}
}
