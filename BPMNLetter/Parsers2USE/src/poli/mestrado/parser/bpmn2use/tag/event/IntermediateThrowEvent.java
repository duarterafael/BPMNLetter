package poli.mestrado.parser.bpmn2use.tag.event;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class IntermediateThrowEvent extends AbstractEventElement {
	
	public static final String TAG_NAME = "intermediateThrowEvent";
	
	
	public IntermediateThrowEvent(String id, String name, List<Documentation> documentationList, EnumEventElementType eventElementType) {
		super(id, name, documentationList, eventElementType);
	}

	

}
