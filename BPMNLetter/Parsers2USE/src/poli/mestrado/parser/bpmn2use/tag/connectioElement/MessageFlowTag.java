package poli.mestrado.parser.bpmn2use.tag.connectioElement;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;

public class MessageFlowTag extends AbstractConnectionElement{

	public static final String TAG_NAME = "messageFlow";

	private String messagem;
	
	public MessageFlowTag(String id, AbstractBaseElement sourceRef,
			AbstractBaseElement targetRef, String messagem) {
		super(id, sourceRef, targetRef);
		this.messagem = messagem;
	}
	
	public String getMessagem() {
		return messagem;
	}

	@Override
	public String toString() {
		String str = messagem == null? "": " messagem = "+messagem;
		return super.toString()+" "+str ;
	}	
	
	

}
