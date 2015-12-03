package poli.mestrado.parser.bpmn2use.tag.event;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class EndEvent extends AbstractBaseElement {
	
	public static final String TAG_NAME = "endEvent";
	
	
	public EndEvent(String id, String name, List<Documentation> documentationList) {
		super(id, name, documentationList);
	}

	

}
