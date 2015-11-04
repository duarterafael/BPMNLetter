package poli.mestrado.parser.bpmn2use.tag.gateway;

public enum EnumEventGatewayType {
	Exclusive("Exclusive"),
	Parallel("Parallel");
	
	private final String type;
	EnumEventGatewayType(String type) { this.type = type; }
    
	public String getValue() { return type; }
    
    public static EnumEventGatewayType getValue(String strType){
    	
    	for (EnumEventGatewayType enumType : values()) {
			if(enumType.getValue().equalsIgnoreCase(strType)){
				return enumType;
			}
		}
    	return null;
    }
    

}
