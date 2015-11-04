package poli.mestrado.parser.bpmn2use.graph;
import java.io.Serializable;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;


@SuppressWarnings("serial")
public class Vertice implements Comparable<Vertice>, Serializable{
	
	public enum VerticeState {
		UNEXPLORED,
		REACHED,
		EXPLORED;

	}
	
	private VerticeState state;
	private AbstractBaseElement veriticeElement;
	
	public Vertice( AbstractBaseElement veriticeElement) {
		super();
		this.state = VerticeState.UNEXPLORED;
		this.veriticeElement = veriticeElement;
	}
	

	public VerticeState getState() {
		return state;
	}



	public void changeState(){
		if(this.state.equals(VerticeState.UNEXPLORED)){
			this.state = VerticeState.REACHED;
		}else if(GraphHelper.getInstance().isAllEdgeWasExploredToSouceVertice(this)){
			this.state = VerticeState.EXPLORED;
		}
	}


	public AbstractBaseElement getVeriticeElement() {
		return veriticeElement;
	}



	public void setVeriticeElement(AbstractBaseElement veriticeElement) {
		this.veriticeElement = veriticeElement;
	}



	@Override
	public int compareTo(Vertice o) {
		return o.getVeriticeElement().compareTo(this.getVeriticeElement());
	}
	
	@Override
	public String toString() {
		return this.veriticeElement.getId() +"   "+ this.veriticeElement.getName();
	}
	
	
	
	

}


