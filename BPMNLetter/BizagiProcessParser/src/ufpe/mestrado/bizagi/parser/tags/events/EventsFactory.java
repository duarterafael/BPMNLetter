package ufpe.mestrado.bizagi.parser.tags.events;

import ufpe.mestrado.bizagi.parser.tags.AbstractBaseObjetct;

public class EventsFactory {
	public static AbstractBaseObjetct buildEvent(String tagName, String id, String name, Boolean isInterrupting, Boolean parallelMultiple) {
		AbstractBaseObjetct event = null;
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
