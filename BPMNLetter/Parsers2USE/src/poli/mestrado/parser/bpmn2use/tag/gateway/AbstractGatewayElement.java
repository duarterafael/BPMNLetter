package poli.mestrado.parser.bpmn2use.tag.gateway;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.util.Constants;

public abstract class AbstractGatewayElement extends AbstractBaseElement {
	
	public static final String 	GATEWAYDIRECTION_ATTRIBUTE = "gatewayDirection";
	
	private String  gatewayDirection;
	
	public AbstractGatewayElement(String id, String name,
			String gatewayDirection) {
		super(id, name);
		this.gatewayDirection = gatewayDirection;
	}



	public String getGatewayDirection() {
		return gatewayDirection;
	}
	
	public int getKindGateway(){
		if(this.gatewayDirection.equalsIgnoreCase("Diverging")){
			return Constants.GATEWAY_FORK;
		}else if(this.gatewayDirection.equalsIgnoreCase("Converging")){
			return Constants.GATEWAY_JOIN;
		}else{
			return Constants.GATEWAY_UNKNOWN;
		}
	}



	@Override
	public String toString() {
		return super.toString()+" gatewayDirection = "+gatewayDirection;	
	}
	
	
	
	
}
