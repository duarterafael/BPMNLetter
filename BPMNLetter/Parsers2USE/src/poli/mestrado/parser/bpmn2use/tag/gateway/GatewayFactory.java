package poli.mestrado.parser.bpmn2use.tag.gateway;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class GatewayFactory {

	public static AbstractGatewayElement buildGateway(String typeGateway, String id, String name, List<Documentation> documentationList, String gatewayDirection) {
		AbstractGatewayElement gatewayElement = null;
		switch (typeGateway) {
		case ExclusiveGateway.TAG_NAME:
			gatewayElement = new ExclusiveGateway(id, name, documentationList, gatewayDirection);
			break;
		case ParallelGateway.TAG_NAME:
			gatewayElement = new ParallelGateway(id, name, documentationList, gatewayDirection);
			break;
		case InclusiveGateway.TAG_NAME:
			gatewayElement = new InclusiveGateway(id, name, documentationList, gatewayDirection);
			break;
		case ComplexGateway.TAG_NAME:
			gatewayElement = new ComplexGateway(id, name, documentationList, gatewayDirection);
			break;

		}

		return gatewayElement;
	}

}
