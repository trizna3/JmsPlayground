package sk.trizna.jms.consumer.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import sk.trizna.jms.consumer.dto.MyRequestDto;

@WebService(targetNamespace="http://example.com/", name = "ConsumerService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ConsumerService {

	public String manage(
			@WebParam(partName = "part", name = "MyRequestDto", targetNamespace = "http://example.com/") 
			MyRequestDto request
			);
}
