package poli.mestrado.parser.bpmn2use.tag.gateway;

public class ComplexGateway extends AbstractGatewayElement {
	

	public static final String TAG_NAME = "complexGateway";

	public ComplexGateway(String id, String name, String gatewayDirection) {
		super(id, name, gatewayDirection);
	}
	
	@Override
	public String toString() {
		return "Type = "+TAG_NAME + " "+super.toString();
	}

}
