package poli.mestrado.parser.bpmn2use.tag.gateway;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class ExclusiveGateway extends AbstractGatewayElement {
	

	public static final String TAG_NAME = "exclusiveGateway";

	public ExclusiveGateway(String id, String name, List<Documentation> documentationList, String gatewayDirection) {
		super(id, name, documentationList, gatewayDirection);
	}
	

}
