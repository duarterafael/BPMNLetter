package poli.mestrado.parser.bpmn2use.tag.task;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;

public class SendTask  extends AbstractTaskElement {
	
	public static final String TAG_NAME = "sendTask";

	public SendTask(String id, String name, Integer completionQuantity,
			Boolean isForCompensation, Integer startQuantity,
			List<AbstractCondition> prePostConditionList,
			List<DataObject> dataInputList, List<DataObject> dataOutList) {
		super(id, name, completionQuantity, isForCompensation, startQuantity,
				prePostConditionList, dataInputList, dataOutList);
		// TODO Auto-generated constructor stub
	}

}
