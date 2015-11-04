package poli.mestrado.parser.uml2use.tag;

import java.util.LinkedList;

import poli.mestrado.parser.bpmn2use.tag.connectioElement.SequenceFlowTag;

public abstract class AbstactFlowElementTag extends BaseTag {
	private String visibility;
	private Boolean isAbstract;
	private LinkedList<SequenceFlowTag> outGoingList;
	private LinkedList<SequenceFlowTag> inComingList;
	
	public AbstactFlowElementTag(String id, String name, String visibility,Boolean isAbstract) {
		super(id, name);
		this.visibility = visibility;
		this.isAbstract = isAbstract;
	}
	public Boolean getIsAbstract() {
		return isAbstract;
	}
	public void setIsAbstract(Boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public LinkedList<SequenceFlowTag> getOutGoingList() {
		return outGoingList;
	}
	public void setOutGoingList(LinkedList<SequenceFlowTag> outGoingList) {
		this.outGoingList = outGoingList;
	}
	public LinkedList<SequenceFlowTag> getInComingList() {
		return inComingList;
	}
	public void setInComingList(LinkedList<SequenceFlowTag> inComingList) {
		this.inComingList = inComingList;
	}
	
	
	


}
