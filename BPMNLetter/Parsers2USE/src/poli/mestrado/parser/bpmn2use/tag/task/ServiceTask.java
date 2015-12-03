package poli.mestrado.parser.bpmn2use.tag.task;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;

public class ServiceTask extends AbstractTaskElement {
	public static final String TAG_NAME = "serviceTask";

	public ServiceTask(String id, String name, List<Documentation> documentationList, Integer completionQuantity,
			Boolean isForCompensation, Integer startQuantity,
			List<AbstractCondition> prePostConditionList,
			List<DataObject> dataInputList, List<DataObject> dataOutList) {
		super(id, name, documentationList, completionQuantity, isForCompensation, startQuantity,
				prePostConditionList, dataInputList, dataOutList);
		// TODO Auto-generated constructor stub
	}

}
