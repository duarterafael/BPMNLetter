package poli.mestrado.parser.bpmn2use.tag.Swimlanes;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.ProcessTag;

public class ParticipantTag extends AbstractBaseElement{
	

	public static final String 	TAG_NAME = "participant";
	public static final String PROCESS_REF = "processRef";
	
	private ProcessTag processRef = null;

	public ParticipantTag(String id, String name, List<Documentation> documentationList, ProcessTag processRef) {
		super(id, name, documentationList);
		this.processRef = processRef;
	}

	public ProcessTag getProcessRef() {
		return processRef;
	}
	
	@Override
	public String toString() {
		String str = processRef != null ? processRef.toString() : "Private process";
		return super.toString()+"\n"+str;
	}
	
}
