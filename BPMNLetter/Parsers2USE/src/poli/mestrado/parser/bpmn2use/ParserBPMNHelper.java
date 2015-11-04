package poli.mestrado.parser.bpmn2use;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.AbstractCondition;
import poli.mestrado.parser.bpmn2use.tag.ICommunProcessAttributeTag;
import poli.mestrado.parser.bpmn2use.tag.PosCondition;
import poli.mestrado.parser.bpmn2use.tag.PreCondition;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.ProcessTag;
import poli.mestrado.parser.bpmn2use.tag.SubProcessTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.LaneTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.AbstractConnectionElement;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.MessageFlowTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.SequenceFlowTag;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataInput;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObjectFactory;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataOutput;
import poli.mestrado.parser.bpmn2use.tag.event.EndEvent;
import poli.mestrado.parser.bpmn2use.tag.event.EventsFactory;
import poli.mestrado.parser.bpmn2use.tag.event.IntermediateCatchEvent;
import poli.mestrado.parser.bpmn2use.tag.event.IntermediateThrowEvent;
import poli.mestrado.parser.bpmn2use.tag.event.StartEvent;
import poli.mestrado.parser.bpmn2use.tag.gateway.AbstractGatewayElement;
import poli.mestrado.parser.bpmn2use.tag.gateway.ComplexGateway;
import poli.mestrado.parser.bpmn2use.tag.gateway.EnumEventGatewayType;
import poli.mestrado.parser.bpmn2use.tag.gateway.EventBasedGateway;
import poli.mestrado.parser.bpmn2use.tag.gateway.ExclusiveGateway;
import poli.mestrado.parser.bpmn2use.tag.gateway.GatewayFactory;
import poli.mestrado.parser.bpmn2use.tag.gateway.InclusiveGateway;
import poli.mestrado.parser.bpmn2use.tag.gateway.ParallelGateway;
import poli.mestrado.parser.bpmn2use.tag.task.AbstractTaskElement;
import poli.mestrado.parser.bpmn2use.tag.task.BusinessRuleTask;
import poli.mestrado.parser.bpmn2use.tag.task.DefualtTask;
import poli.mestrado.parser.bpmn2use.tag.task.ManualTask;
import poli.mestrado.parser.bpmn2use.tag.task.ReceiveTask;
import poli.mestrado.parser.bpmn2use.tag.task.ScriptTask;
import poli.mestrado.parser.bpmn2use.tag.task.SendTask;
import poli.mestrado.parser.bpmn2use.tag.task.ServiceTask;
import poli.mestrado.parser.bpmn2use.tag.task.TaskFactory;
import poli.mestrado.parser.bpmn2use.tag.task.UserTask;



public class ParserBPMNHelper {
	private static ParserBPMNHelper instance = null;
	private DocumentBuilder documentBuilde;

	private List<DataObject> dataObjectList;

	private ParserBPMNHelper(){
		try {
			documentBuilde = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			dataObjectList = new LinkedList<DataObject>();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}  
	}

	public static ParserBPMNHelper getInstance(){
		if(instance == null){
			instance = new ParserBPMNHelper();
		}
		return instance;
	}

	

