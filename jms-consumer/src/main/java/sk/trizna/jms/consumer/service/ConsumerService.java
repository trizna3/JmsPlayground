package sk.trizna.jms.consumer.service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import sk.trizna.jms.consumer.dto.MyRequestDto;
import sk.trizna.jms.consumer.dto.MyResponseDto;

@WebService(targetNamespace="http://example.com/", name = "ConsumerService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ConsumerService {

	@WebResult(partName = "part", name = "MyResponseDto", targetNamespace = "http://example.com/")
	public MyResponseDto sayHello(
			@WebParam(partName = "part", name = "MyRequestDto", targetNamespace = "http://example.com/") 
			MyRequestDto request
			);
}
