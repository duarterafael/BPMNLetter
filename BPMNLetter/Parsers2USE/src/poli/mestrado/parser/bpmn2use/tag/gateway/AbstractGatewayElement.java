package poli.mestrado.parser.bpmn2use.tag.gateway;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.util.MyConstants;

public abstract class AbstractGatewayElement extends AbstractBaseElement {
	
	public static final String 	GATEWAYDIRECTION_ATTRIBUTE = "gatewayDirection";
	
	private String  gatewayDirection;
	
	public AbstractGatewayElement(String id, String name, List<Documentation> documentationList, 
			String gatewayDirection) {
		super(id, name, documentationList);
		this.gatewayDirection = gatewayDirection;
	}



	public String getGatewayDirection() {
		return gatewayDirection;
	}
	
	public int getKindGateway(){
		if(this.gatewayDirection.equalsIgnoreCase("Diverging")){
			return MyConstants.GATEWAY_FORK;
		}else if(this.gatewayDirection.equalsIgnoreCase("Converging")){
			return MyConstants.GATEWAY_JOIN;
		}else{
			return MyConstants.GATEWAY_UNKNOWN;
		}
	}



	@Override
	public String toString() {
		return super.toString()+" gatewayDirection = "+gatewayDirection;	
	}
	
	
	
	
}
