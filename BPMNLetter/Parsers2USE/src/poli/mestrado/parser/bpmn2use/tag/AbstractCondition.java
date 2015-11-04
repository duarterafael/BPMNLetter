package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;

public abstract class AbstractCondition implements Serializable{
	
	public static final String RESOURCEASSINNMENTEXPRESSION = "resourceAssignmentExpression";
	
	private String id;
	private String name;
	private String conditionExpression;
	
	public AbstractCondition(String id, String name, String conditionExpression) {
		super();
		this.id = id;
		this.name = name;
		this.conditionExpression = conditionExpression;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConditionExpression() {
		return conditionExpression;
	}

	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
	
	@Override
	public String toString() {
		return "ID = "+this.id+ " Name = "+this.name+" conditionExpression = "+this.conditionExpression;
	}

}
