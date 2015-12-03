package poli.mestrado.parser.bpmn2use.graph;

import java.io.Serializable;

import poli.mestrado.parser.util.MyConstants;

@SuppressWarnings("serial")
public class Edge implements Serializable{
	
	public enum EdgeState {
		UNEXPLORED,
		EXPLORED;
	}
	
	private EdgeState state;
	private	Vertice sourceVertice, targetVertice;
	private String conditionExpression;
	
	public Edge(Vertice source, Vertice target, String conditionExpression) {
		super();
		this.state = EdgeState.UNEXPLORED;
		this.sourceVertice = source;
		this.targetVertice = target;
		this.conditionExpression = conditionExpression;
	}


	public Vertice getSourceVertice() {
		return sourceVertice;
	}

	public Vertice getTargetVertice() {
		return targetVertice;
	}

	public void changeState(){
		if(this.state.equals(EdgeState.UNEXPLORED)){
			this.state = EdgeState.EXPLORED;
		}
	}
	
	public EdgeState getState() {
		return state;
	}
	

	public String getConditionExpression() {
		return conditionExpression;
	}
	
	public String getAsslConditionComand(){
		return "if ["+ this.conditionExpression +" ] then ";
	}
	
	public String getUseShellComand(){
		if(MyConstants.VERBOSE_CONTION){
			return "??"+this.conditionExpression;
		}else{
			return "?"+this.conditionExpression;
		}
	}
	


	@Override
	public String toString() {
		return "SourceVertice = "+this.sourceVertice.getVeriticeElement().getId() + " TargetVertice = "+this.targetVertice.getVeriticeElement().getId();
	}
	
	
	
	
}
