package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import poli.mestrado.parser.bpmn2use.graph.Edge;
import poli.mestrado.parser.bpmn2use.graph.Vertice;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.LaneTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.MessageFlowTag;
import poli.mestrado.parser.util.MyConstants;

public class ProcessDiagram implements Serializable{
	
	private ProcessTag singleProcess;
	private LinkedList<ParticipantTag> participantList;
	private LinkedList<MessageFlowTag> messageFlowList;

	public ProcessDiagram(ProcessTag singleProcess, LinkedList<ParticipantTag> participantList, LinkedList<MessageFlowTag> messageFlowList) {
		super();
		this.participantList = participantList;
		this.messageFlowList = messageFlowList;
	}
	
	public ProcessDiagram(){
		super();
		singleProcess = null;
		this.participantList = new LinkedList<ParticipantTag>();
		this.messageFlowList = new LinkedList<MessageFlowTag>();
	}

	public ProcessTag getSingleProcess() {
		return singleProcess;
	}
	
	public void setSingleProcess(ProcessTag singleProcess) {
		this.singleProcess =  singleProcess;
	}

	public LinkedList<ParticipantTag> getParticipantList() {
		return participantList;
	}
	
	public void addParticipant(ParticipantTag participantTag){
		this.participantList.add(participantTag);
	}
	

	public LinkedList<MessageFlowTag> getMessageFlowList() {
		return messageFlowList;
	}
	
	public void addmessageFlow(MessageFlowTag messageFlow){
		this.messageFlowList.add(messageFlow);
	}
	
	@Override
	public String toString() {
		String str  = "";
		
		if(!messageFlowList.isEmpty()){
			for (MessageFlowTag messageFlowTag : messageFlowList) {
				str += messageFlowTag.toString()+"\n";
			}
		}
		if(!participantList.isEmpty()){
			for (ParticipantTag processTag : participantList) {
				str += processTag.toString();
			}
		}else{
			str += singleProcess.toString();
		}
		return str;
	}
	
	public LaneTag getOwnerLane(AbstractBaseElement element){
		for (ParticipantTag participantTag : participantList) {
			if(participantTag.getProcessRef() != null && participantTag.getProcessRef().getLaneList() != null){
				for (LaneTag lane : participantTag.getProcessRef().getLaneList()) {
					if(lane.getFlowElementList() != null){
						for (AbstractBaseElement flowElementTag : lane.getFlowElementList()) {
							if(flowElementTag.getId().equalsIgnoreCase(element.getId())){
								return lane;
							}
						}
					}
					
				}
			}
		}
		return null;
	}
	
	public ParticipantTag getOwnerProcess(AbstractBaseElement element){
		for (ParticipantTag participantTag : participantList) {
			if(participantTag.getProcessRef() != null){
				for (AbstractBaseElement abstractBaseElement : participantTag.getProcessRef().getFlowElementList()) {
					if(abstractBaseElement.getId().equalsIgnoreCase(element.getId())){
						return participantTag;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @param v
	 * @param kindOfVertice  0-souce vertice / 1-targe Vertice
	 * @return
	 */
	public List<MessageFlowTag> getAllMessageFlowToVertice(AbstractBaseElement v, int kindOfVertice){
		List<MessageFlowTag> allEdgeLinkedToVerticeList =  new LinkedList<MessageFlowTag>();

		for (MessageFlowTag messageFlowTag : messageFlowList) {

			if(kindOfVertice == MyConstants.SOURCE_VERTICE){
				if(messageFlowTag.getSourceRef().getId().equals(v.getId())){
					allEdgeLinkedToVerticeList.add(messageFlowTag);
				}
			}else if(kindOfVertice == MyConstants.TARGET_VERTICE){
				if(messageFlowTag.getTargetRef().getId().equals(v.getId())){
					allEdgeLinkedToVerticeList.add(messageFlowTag);
				}
			}
		}
		return allEdgeLinkedToVerticeList;
	}

	
	

}
