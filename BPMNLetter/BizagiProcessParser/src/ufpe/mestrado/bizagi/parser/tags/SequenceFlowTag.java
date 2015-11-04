package ufpe.mestrado.bizagi.parser.tags;


public class SequenceFlowTag extends AbstractBaseObjetct{
	
	public static final String TAG_NAME = "sequenceFlow";
	public static final String ATTRIBUTE_TAG_SOURCE_REF = "sourceRef";
	public static final String ATTRIBUTE_TAG_TARGET_REF = "targetRef";
	public static final String 	CONDITIONEXPRESSION_TAG_NAME = "conditionExpression";
	
	private AbstractBaseObjetct sourceRef;
	private AbstractBaseObjetct targetRef;
	private String conditionExpression;
	
	public SequenceFlowTag(String id, AbstractBaseObjetct sourceRef, AbstractBaseObjetct targetRef, String conditionExpression) {
		super(id);
		this.sourceRef = sourceRef;
		this.targetRef = targetRef;
		this.conditionExpression = conditionExpression;
	}


	public AbstractBaseObjetct getSourceRef() {
		return sourceRef;
	}

	public AbstractBaseObjetct getTargetRef() {
		return targetRef;
	}
	
	public String getConditionExpression() {
		return conditionExpression;
	}

	
	@Override
	public String toString() {
		String str = conditionExpression == null? "": " conditionExpression = "+conditionExpression;
		return "ID = "+ super.getId() + " sourceRef = "+sourceRef.getId() + " targetRef =  " + targetRef.getId() +str ;
	}

}
