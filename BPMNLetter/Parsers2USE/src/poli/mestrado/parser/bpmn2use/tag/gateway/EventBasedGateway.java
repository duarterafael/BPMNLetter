package poli.mestrado.parser.bpmn2use.tag.gateway;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class EventBasedGateway extends AbstractGatewayElement {
	
	public static final String TAG_NAME = "eventBasedGateway";
	public static final String EVENTGATEWAYTYPE_ATTRIBUTE = "eventGatewayType";
	public static final String INSTANTIATE_ATTRIBUTE = "instantiate";
	
	private EnumEventGatewayType type;
	private Boolean instantiate;
	
	
	public EventBasedGateway(String id, String name, List<Documentation> documentationList, String gatewayDirection,
			EnumEventGatewayType type, Boolean instantiate) {
		super(id, name, documentationList, gatewayDirection);
		this.type = type;
		this.instantiate = instantiate;
	}
	
	public EnumEventGatewayType getType() {
		return type;
	}

	public Boolean getInstantiate() {
		return instantiate;
	}

	@Override
	public String toString() {
		return "Type = "+TAG_NAME + " eventGatewayType = "+type.getValue()+" instantiate = "+instantiate+" "+ super.toString();
	}



}
