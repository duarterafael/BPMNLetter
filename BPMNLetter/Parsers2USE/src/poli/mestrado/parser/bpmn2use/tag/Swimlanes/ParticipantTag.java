package poli.mestrado.parser.bpmn2use.tag.Swimlanes;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.ProcessTag;

public class ParticipantTag extends AbstractBaseElement{
	

	public static final String 	TAG_NAME = "participant";
	public static final String PROCESS_REF = "processRef";
	
	private ProcessTag processRef = null;

	public ParticipantTag(String id, String name, ProcessTag processRef) {
		super(id, name);
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
