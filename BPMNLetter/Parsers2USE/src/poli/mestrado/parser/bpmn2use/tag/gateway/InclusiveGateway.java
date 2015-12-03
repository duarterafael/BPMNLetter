package poli.mestrado.parser.bpmn2use.tag.gateway;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class InclusiveGateway extends AbstractGatewayElement {
	

	public static final String TAG_NAME = "inclusiveGateway";

	public InclusiveGateway(String id, String name, List<Documentation> documentationList, String gatewayDirection) {
		super(id, name, documentationList, gatewayDirection);
	}
	
	@Override
	public String toString() {
		return "Type = "+TAG_NAME + " "+super.toString();
	}

}