	public ProcessDiagram getProcessDiagram(){
		ProcessDiagram processDiagram = new ProcessDiagram();
		try {
			File f = BpmnFileManager.getInstance().getExportFile();
			
			Document doc = documentBuilde.parse(f);  
			doc.getDocumentElement().normalize();

			//obtem os n processos existentes
			List<ProcessTag> processList = new LinkedList<ProcessTag>();

			NodeList nodeProcessList = doc.getElementsByTagName(ProcessTag.TAG_NAME);  
			for (int i = 0; i < nodeProcessList.getLength(); i++) {
				Element processTag = (Element) nodeProcessList.item(i);
				processList.add(getProcess(processTag));
			}
			//Associa os processo as sua respectivas pscinas caso n tenha nenhuma pscina é um processo unico.
			NodeList nodeParticipantList = doc.getElementsByTagName(ParticipantTag.TAG_NAME);
			if(nodeParticipantList.getLength() > 0){
				for (int i = 0; i < nodeParticipantList.getLength(); i++) {
					Element participantTag = (Element) nodeParticipantList.item(i);
					processDiagram.addParticipant(getParticipant(participantTag,processList));
				}
			}else{
				if(processList.size() > 0){
					processDiagram.setSingleProcess(processList.get(0));
				}
			}
			
			//obter as messageFlow entre os elementos da BPMN
			NodeList nodeNessageFlowList = doc.getElementsByTagName(MessageFlowTag.TAG_NAME);
			if(nodeNessageFlowList.getLength() > 0){
				for (int i = 0; i < nodeNessageFlowList.getLength(); i++) {
					Element messageFlowTag = (Element) nodeNessageFlowList.item(i);
					processDiagram.addmessageFlow((getMessageFlow(messageFlowTag,processDiagram)));
				}
			}
			
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return processDiagram; 
	}
	
	private MessageFlowTag getMessageFlow(Element messageFlowTag, ProcessDiagram processDiagram) {
		
		String id = messageFlowTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
		String sourceRef = messageFlowTag.getAttribute(AbstractConnectionElement.ATTRIBUTE_TAG_SOURCE_REF);
		String targetRef = messageFlowTag.getAttribute(AbstractConnectionElement.ATTRIBUTE_TAG_TARGET_REF);
		
		MessageFlowTag messageFlow = new MessageFlowTag(id,findElementById(processDiagram, sourceRef),findElementById(processDiagram, targetRef));
		
		return messageFlow;
	}
	

	public List<LaneTag> getAllLanes(Element processTag, ProcessTag process){
		List<LaneTag> laneList = new LinkedList<LaneTag>();
		
		NodeList laneTagList = processTag.getElementsByTagName(LaneTag.TAG_NAME);

		if(laneTagList != null){
			for (int i = 0; i < laneTagList.getLength(); i++) {

				Element laneTag = (Element) laneTagList.item(i);

				String id = laneTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = laneTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				
				LaneTag lane = new LaneTag(id, name);


				NodeList flowNodeRefList = laneTag.getElementsByTagName(LaneTag.FLOWNODE_REF);
				if(flowNodeRefList != null){
					for (int j = 0; j < flowNodeRefList.getLength(); j++) {
						Element flowNodeRef = (Element) flowNodeRefList.item(j);
						String flowNodeRefId = flowNodeRef.getFirstChild().getNodeValue();
						AbstractBaseElement element = findElementById(process.getFlowElementList(), flowNodeRefId);
						if(element != null){
							lane.addElement(element);
						}
					}
				}
				laneList.add(lane);
			}

		}
		return laneList;
		
		
		
	}
	
	public ParticipantTag getParticipant(Element participantTag, List<ProcessTag> processList){
		
		
		String idParticipant = participantTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
		String name = participantTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
		String processRefString = participantTag.getAttribute(ParticipantTag.PROCESS_REF);
		
		ProcessTag processRef = null;
		if(processRefString != null && !processRefString.equals("")){
			for (ProcessTag processTag : processList) {
				if(processTag.getId().equalsIgnoreCase(processRefString)){
					processRef = processTag;
					break;
				}
			}
		}
		
		ParticipantTag ParticipantTag = new ParticipantTag(idParticipant, name, processRef);
				
		return ParticipantTag;
	}

	
	private ProcessTag getProcess(Element processTag){
		ProcessTag processModel = null;
		
		String idProcess = processTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);

		LinkedList<AbstractBaseElement> elementList = getAllElements(processTag);

		LinkedList<SequenceFlowTag> sequenceFlowList = getAllSequenceFlow(processTag, elementList);

		processModel =  new ProcessTag(idProcess, elementList, sequenceFlowList);
		
		processModel.setLaneList(getAllLanes(processTag, processModel));
		
		return processModel;
	}

	
	
//	public ProcessTag parserBpmn(){
//		try {
//			File f =
//					BpmnFileManager.getInstance().getExportFile();
////				new File("C:/Users/Rafael/Desktop/Nova pasta (3)/bpmnmodel.bpmn");
////			System.out.println(ParserBPMNHelper.class.getName()+""+ParserBPMNHelper.class.getName()+"O arquvi"+f.getAbsolutePath()+" vai ser parseado");
//			
//			Document doc = documentBuilde.parse(f);  
//			doc.getDocumentElement().normalize();  
//
//			NodeList nodeList = doc.getElementsByTagName(ProcessTag.TAG_NAME);  
//			if(nodeList.getLength() > 0){
//				Element processTag = (Element) nodeList.item(0);
//
//				String idProcess = processTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
//
//				LinkedList<AbstractBaseElement> elementList = getAllElements(processTag);
//
//				LinkedList<SequenceFlowTag> sequenceFlowList = getAllSequenceFlow(processTag, elementList);
//
//				processModel =  new ProcessTag(idProcess, elementList, sequenceFlowList);
//
//			}
//
//		} catch (SAXException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return processModel; 
//	}
//

