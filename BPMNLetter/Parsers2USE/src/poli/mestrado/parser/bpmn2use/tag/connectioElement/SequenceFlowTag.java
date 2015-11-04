package poli.mestrado.parser.bpmn2use.tag.connectioElement;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;

public class SequenceFlowTag extends AbstractConnectionElement{
	
	public static final String TAG_NAME = "sequenceFlow";
	public static final String 	CONDITIONEXPRESSION_TAG_NAME = "conditionExpression";
	
	private String conditionExpression;
	
	public SequenceFlowTag(String id, AbstractBaseElement sourceRef, AbstractBaseElement targetRef, String conditionExpression) {
		super(id, sourceRef, targetRef);
		this.conditionExpression = conditionExpression;
	}

	public String getConditionExpression() {
		return conditionExpression;
	}

	@Override
	public String toString() {
		String str = conditionExpression == null? "": " conditionExpression = "+conditionExpression;
		return super.toString()+" "+str ;
	}	
	
	
	
	
	
	

}
