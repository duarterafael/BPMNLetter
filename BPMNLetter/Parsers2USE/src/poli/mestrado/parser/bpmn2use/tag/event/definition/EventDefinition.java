package poli.mestrado.parser.bpmn2use.tag.event.definition;

public class EventDefinition {
	private String id;
	private String condition;
	private EventDefinitionType timerDefinitionType = EventDefinitionType.NONE;
	
	public EventDefinition(String id, String condition, EventDefinitionType timerDefinitionType) {
		this.id = id;
		this.condition = condition;
		this.timerDefinitionType = timerDefinitionType;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

	public EventDefinitionType getTimerDefinitionType() {
		return timerDefinitionType;
	}

	public void setTimerDefinitionType(EventDefinitionType timerDefinitionType) {
		this.timerDefinitionType = timerDefinitionType;
	}
	
	
}