	private LinkedList<SequenceFlowTag> getAllSequenceFlow(Element processTag, LinkedList<AbstractBaseElement> elementList) {

		LinkedList<SequenceFlowTag> specificKindofTaskList = new LinkedList<SequenceFlowTag>();

		NodeList sequenceFlowTagList = processTag.getElementsByTagName(SequenceFlowTag.TAG_NAME);

		if(sequenceFlowTagList != null){
			for (int i = 0; i < sequenceFlowTagList.getLength(); i++) {

				Element sequencflowTag = (Element) sequenceFlowTagList.item(i);

				String id = sequencflowTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String sourceRef = sequencflowTag.getAttribute(SequenceFlowTag.ATTRIBUTE_TAG_SOURCE_REF);
				String targetRef = sequencflowTag.getAttribute(SequenceFlowTag.ATTRIBUTE_TAG_TARGET_REF);

				String conditionExpression = null;

				NodeList conditionExpressionTagList = sequencflowTag.getElementsByTagName(SequenceFlowTag.CONDITIONEXPRESSION_TAG_NAME);
				if(conditionExpressionTagList != null){
					for (int j = 0; j < conditionExpressionTagList.getLength(); j++) {
						Element conditionExpressionTag = (Element) conditionExpressionTagList.item(j);

						conditionExpression = conditionExpressionTag.getTextContent();
					}
				}


				specificKindofTaskList.add(new SequenceFlowTag(id, findElementById(elementList, sourceRef), findElementById(elementList, targetRef), conditionExpression));
			}

		}else{
			return null;
		}


		return specificKindofTaskList;

	}
	
	private AbstractBaseElement findElementById (ProcessDiagram processDiagram, String id){
		if(processDiagram.getParticipantList() != null && !processDiagram.getParticipantList().isEmpty()){
			for (ParticipantTag participantTag : processDiagram.getParticipantList()){
				if(participantTag.getId().equalsIgnoreCase(id)){
					return  participantTag;
				}else if(participantTag.getProcessRef() != null){
					AbstractBaseElement flowElemet = findElementById(participantTag.getProcessRef().getFlowElementList(), id);
					if(flowElemet != null){
						return flowElemet;
					}
				}
			}
		}else{
			return findElementById(processDiagram.getSingleProcess().getFlowElementList(), id);
		}
		return null;
	}

	private AbstractBaseElement findElementById(LinkedList<AbstractBaseElement> elementList, String id){
		for (AbstractBaseElement abstractBaseElement : elementList) {
			if(abstractBaseElement.getId().equalsIgnoreCase(id)){
				return abstractBaseElement;
			}
		}
		return null;
	}

	private SequenceFlowTag findSequenceFlowById(LinkedList<SequenceFlowTag> sequenceFlowList, String id){
		for (SequenceFlowTag sequenceFlow : sequenceFlowList) {
			if(sequenceFlow.getId().equalsIgnoreCase(id)){
				return sequenceFlow;
			}
		}
		return null;
	}

