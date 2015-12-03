package poli.mestrado.parser.bpmn2use.tag.task;


import java.util.List;

import poli.mestrado.parser.bpmn2use.TagTransformerAsslCommand;
import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.PosCondition;
import poli.mestrado.parser.bpmn2use.tag.PreCondition;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;
import poli.mestrado.parser.util.MyConstants;

public abstract class AbstractTaskElement extends AbstractBaseElement {

	public static final String COMPLETIONQUANTITY_ATTRIBUTE = "completionQuantity";
	public static final String ISFORCOMPENSATION_ATTRIBUTE = "isForCompensation";
	public static final String STARTQUANTITY_ATTRIBUTE = "startQuantity";

	private Integer completionQuantity;
	private Boolean isForCompensation;
	private Integer startQuantity;
	private List<AbstractCondition> prePostConditionList;
	private List<DataObject> dataInputList;
	private List<DataObject> dataOutList;



	public AbstractTaskElement(String id, String name, List<Documentation> documentationList, 
			Integer completionQuantity, Boolean isForCompensation,
			Integer startQuantity,
			List<AbstractCondition> prePostConditionList,
			List<DataObject> dataInputList, List<DataObject> dataOutList) {
		super(id, name, documentationList);
		this.completionQuantity = completionQuantity;
		this.isForCompensation = isForCompensation;
		this.startQuantity = startQuantity;
		this.prePostConditionList = prePostConditionList;
		this.dataInputList = dataInputList;
		this.dataOutList = dataOutList;
	}

	public int getCompletionQuantity() {
		return completionQuantity;
	}

	public Boolean getIsForCompensation() {
		return isForCompensation;
	}

	public int getStartQuantity() {
		return startQuantity;
	}

	public String getAsslCall(){
		return "AsslCall "+super.getName().replace(" ", "_")+"();";
	}

	public String getDataInputs(){
		String param = "";

		if(dataInputList != null && !dataInputList.isEmpty()){
			for (DataObject dtInput : dataInputList) {
				param += dtInput.toString()+" ,";
			}
		}

		return param;
	}

	public String getDataOutputs(){
		String varArgs = "";

		if(dataOutList != null && !dataOutList.isEmpty()){
			for (DataObject dtOut : dataOutList) {
				varArgs += dtOut.toString()+" ,";
			}
		}


		return varArgs;
	}
	


	public String getAsslComan(){
		String preConditonListStr = "";
		String postConditionListStr = "";

		
		for (int i = 0; i < prePostConditionList.size(); i++) {
			if(prePostConditionList.get(i) instanceof PreCondition){
				if(MyConstants.asslScriptWhithLog){
					preConditonListStr += "\n\t-------Pre condition name: "+prePostConditionList.get(i).getName()+"-------------\n";
				}
				for (String asslComand : TagTransformerAsslCommand.conditionExpression2AsslCommand(prePostConditionList.get(i).getConditionExpression())) {
					preConditonListStr += "\t\t"+asslComand.replace("\n", "").trim()+"\n";
				}
			}else if(prePostConditionList.get(i) instanceof PosCondition){
				if(MyConstants.asslScriptWhithLog){
					postConditionListStr += "\n\t-------Pos condition name: "+prePostConditionList.get(i).getName()+"-------------\n";
				}
				for (String asslComand : TagTransformerAsslCommand.conditionExpression2AsslCommand(prePostConditionList.get(i).getConditionExpression())) {
					postConditionListStr += "\t\t"+asslComand.replace("\n", "").trim()+"\n";
				}
			}
		}
		if(MyConstants.asslScriptWhithLog){
			if(!preConditonListStr.equals("")){
				preConditonListStr = "\n\n---------------BEGIN Pre condtion List---------------"+preConditonListStr+"\n---------------END Pre condtion List---------------\n";
			}else{
				preConditonListStr += "\n\n---------------Pre condtion List is empty---------------";
			}
			if(!postConditionListStr.equals("")){
				postConditionListStr = "\n\n---------------BEGIN Pos condtion List---------------"+postConditionListStr+"\n---------------END Pos condtion List---------------\n";
			}else{
				preConditonListStr += "\n\n---------------Pos condtion List is empty---------------";
			}
		}
	
		

		String str = "";
		if(MyConstants.asslScriptWhithLog){
			str += "\n------BEGIN TASK id: "+super.getId()+" Name: "+super.getName()+" ---------";
		}
		str+=preConditonListStr;
		str+=postConditionListStr;

		if(MyConstants.asslScriptWhithLog){
			str+= "\n------------END TASK id: "+super.getId()+" Name: "+super.getName()+" ---------";
		}
		return str;
	}


	public List<DataObject> getDataInputList() {
		return dataInputList;
	}


	public List<DataObject> getDataOutList() {
		return dataOutList;
	}

	public List<AbstractCondition> getPrePostConditionList() {
		return prePostConditionList;
	}

	public void setPrePostConditionList(List<AbstractCondition> prePostConditionList) {
		this.prePostConditionList = prePostConditionList;
	}
	
	



}
