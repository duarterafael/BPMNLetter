package poli.mestrado.parser.bpmn2use.tag.event;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;

public class EventsFactory {
	public static AbstractBaseElement buildEvent(String tagName, String id, String name, Boolean isInterrupting, Boolean parallelMultiple) {
		AbstractBaseElement event = null;
		switch (tagName) {
		case StartEvent.TAG_NAME:
			event = new StartEvent(id, name, isInterrupting, parallelMultiple);
			break;
		case EndEvent.TAG_NAME:
			event = new EndEvent(id, name);
			break;
		case IntermediateCatchEvent.TAG_NAME:
			event = new IntermediateCatchEvent(id, name, parallelMultiple);
			break;
		case IntermediateThrowEvent.TAG_NAME:
			event = new IntermediateThrowEvent(id, name);
			break;
		}
		return event;
    }

}
