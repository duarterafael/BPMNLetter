package poli.mestrado.parser.bpmn2use.tag.event;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;

public abstract class AbstractEventElement extends AbstractBaseElement {
	private EnumEventElementType eventElementType = EnumEventElementType.NONE;
	

	public AbstractEventElement(String id, String name,
			List<Documentation> documentations, EnumEventElementType eventElementType) {
		super(id, name, documentations);
		this.eventElementType = eventElementType;
	}


	public EnumEventElementType getEventElementType() {
		return eventElementType;
	}


	public void setEventElementType(EnumEventElementType eventElementType) {
		this.eventElementType = eventElementType;
	}
	
	
	
}
