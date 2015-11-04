package poli.mestrado.parser.bpmn2use.tag.gateway;

public class ParallelGateway extends AbstractGatewayElement {
	

	public static final String TAG_NAME = "parallelGateway";

	public ParallelGateway(String id, String name, String gatewayDirection) {
		super(id, name, gatewayDirection);
	}
	
	@Override
	public String toString() {
		return "Type = "+TAG_NAME + " "+super.toString();
	}

}