	private LinkedList<AbstractBaseElement> getAllElements(Element processTag){
		LinkedList<AbstractBaseElement> abstractElementsList = new LinkedList<AbstractBaseElement>();

		// DataObject
		Element ioSpecification = (Element) processTag.getElementsByTagName(DataObject.IOSPECIFICATION_TAG).item(0);
		if(ioSpecification != null){
			dataObjectList.addAll(getSpecificKindOfDataObjectList(ioSpecification, DataInput.TAG_NAME));
			dataObjectList.addAll(getSpecificKindOfDataObjectList(ioSpecification, DataOutput.TAG_NAME));
		}
		
		//subprocess
		abstractElementsList.addAll(getSubprocesstLis(processTag));

		//Events
		abstractElementsList.addAll(getSpecificKindOfEventLis(processTag, StartEvent.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfEventLis(processTag, EndEvent.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfEventLis(processTag, IntermediateCatchEvent.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfEventLis(processTag, IntermediateThrowEvent.TAG_NAME));

		//Tasks
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, DefualtTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, SendTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, ReceiveTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, ServiceTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, UserTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, ManualTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, ScriptTask.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfTaskList(processTag, BusinessRuleTask.TAG_NAME));

		//Gateways
		abstractElementsList.addAll(getSpecificKindOfGatewayList(processTag, ExclusiveGateway.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfGatewayList(processTag, ParallelGateway.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfGatewayList(processTag, InclusiveGateway.TAG_NAME));
		abstractElementsList.addAll(getSpecificKindOfGatewayList(processTag, ComplexGateway.TAG_NAME));

		//EventBasedGateway
		abstractElementsList.addAll(getEventBasedGatewayList(processTag));

		return abstractElementsList;

	}
	
	private LinkedList<AbstractBaseElement> getSubprocesstLis(Element processTag){
		LinkedList<AbstractBaseElement> subprocesstLis = new LinkedList<AbstractBaseElement>();
		
		NodeList abstractEventTagList = processTag.getElementsByTagName(SubProcessTag.TAG_NAME);
		if(abstractEventTagList != null){
			for (int i = 0; i < abstractEventTagList.getLength(); i++) {
				Element subProcessTag = (Element) abstractEventTagList.item(i);
				
				String id = subProcessTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = subProcessTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				Integer completionQuantity = new Integer(subProcessTag.getAttribute(SubProcessTag.COMPLETION_QUANTITY));
				Boolean isForCompensation = new Boolean(subProcessTag.getAttribute(SubProcessTag.IS_FORCOMPENSATION));
				Integer startQuantity = new Integer(subProcessTag.getAttribute(SubProcessTag.START_QUANTITY));
				Boolean triggeredByEvent =  new Boolean(subProcessTag.getAttribute(SubProcessTag.TRIGGERED_BY_EVENT));
				
				subprocesstLis.add(new SubProcessTag(id, name, completionQuantity, isForCompensation, startQuantity, triggeredByEvent));
			}
		}
		
		
		return subprocesstLis;
	}



	private LinkedList<AbstractBaseElement> getSpecificKindOfEventLis(Element processTag, String tagName) {
		LinkedList<AbstractBaseElement> specificKindofEventList = new LinkedList<AbstractBaseElement>();

		NodeList abstractEventTagList = processTag.getElementsByTagName(tagName);
		if(abstractEventTagList != null){
			for (int i = 0; i < abstractEventTagList.getLength(); i++) {
				Element eventTag = (Element) abstractEventTagList.item(i);

				String id = eventTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = eventTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);

				Boolean isInterrupting = null;
				String StrIsInterrupting = eventTag.getAttribute(StartEvent.ISINTERRUPTIOG_TAG) ;
				if(StrIsInterrupting != null && !StrIsInterrupting.equals("")){
					isInterrupting =  new Boolean(StrIsInterrupting);
				}

				Boolean parallelMultiple = null;
				String StrParallelMultiple = eventTag.getAttribute(StartEvent.PARALLELMULTIPLE_TAG) ;
				if(StrParallelMultiple != null && !StrParallelMultiple.equals("")){
					parallelMultiple =  new Boolean(StrParallelMultiple);
				}

				specificKindofEventList.add(EventsFactory.buildEvent(tagName, id, name, isInterrupting, parallelMultiple));
			}
		}

		return specificKindofEventList;
	}

	private List<DataObject> getDataObjectListAssociationToTask(Element taskTag, String kindAssociation){
		LinkedList<DataObject> dataObjectListAssociationToTaskList = new LinkedList<DataObject>();
		
		NodeList dataObjectAssociationTagList = taskTag.getElementsByTagName(kindAssociation);
		if(dataObjectAssociationTagList != null){
			for (int i = 0; i < dataObjectAssociationTagList.getLength(); i++) {
				Element dataObjectAssociationTag = (Element) dataObjectAssociationTagList.item(i);
				
				String dataObjectID = "";
				if(kindAssociation.equalsIgnoreCase(DataObject.DATAINPUTASSOCIATION_TAG)){
					dataObjectID =  dataObjectAssociationTag.getElementsByTagName("sourceRef").item(0).getTextContent();
				}else if(kindAssociation.equalsIgnoreCase(DataObject.DATAOUTPUTASSOCIATION_TAG)){
					dataObjectID = dataObjectAssociationTag.getElementsByTagName("targetRef").item(0).getTextContent();
				}
				
				
				for (DataObject dtObj : this.dataObjectList) {
					if(dtObj.getId().equalsIgnoreCase(dataObjectID)){
						dataObjectListAssociationToTaskList.add(dtObj);
						break;
					}
				}
				
			}
		}
		
		return dataObjectListAssociationToTaskList;
	}


	private LinkedList<AbstractBaseElement> getSpecificKindOfTaskList(Element processTag, String tagName){

		LinkedList<AbstractBaseElement> specificKindofTaskList = new LinkedList<AbstractBaseElement>();

		NodeList abstractTaskTagList = processTag.getElementsByTagName(tagName);
		if(abstractTaskTagList != null){
			for (int i = 0; i < abstractTaskTagList.getLength(); i++) {
				Element TaskTag = (Element) abstractTaskTagList.item(i);

				String id = TaskTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = TaskTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				Integer completionQuantity = new Integer(TaskTag.getAttribute(AbstractTaskElement.COMPLETIONQUANTITY_ATTRIBUTE));
				Boolean isForCompensation = new Boolean(TaskTag.getAttribute(AbstractTaskElement.ISFORCOMPENSATION_ATTRIBUTE));
				Integer startQuantity = new Integer(TaskTag.getAttribute(AbstractTaskElement.STARTQUANTITY_ATTRIBUTE));
				
				List<DataObject> dataInputObjects = getDataObjectListAssociationToTask(TaskTag, DataObject.DATAINPUTASSOCIATION_TAG);
				List<DataObject> dataOutObjects = getDataObjectListAssociationToTask(TaskTag, DataObject.DATAOUTPUTASSOCIATION_TAG);
				

				//----------------------------get pre post conditon of task------------------------------------------------
				LinkedList<AbstractCondition> prePostConditionList = new LinkedList<AbstractCondition> ();

				NodeList precontionTagList = TaskTag.getElementsByTagName(PreCondition.TAG_NAME);
				if(precontionTagList != null){
					for (int j = 0; j < precontionTagList.getLength(); j++) {
						Element PrecontionTag = (Element) precontionTagList.item(j);

						String conditionId = PrecontionTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
						String conditionName = PrecontionTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);

						String condtionExpression = PrecontionTag.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getTextContent();
//						System.out.println(condtionExpression);


						prePostConditionList.add(new PreCondition(conditionId,conditionName, condtionExpression));
					}
				}


				NodeList poscontionTagList = TaskTag.getElementsByTagName(PosCondition.TAG_NAME);
				if(poscontionTagList != null){
					for (int j = 0; j < poscontionTagList.getLength(); j++) {
						Element postcontionTag = (Element) poscontionTagList.item(j);

						String conditionId = postcontionTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
						String conditionName = postcontionTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);


						String condtionExpression = postcontionTag.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getTextContent();


						prePostConditionList.add(new PosCondition(conditionId,conditionName, condtionExpression));
					}
				}
				//-----------------------------------------------END------------------------------------------------------------------
				specificKindofTaskList.add(TaskFactory.buildTask(tagName, id, name, completionQuantity, isForCompensation, startQuantity, prePostConditionList, dataInputObjects, dataOutObjects));
			}

		}else{
			return null;
		}


		return specificKindofTaskList;

	}




	private LinkedList<AbstractGatewayElement> getEventBasedGatewayList(Element processTag){

		LinkedList<AbstractGatewayElement> specificKindofGatewayList = new LinkedList<AbstractGatewayElement>();

		NodeList abstractGatewayTagList = processTag.getElementsByTagName(EventBasedGateway.TAG_NAME);
		if(abstractGatewayTagList != null){
			for (int i = 0; i < abstractGatewayTagList.getLength(); i++) {
				Element gatewayTag = (Element) abstractGatewayTagList.item(i);

				String id = gatewayTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = gatewayTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				String gatewayDirection = gatewayTag.getAttribute(AbstractGatewayElement.GATEWAYDIRECTION_ATTRIBUTE);
				EnumEventGatewayType type = EnumEventGatewayType.getValue(gatewayTag.getAttribute(EventBasedGateway.EVENTGATEWAYTYPE_ATTRIBUTE));
				Boolean instantiate = new Boolean(gatewayTag.getAttribute(EventBasedGateway.INSTANTIATE_ATTRIBUTE));

				specificKindofGatewayList.add(new EventBasedGateway(id, name, gatewayDirection, type, instantiate));
			}

		}else{
			return null;
		}


		return specificKindofGatewayList;

	}

	private LinkedList<AbstractGatewayElement> getSpecificKindOfGatewayList(Element processTag, String tagName){

		LinkedList<AbstractGatewayElement> specificKindofGatewayList = new LinkedList<AbstractGatewayElement>();

		NodeList abstractGatewayTagList = processTag.getElementsByTagName(tagName);
		if(abstractGatewayTagList != null){
			for (int i = 0; i < abstractGatewayTagList.getLength(); i++) {
				Element gatewayTag = (Element) abstractGatewayTagList.item(i);

				String id = gatewayTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = gatewayTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				String gatewayDirection = gatewayTag.getAttribute(AbstractGatewayElement.GATEWAYDIRECTION_ATTRIBUTE);

				specificKindofGatewayList.add(GatewayFactory.buildGateway(tagName, id, name, gatewayDirection));
			}

		}else{
			return null;
		}


		return specificKindofGatewayList;

	}


	private LinkedList<DataObject> getSpecificKindOfDataObjectList(Element fatherTag, String tagName){

		LinkedList<DataObject> specificKindofDataObjectList = new LinkedList<DataObject>();

		NodeList dataObjectTagList = fatherTag.getElementsByTagName(tagName);
		if(dataObjectTagList != null){
			for (int i = 0; i < dataObjectTagList.getLength(); i++) {
				Element dataObjectTag = (Element) dataObjectTagList.item(i);
				String id = dataObjectTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_ID);
				String name = dataObjectTag.getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				Boolean isCollection = new Boolean( dataObjectTag.getAttribute(DataObject.ISCOLLECTION_ATTRIBUTE));

				String itemSubjectRef = dataObjectTag.getAttribute(ICommunProcessAttributeTag.ITEMSUBJECTREF_ATTRIBUTE);
				itemSubjectRef = itemSubjectRef == "" ? null : itemSubjectRef;

				String state =null;
				NodeList dataStateTagList = dataObjectTag.getElementsByTagName(DataObject.DATASTATE_TAG);
				if(dataStateTagList.getLength() > 0){
					state = ((Element)dataStateTagList.item(0)).getAttribute(ICommunProcessAttributeTag.ATTRIBUTE_TAG_NAME);
				}

				specificKindofDataObjectList.add(DataObjectFactory.buildDataObject(tagName, id, name, isCollection, state, itemSubjectRef));
			}

		}else{
			return null;
		}


		return specificKindofDataObjectList;

	}
	


}
