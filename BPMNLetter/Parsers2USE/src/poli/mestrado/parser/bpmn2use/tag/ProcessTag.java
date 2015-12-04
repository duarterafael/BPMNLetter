package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Swimlanes.LaneTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.SequenceFlowTag;


@SuppressWarnings("serial")
public class ProcessTag implements Serializable{
	public static final String TAG_NAME = "process";
	private String id;
	private LinkedList<SequenceFlowTag> sequenceFlowList;
	private LinkedList<AbstractBaseElement> flowElementList;
	private LinkedList<LaneTag> laneList;
	
	
	public ProcessTag(String id,   LinkedList<AbstractBaseElement> flowElementList, LinkedList<SequenceFlowTag> sequenceFlowList){
		this.id = id;
		this.sequenceFlowList = new LinkedList<SequenceFlowTag>();
		this.sequenceFlowList.addAll(sequenceFlowList);
		this.flowElementList = new LinkedList<AbstractBaseElement>();
		this.flowElementList.addAll(flowElementList);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		if(flowElementList != null){
			for (AbstractBaseElement abstractBaseElement : flowElementList) {
				str += abstractBaseElement.toString()+"\n";
			}
		}
		if(sequenceFlowList != null){
			for (SequenceFlowTag sequenceFlowTag : sequenceFlowList) {
				str += sequenceFlowTag.toString()+"\n"; 
			}
		}
		
		if(laneList != null){
			for (AbstractBaseElement abstractBaseElement : laneList) {
				str += abstractBaseElement.toString()+"\n"; 
			}
		}
		
		return "Process ID = "+id+"\n"+str;
	}

	public LinkedList<SequenceFlowTag> getSequenceFlowList() {
		return sequenceFlowList;
	}

	public LinkedList<AbstractBaseElement> getFlowElementList() {
		return flowElementList;
	}

	public LinkedList<LaneTag> getLaneList() {
		return laneList;
	}

	public void setLaneList(List<LaneTag> laneList) {
		this.laneList = (LinkedList<LaneTag>) laneList;
	}
	
	public boolean hasFlowWElement(String id){
		for (AbstractBaseElement abstractBaseElement : flowElementList) {
			if(abstractBaseElement.getId().equalsIgnoreCase(id)){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
}
