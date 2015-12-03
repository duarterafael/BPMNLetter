package poli.mestrado.parser.bpmn2use.tag.task;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;

public class UserTask extends AbstractTaskElement {



	public static final String TAG_NAME = "userTask";


	public UserTask(String id, String name, List<Documentation> documentationList, Integer completionQuantity,
			Boolean isForCompensation, Integer startQuantity,
			List<AbstractCondition> prePostConditionList,
			List<DataObject> dataInputList, List<DataObject> dataOutList) {
		super(id, name, documentationList, completionQuantity, isForCompensation, startQuantity,
				prePostConditionList, dataInputList, dataOutList);
	}


	@Override
	public String toString() {
		return "--------Type:  = "+TAG_NAME +" "+super.toString();
	}
}
