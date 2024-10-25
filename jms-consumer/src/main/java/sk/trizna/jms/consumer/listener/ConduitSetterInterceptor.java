package sk.trizna.jms.consumer.listener;


import java.util.Collection;

import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.UpfrontConduitSelector;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;

public class ConduitSetterInterceptor extends AbstractPhaseInterceptor<Message> {

	public ConduitSetterInterceptor() {
		super(Phase.USER_LOGICAL);
	}

	public void handleMessage(Message message) throws Fault {
		UpfrontConduitSelector upF = new UpfrontConduitSelector();
		upF.setEndpoint(message.getExchange().getEndpoint());
		message.getExchange().put(ConduitSelector.class, upF);
	}
	
	public void handleFault(Message message) {
		UpfrontConduitSelector upF = new UpfrontConduitSelector();
		upF.setEndpoint(message.getExchange().getEndpoint());
		message.getExchange().put(ConduitSelector.class, upF);
	}

	@Override
	public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
		// TODO Auto-generated method stub
		return super.getAdditionalInterceptors();
	}
	
	
}
