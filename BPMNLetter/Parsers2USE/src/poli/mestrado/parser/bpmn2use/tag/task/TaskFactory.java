package poli.mestrado.parser.bpmn2use.tag.task;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;

public class TaskFactory {
	public static AbstractTaskElement buildTask(String typeTask, String id, String name, List<Documentation> documentationList, Integer completionQuantity, Boolean isForCompensation, Integer startQuantity, List<AbstractCondition>  prePostContionList,  List<DataObject> dataInputList, List<DataObject> dataOutList) {
		AbstractTaskElement taskElement = null;
		switch (typeTask) {
		case DefualtTask.TAG_NAME:
			taskElement = new DefualtTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case SendTask.TAG_NAME:
			taskElement = new SendTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case ReceiveTask.TAG_NAME:
			taskElement = new ReceiveTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case ServiceTask.TAG_NAME:
			taskElement = new ServiceTask(id, name,  documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case UserTask.TAG_NAME:
			taskElement = new UserTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case ManualTask.TAG_NAME:
			taskElement = new ManualTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case ScriptTask.TAG_NAME:
			taskElement = new ScriptTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		case BusinessRuleTask.TAG_NAME:
			taskElement = new BusinessRuleTask(id, name, documentationList, completionQuantity, isForCompensation, startQuantity, prePostContionList, dataInputList, dataOutList);
			break;
		}
		return taskElement;
    }

}
