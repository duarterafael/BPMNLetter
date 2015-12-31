package poli.mestrado.parser.bpmn2use.tag.event;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.event.definition.EventDefinition;

public class EventsFactory {
	public static AbstractBaseElement buildEvent(String tagName, String id, String name, List<Documentation> documentationList,
			Boolean isInterrupting, Boolean parallelMultiple,  EnumEventElementType eventElementType, EventDefinition eventDefinition) {
		AbstractBaseElement event = null;
		switch (tagName) {
		case StartEvent.TAG_NAME:
			event = new StartEvent(id, name, documentationList, isInterrupting, parallelMultiple, eventElementType, eventDefinition);
			break;
		case EndEvent.TAG_NAME:
			event = new EndEvent(id, name, documentationList, eventElementType, eventDefinition);
			break;
		case IntermediateCatchEvent.TAG_NAME:
			event = new IntermediateCatchEvent(id, name, documentationList, parallelMultiple, eventElementType, eventDefinition);
			break;
		case IntermediateThrowEvent.TAG_NAME:
			event = new IntermediateThrowEvent(id, name, documentationList, eventElementType, eventDefinition);
			break;
		}
		return event;
    }

}
