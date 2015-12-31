package poli.mestrado.parser.bpmn2use.tag.event.definition;


public enum EventDefinitionType {
	NONE("", ""), 
	TIME_CYCLE("timeDuration", "duração"), 
	TIME_DATE("timeDate", "data"),
	TIME_DURATION("timeCycle", "ciclo");
	
	private final String description; 
	private final String msg;
	
	EventDefinitionType(String description, String msg){ 
		this.description = description; 
		this.msg =  msg;
	}
	public static EventDefinitionType buildEnum(String description){
		for (EventDefinitionType enumType : values()) {
			if(enumType.getDescription().equalsIgnoreCase(description)){
				return enumType;
			}
		}
		return null;
	}
	
	public String getDescription(){ 
		return description; 
	}
	
	public String getMsg(){ 
		return msg; 
	}
}
