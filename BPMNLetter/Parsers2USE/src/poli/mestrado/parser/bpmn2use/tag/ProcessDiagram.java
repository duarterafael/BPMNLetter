package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;
import java.util.LinkedList;

import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.MessageFlowTag;

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
	
	

}
