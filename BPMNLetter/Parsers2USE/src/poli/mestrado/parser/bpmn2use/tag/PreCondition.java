package poli.mestrado.parser.bpmn2use.tag;

public class PreCondition extends AbstractCondition {
	
	public static final String TAG_NAME = "PreCondition";

	public PreCondition(String id, String name, String conditionExpression) {
		super(id, name, conditionExpression);
	}

}
