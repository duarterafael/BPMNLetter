package poli.mestrado.parser.bpmn2use.tag.gateway;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class ParallelGateway extends AbstractGatewayElement {
	

	public static final String TAG_NAME = "parallelGateway";

	public ParallelGateway(String id, String name, List<Documentation> documentationList, String gatewayDirection) {
		super(id, name, documentationList , gatewayDirection);
	}
	
	@Override
	public String toString() {
		return "Type = "+TAG_NAME + " "+super.toString();
	}

}
