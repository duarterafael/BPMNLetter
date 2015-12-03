package poli.mestrado.parser.bpmn2use.tag.Swimlanes;

import java.util.LinkedList;
import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;

public class LaneTag extends AbstractBaseElement{
	public static final String 	TAG_NAME = "lane";
	public static final String FLOWNODE_REF = "flowNodeRef";
	
	private List<AbstractBaseElement> flowElementList;

	public LaneTag(String id, String name, List<Documentation> documentationList, List<AbstractBaseElement> flowElementList) {
		super(id, name, documentationList);
		this.flowElementList = flowElementList;
	}
	
	public LaneTag(String id, String name, List<Documentation> documentationList) {
		super(id, name, documentationList);
		this.flowElementList = new LinkedList<AbstractBaseElement>();
	}

	public List<AbstractBaseElement> getFlowElementList() {
		return flowElementList;
	}

	
	public void addElement(AbstractBaseElement flowElement){
		this.flowElementList.add(flowElement);
	}
	
	public boolean hasFlowWElement(String id){
		for (AbstractBaseElement abstractBaseElement : flowElementList) {
			if(abstractBaseElement.getId().equalsIgnoreCase(id)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "flowElementContent: ";
		
		for (AbstractBaseElement abstractBaseElement : flowElementList) {
			str+=abstractBaseElement.getId()+" ";
		}
		return str;
	}

}
