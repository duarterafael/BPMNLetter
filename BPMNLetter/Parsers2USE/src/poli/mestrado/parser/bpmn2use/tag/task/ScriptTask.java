package poli.mestrado.parser.bpmn2use.tag.task;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;

public class ScriptTask extends AbstractTaskElement {


	public static final String TAG_NAME = "scriptTask";

	public ScriptTask(String id, String name, Integer completionQuantity,
			Boolean isForCompensation, Integer startQuantity,
			List<AbstractCondition> prePostConditionList,
			List<DataObject> dataInputList, List<DataObject> dataOutList) {
		super(id, name, completionQuantity, isForCompensation, startQuantity,
				prePostConditionList, dataInputList, dataOutList);
	}

}
