package poli.mestrado.parser.bpmn2use.tag.event;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.event.definition.EventDefinition;

public abstract class AbstractEventElement extends AbstractBaseElement {
	private EnumEventElementType eventElementType = EnumEventElementType.NONE;
	private EventDefinition eventDefinition;
	
	

	public AbstractEventElement(String id, String name,
			List<Documentation> documentations, EnumEventElementType eventElementType,
			EventDefinition eventDefinition) {
		super(id, name, documentations);
		this.eventElementType = eventElementType;
		this.eventDefinition = eventDefinition;
	}


	public EnumEventElementType getEventElementType() {
		return eventElementType;
	}


	public void setEventElementType(EnumEventElementType eventElementType) {
		this.eventElementType = eventElementType;
	}


	public EventDefinition getEventDefinition() {
		return eventDefinition;
	}


	public void setEventDefinition(EventDefinition eventDefinition) {
		this.eventDefinition = eventDefinition;
	}
	
	
	
	
	
}
