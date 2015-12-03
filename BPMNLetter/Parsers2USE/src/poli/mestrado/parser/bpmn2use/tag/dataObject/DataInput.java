package poli.mestrado.parser.bpmn2use.tag.dataObject;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;

@SuppressWarnings("serial")
public class DataInput extends DataObject {
	
	public static final String 	TAG_NAME = "dataInput";
	
	

	public DataInput(String id, String name,List<Documentation> documentationList, Boolean isCollection, String state, String itemSubjectRef) {
		super(id, name, documentationList, isCollection, state, itemSubjectRef);
		
	}
	

	
	
	
	
	

}
