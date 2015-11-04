package poli.mestrado.parser.bpmn2use.tag.connectioElement;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;

public class MessageFlowTag extends AbstractConnectionElement{

	public static final String TAG_NAME = "messageFlow";
	
	public MessageFlowTag(String id, AbstractBaseElement sourceRef,
			AbstractBaseElement targetRef) {
		super(id, sourceRef, targetRef);
	}

}
