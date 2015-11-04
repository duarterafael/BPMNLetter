package ufpe.mestrado.bizagi.parser.tags;

import java.util.LinkedList;


@SuppressWarnings("serial")
public class ProcessTag extends AbstractBaseObjetct{
	public static final String TAG_NAME = "process";
	private LinkedList<SequenceFlowTag> sequenceFlowList;
	private LinkedList<AbstractBaseObjetct> abstractBaseElementList;
	
	
	public ProcessTag(String id, LinkedList<AbstractBaseObjetct> abstractBaseElement, LinkedList<SequenceFlowTag> sequenceFlowList){
		super(id);
		this.sequenceFlowList = new LinkedList<SequenceFlowTag>();
		this.sequenceFlowList.addAll(sequenceFlowList);
		this.abstractBaseElementList = new LinkedList<AbstractBaseObjetct>();
		this.abstractBaseElementList.addAll(abstractBaseElement);
	}
	
	@Override
	public String toString() {
		String str = "";
		
		if(abstractBaseElementList != null){
			for (AbstractBaseObjetct abstractBaseElement : abstractBaseElementList) {
				str += abstractBaseElement.toString()+"\n";
			}
		}
		if(sequenceFlowList != null){
			for (SequenceFlowTag sequenceFlowTag : sequenceFlowList) {
				str += sequenceFlowTag.toString()+"\n"; 
			}
		}
		
		return "Process ID = "+super.getId()+"\n"+str;
	}

	public LinkedList<SequenceFlowTag> getSequenceFlowList() {
		return sequenceFlowList;
	}

	public LinkedList<AbstractBaseObjetct> getAbstractBaseElementList() {
		return abstractBaseElementList;
	}
	
	
}
