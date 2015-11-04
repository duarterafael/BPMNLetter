package poli.mestrado.parser.bpmn2use.tag.gateway;

public class GatewayFactory {

	public static AbstractGatewayElement buildGateway(String typeGateway, String id, String name, String gatewayDirection) {
		AbstractGatewayElement gatewayElement = null;
		switch (typeGateway) {
		case ExclusiveGateway.TAG_NAME:
			gatewayElement = new ExclusiveGateway(id, name, gatewayDirection);
			break;
		case ParallelGateway.TAG_NAME:
			gatewayElement = new ParallelGateway(id, name, gatewayDirection);
			break;
		case InclusiveGateway.TAG_NAME:
			gatewayElement = new InclusiveGateway(id, name, gatewayDirection);
			break;
		case ComplexGateway.TAG_NAME:
			gatewayElement = new ComplexGateway(id, name, gatewayDirection);
			break;

		}

		return gatewayElement;
	}

}
