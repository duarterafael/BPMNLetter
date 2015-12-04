package poli.mestrado.parser.bpmn2use.tag.event;

public enum EnumEventElementType {
	NONE(""), 
	MESSAGE("messageEventDefinition"), 
	TIMER("timerEventDefinition"), 
	CONDITIONAL("conditionalEventDefinition"), 
	SIGINAL("signalEventDefinition"),
	
	ESCALATION("escalationEventDefinition"),
	ERROR("errorEventDefinition"),
	COMPENSATE("compensateEventDefinition"),
	
	TERMINATE("terminateEventDefinition"),
	LINK("linkEventDefinition");
	
	
	private final String description; 
	
	EnumEventElementType(String description){ 
		this.description = description; 
	}
	public static EnumEventElementType buildEnum(String description){
		for (EnumEventElementType enumType : values()) {
			if(enumType.getDescription().equalsIgnoreCase(description)){
				return enumType;
			}
		}
		return null;
	}
	
	public String getDescription(){ 
		return description; 
	}

	

}
