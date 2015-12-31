package poli.mestrado.document.management;

import java.io.IOException;
import java.util.List;

import com.bpmn.letter.word.document.IDocumentMaker;
import com.bpmn.letter.word.document.WordDocumentHelper;

import poli.mestrado.parser.bpmn2use.graph.Edge;
import poli.mestrado.parser.bpmn2use.graph.Graph;
import poli.mestrado.parser.bpmn2use.graph.GraphHelper;
import poli.mestrado.parser.bpmn2use.graph.Vertice;
import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.LaneTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.MessageFlowTag;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;
import poli.mestrado.parser.bpmn2use.tag.event.AbstractEventElement;
import poli.mestrado.parser.bpmn2use.tag.event.EndEvent;
import poli.mestrado.parser.bpmn2use.tag.event.EnumEventElementType;
import poli.mestrado.parser.bpmn2use.tag.event.IntermediateCatchEvent;
import poli.mestrado.parser.bpmn2use.tag.event.IntermediateThrowEvent;
import poli.mestrado.parser.bpmn2use.tag.event.StartEvent;
import poli.mestrado.parser.bpmn2use.tag.gateway.ExclusiveGateway;
import poli.mestrado.parser.bpmn2use.tag.gateway.ParallelGateway;
import poli.mestrado.parser.bpmn2use.tag.task.AbstractTaskElement;
import poli.mestrado.parser.util.MyConstants;


public class DocumentHelper {
	private IDocument document  = new TxtDocument();

	private static DocumentHelper instance = null;

	public static DocumentHelper getInstance(){
		if(instance == null){
			instance = new DocumentHelper();
		}
		return instance;
	}
	private DocumentHelper(){

	}

	public void geneareDocument(String documentPath, ProcessDiagram processDiagram) throws IOException{

		System.out.println("\n--------------------------------resultado do parser------------------------------");
		System.out.println(processDiagram.toString());

		Graph graph1 = GraphHelper.getInstance().generateGraph(processDiagram);
		System.out.println("\n______________________________Gerando o grafo__________________________________________________________");
		System.out.println(graph1.toString());


		List<List<Vertice>>  letterComponetes = GraphHelper.getInstance().generateLetter();


		String ext = documentPath.substring(documentPath.lastIndexOf(".")+1);

		IDocumentMaker documentMaker = null;

		if(ext.equals("doc")){
			documentMaker = WordDocumentHelper.getInstance(documentPath);
		}else if(ext.equals("pdf")){

		}else if(ext.equals("html")){

		}

		documentMaker.insertCapa(getProcessName (processDiagram));


		String processImagePath = documentPath.substring(0, documentPath.lastIndexOf(".")+1)+"png";
		documentMaker.insertImage(processImagePath);

		//---------------------Não mexer aqui
		//		int qtdGateway = 0;
		//		int pathcount = 0;
		//		boolean isDifferentPath = false;
		//		String str = "";
		//		for(int i = 0; i < letterComponetes.size(); i++){
		//			List<Vertice> list = letterComponetes.get(i);
		//			if(isDifferentPath){
		//				System.out.println(str+"Caminho "+pathcount);
		//			}
		//			for (int j = 0; j < list.size(); j++) {
		//				str = "";
		//				Vertice vertice = list.get(j);
		//				if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
		//					isDifferentPath = true;
		//					qtdGateway ++;
		//				} 
		//				for (int k = 0; k < qtdGateway; k++) {
		//					 str = "\t";
		//				}
		//				System.out.println(str+vertice.getVeriticeElement().getName());
		//
		//				if(i != letterComponetes.size()-1 && GraphHelper.getKindGateway(letterComponetes.get(i+1).get(0)) == MyConstants.GATEWAY_JOIN){
		//					qtdGateway --;
		//					isDifferentPath = false;
		//					pathcount = 0;
		//				}
		//			
		//			}
		//			if(isDifferentPath){
		//				pathcount ++;
		//			}
		//		}
		//------------------------------------------------------

		//		//		System.out.println("\n--------------------------------Camhinhos gerados--------------------------------------------------------");
		//		int qtdGateway = 0;
		//		String str = "";
		//		
		//		for(int i = 0; i < letterComponetes.size(); i++){
		//			List<Vertice> list = letterComponetes.get(i);
		//			Vertice vertice = list.get(0);
		//			System.out.println(str+vertice.getVeriticeElement().getName());
		//			if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
		//				qtdGateway ++;
		//			}else if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
		//				qtdGateway --;
		//			}
		//			for (int j = 0; j < qtdGateway; j++) {
		//				str+="\t";
		//			}
		//			if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
		//				List<Edge> allEdgeLinkedToVertice = GraphHelper.getAllEdgeLinkedToVertice(vertice, MyConstants.SOURCE_VERTICE);
		//				int k = 1;
		//				System.out.println("Caminho: "+k);
		//				for (int j = i+1; j < allEdgeLinkedToVertice.size(); j++) {
		//					List<Vertice> list1 = letterComponetes.get(j);
		//					for (Vertice vertice2 : list1) {
		//						System.out.println(str+vertice2.getVeriticeElement().getName());
		//					}
		//				}
		//				k++;
		//				i = i+(allEdgeLinkedToVertice.size()+1);
		//			}
		//		}
		int i = 0;
		int qtdGateway = 0;
		String str = "";
		for (List<Vertice> list : letterComponetes) {
			if(GraphHelper.getKindGateway(list.get(0)) == MyConstants.GATEWAY_FORK){
				i++;
			}else if(GraphHelper.getKindGateway(list.get(0)) == MyConstants.GATEWAY_JOIN){
				i = 0;
			}
			for (Vertice vertice : list) {
				if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
					qtdGateway ++;
				}else if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
					qtdGateway --;
				}
				for (int j = 0; j < qtdGateway; j++) {
					str +="\t";
				}

				str += bpmnElemt2Letter(vertice, processDiagram);

				//						str+=vertice.getVeriticeElement().getClass().getName().substring(vertice.getVeriticeElement().getClass().getName().lastIndexOf(".")+1, vertice.getVeriticeElement().getClass().getName().length())
				//						+":"+vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+")"+"\n";
				////				documentMaker.insertParagraph(str1);
			}
		}
		System.out.println(str);
		documentMaker.insertText(str);

		documentMaker.finishDocument();

	}

	private String bpmnElemt2Letter(Vertice vertice, ProcessDiagram processDiagram){
		String str = "";

		List<Edge> allEdgeLinkedToVertice = GraphHelper.getAllEdgeLinkedToVertice(vertice, MyConstants.TARGET_VERTICE);
		for (Edge edge : allEdgeLinkedToVertice) {
			if(edge.getConditionExpression() != null && !edge.getConditionExpression().equals("")){
				str += "Se a condição "+edge.getConditionExpression()+" for validada: \n";
			}
		}


		if(vertice.getVeriticeElement() instanceof StartEvent){
			str += startEventFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof AbstractTaskElement){
			str += taskFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof EndEvent){
			str += endEventFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof IntermediateCatchEvent){
			str += intermediateCatchEventFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof IntermediateThrowEvent){
			str += intermediateThrowEventFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof ExclusiveGateway && GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
			str += exclusiveGatewayForkFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof ExclusiveGateway && GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
			str += exclusiveGatewayJoinFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof ParallelGateway && GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
			//TODO
			str += parallelGatewayForkFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof ParallelGateway && GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
			str += parallelGatewayJoinFragment(vertice, processDiagram);
		}else{
			str += "O elemento BPMN"+vertice.getVeriticeElement().getClass().getSimpleName()+" ainda não possui fragmento mapeado. ID = "+
					vertice.getVeriticeElement().getId()+ " Name= "+vertice.getVeriticeElement().getName();
		}
		return organizeBegingEndFragment(str);
	}

	private String intermediateThrowEventFragment(Vertice vertice,
			ProcessDiagram processDiagram) {
		String str = "";	

		str += " "+"ocorre um evento de"+" ";

		IntermediateThrowEvent intermediateThrowEvent = (IntermediateThrowEvent) vertice.getVeriticeElement();
		if(intermediateThrowEvent.getEventElementType() != null){
			if(intermediateThrowEvent.getEventElementType().equals(EnumEventElementType.MESSAGE)){
				str += " "+"mensagem que envia um(a) "+" "+intermediateThrowEvent.getName();
				List<MessageFlowTag> allMessageTargetFlowToVertice = processDiagram.getAllMessageFlowToVertice(vertice.getVeriticeElement(), MyConstants.SOURCE_VERTICE);
				if(!allMessageTargetFlowToVertice.isEmpty()){
					str += " "+"para "+" "+allMessageTargetFlowToVertice.get(0).getTargetRef().getName()+", após essa mensagem";
				}
			}
		}
		return organizeBegingEndFragment(str);
	}
	private String intermediateCatchEventFragment(Vertice vertice,
			ProcessDiagram processDiagram) {
		String str = "";

		str += " "+"ocorre um evento de"+" ";

		IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) vertice.getVeriticeElement();
		if(intermediateCatchEvent.getEventElementType() != null){
			if(intermediateCatchEvent.getEventElementType().equals(EnumEventElementType.TIMER)){

				str += " "+"tempo"+" "+intermediateCatchEvent.getName()+" ";
				if(intermediateCatchEvent.getEventDefinition() != null && intermediateCatchEvent.getEventDefinition().getCondition() != null){
					str += "com a condição de aguardar um(a) "+intermediateCatchEvent.getEventDefinition().getTimerDefinitionType().getMsg() +" de "+
							intermediateCatchEvent.getEventDefinition().getCondition();
				}
				str +=", após esse tempo"+" ";
			}else if(intermediateCatchEvent.getEventElementType().equals(EnumEventElementType.MESSAGE)){
				str += " "+"mensagem que aguarda o recebimento de um(a)"+" "+intermediateCatchEvent.getName();

				List<MessageFlowTag> allMessageTargetFlowToVertice = processDiagram.getAllMessageFlowToVertice(vertice.getVeriticeElement(), MyConstants.TARGET_VERTICE);
				if(!allMessageTargetFlowToVertice.isEmpty()){
					str += " "+"do(a)"+" "+allMessageTargetFlowToVertice.get(0).getSourceRef().getName()+", após essa mensagem";
				}
			}
		}


		return organizeBegingEndFragment(str);
	}
	private String exclusiveGatewayForkFragment(Vertice vertice,
			ProcessDiagram processDiagram) {
		String str = "";
		str += "Neste momento será verificado a seguinte condição"+" "+vertice.getVeriticeElement().getName()+" ";
		LaneTag ownerLane = processDiagram.getOwnerLane(vertice.getVeriticeElement());
		if(ownerLane != null){
			str += " "+"pelo(a)"+" "+ownerLane.getName()+".";
		}

		str += " "+"Essa condição testa as seguintes condições:"+" ";
		List<Edge> allEdgeLinkedToVertice = GraphHelper.getAllEdgeLinkedToVertice(vertice, MyConstants.SOURCE_VERTICE);
		if(allEdgeLinkedToVertice != null && !allEdgeLinkedToVertice.isEmpty()){
			for (Edge edge : allEdgeLinkedToVertice) {
				str += edge.getConditionExpression()+", ";

			}
			str = str.substring(0, str.length()-2);
			str += ".";
		}
		return organizeBegingEndFragment(str);
	}
	private String exclusiveGatewayJoinFragment(Vertice vertice,
			ProcessDiagram processDiagram) {
		String str = "";
		str += "Após as tarefas, neste ponto, será executado a junção dos caminhos exclusivos.";
		return organizeBegingEndFragment(str);
	}
	private String parallelGatewayForkFragment(Vertice vertice,	ProcessDiagram processDiagram) {
		String str = "";

		List<Edge> allEdgeLinkedToVertice = GraphHelper.getAllEdgeLinkedToVertice(vertice, MyConstants.SOURCE_VERTICE);
		str += "Neste momento será executado um paralelo de "+allEdgeLinkedToVertice.size()+" caminhos.";
		return organizeBegingEndFragment(str);
	}
	private String parallelGatewayJoinFragment(Vertice vertice,
			ProcessDiagram processDiagram) {
		String str = "";
		str += "Após as tarefas, neste ponto, será executado o paralelo que unifica todos os caminhos e após a unificação";
		return organizeBegingEndFragment(str);
	}
	private String taskFragment(Vertice vertice, ProcessDiagram processDiagram){
		String str = "";
		str += " "+"a tarefa"+" "+vertice.getVeriticeElement().getName()+" "+"será realizada";
		LaneTag ownerLane = processDiagram.getOwnerLane(vertice.getVeriticeElement());
		if(ownerLane != null){
			str += " "+"pelo(a)"+" "+ownerLane.getName()+".";
		}
		AbstractTaskElement abstractTaskElement = ((AbstractTaskElement)vertice.getVeriticeElement());

		if(abstractTaskElement.getDataInputList() != null && !abstractTaskElement.getDataInputList().isEmpty()){
			str += " "+"\tO processamento da tarefa demanda o recebimento do(s) seguinte(s) elemento(s):"+" ";
			for (DataObject dataInput : abstractTaskElement.getDataInputList()) {
				str += dataInput.getName()+", ";
			}
			str = str.substring(0, str.length()-2);
			str += ".";
		}

		if(abstractTaskElement.getDataOutputList() != null && !abstractTaskElement.getDataOutputList().isEmpty()){
			str += " "+"O processamento da tarefa demanda a produção do(s) seguinte(s) elemento(s):"+" ";
			for (DataObject dataOutput : abstractTaskElement.getDataOutputList()) {
				str += dataOutput.getName()+", ";
			}
			str = str.substring(0, str.length()-2);
			str += ".";
		}

		List<MessageFlowTag> allMessageTargetFlowToVertice = processDiagram.getAllMessageFlowToVertice(vertice.getVeriticeElement(), MyConstants.TARGET_VERTICE);
		if(!allMessageTargetFlowToVertice.isEmpty()){
			str += " O processamento "+vertice.getVeriticeElement().getName()+" recebe uma comunicação de";
			for (MessageFlowTag messageFlowTag : allMessageTargetFlowToVertice) {
				str += " "+messageFlowTag.getSourceRef().getName()+", ";
			}
			str = str.substring(0, str.length()-2);
			str += ".";
		}

		List<MessageFlowTag> allMessageSourceMessageFlow = processDiagram.getAllMessageFlowToVertice(vertice.getVeriticeElement(), MyConstants.SOURCE_VERTICE);
		if(!allMessageSourceMessageFlow.isEmpty()){
			str += " O processamento "+vertice.getVeriticeElement().getName()+" comunica-se com o";
			for (MessageFlowTag sourceMessageFlow : allMessageSourceMessageFlow) {
				str += " "+sourceMessageFlow.getTargetRef().getName()+", ";
			}
			str = str.substring(0, str.length()-2);
			str += ".";
		}
		return organizeBegingEndFragment(str);
	}

	public String organizeBegingEndFragment(String str){
		String newStr = str.replace("  ", " ");

		if(!newStr.endsWith(".")){
			newStr += ".";
		}
		int index = 0;
		for (int i=0; i<str.length(); i++) {
			if(Character.isLetterOrDigit(str.charAt(i))){
				index = i+1;
				break;
			}
		}


		newStr = newStr.substring(0, index).toUpperCase() + newStr.substring(index);

		return newStr;
	}

	private String endEventFragment(Vertice vertice, ProcessDiagram processDiagram){
		String str = "";
		str += " "+"ocorre o evento final"+" "+ vertice.getVeriticeElement().getName();

		List<MessageFlowTag> allMessageSourceFlowToVertice = processDiagram.getAllMessageFlowToVertice(vertice.getVeriticeElement(), MyConstants.SOURCE_VERTICE);
		if(!allMessageSourceFlowToVertice.isEmpty()){
			for (MessageFlowTag messageFlowTag : allMessageSourceFlowToVertice) {
				str += " "+"com o envio da mensagem"+" ";
				if(messageFlowTag.getMessagem() != null && !messageFlowTag.getMessagem().equals("")){
					str += messageFlowTag.getMessagem()+" ";
				}
				str += "para "+messageFlowTag.getTargetRef().getName()+", "; 
			}
			str = str.substring(0, str.length()-2);
			str += ".";
		}
		return organizeBegingEndFragment(str);
	}

	private String intermediateEventFragment (Vertice vertice, ProcessDiagram processDiagram){
		String str = "";

		return organizeBegingEndFragment(str);
	}

	private String startEventFragment (Vertice vertice, ProcessDiagram processDiagram){
		String str = "";
		str += "O processo";//DocumentUtils.getInstance().getI18NLabel(DocumentUtils.START_EVENT_PART_1);

		ParticipantTag NamePool =processDiagram.getOwnerProcess(vertice.getVeriticeElement());
		if(NamePool != null){
			str += " "+NamePool.getName();
		}
		str += " "+"inicia com o evento ";//DocumentUtils.getInstance().getI18NLabel(DocumentUtils.START_EVENT_PART_2);

		if(vertice.getVeriticeElement().getName() != null && !vertice.getVeriticeElement().equals("")){
			str += vertice.getVeriticeElement().getName();
		}

		List<MessageFlowTag> allMessageFlowToVertice = processDiagram.getAllMessageFlowToVertice(vertice.getVeriticeElement(), MyConstants.TARGET_VERTICE);
		if(!allMessageFlowToVertice.isEmpty()){
			str += ", gerado pelo(a) "+allMessageFlowToVertice.get(0).getSourceRef().getName()+".";
		}

		if(((AbstractEventElement)vertice.getVeriticeElement()).getEventElementType() != null &&
				((AbstractEventElement)vertice.getVeriticeElement()).getEventElementType().equals(EnumEventElementType.MESSAGE)){
			str += " Após o recebimento, será realizado(a)";
		}


		return organizeBegingEndFragment(str);
	}

	private String getProcessName ( ProcessDiagram processDiagram){
		String str = "";

		for (ParticipantTag participantTag : processDiagram.getParticipantList()) {
			if( participantTag.getProcessRef() != null){
				for (AbstractBaseElement element : participantTag.getProcessRef().getFlowElementList()) {
					if (element instanceof StartEvent) {
						ParticipantTag NamePool =processDiagram.getOwnerProcess(element);
						if(NamePool != null){
							str += NamePool.getName();
						}
						break;
					}
				}
			}
		}


		return str;
	}

	//	public void genereateWordDocument(String documentPath,List<List<Vertice>>  letterComponetes){
	//		try{
	//
	//			WordDocumentHelper instance = WordDocumentHelper.getInstance(documentPath);
	//
	//			String processImagePath = documentPath.substring(0, documentPath.lastIndexOf(".")+1)+"png";
	//
	//			instance.insertImage(processImagePath);
	//			instance.insertParagraph("Maaaiii!!!!");
	//			instance.finishDocument();
	//
	////			System.out.println("\n--------------------------------Camhinhos gerados--------------------------------------------------------");
	////			int qtdGateway = 0;
	////			for (List<Vertice> list : letterComponetes) {
	////				for (Vertice vertice : list) {
	////					String str = "";
	////					if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
	////						qtdGateway ++;
	////					}else if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
	////						qtdGateway --;
	////					}
	////					for (int j = 0; j < qtdGateway; j++) {
	////						str+="\t";
	////					}
	////					strReturn += str+vertice.getVeriticeElement().getClass().getName().substring(vertice.getVeriticeElement().getClass().getName().lastIndexOf(".")+1, vertice.getVeriticeElement().getClass().getName().length())
	////							+":"+vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+")"+"\n";
	////				}
	////			}
	//		}catch (Exception e){
	//			e.printStackTrace();
	//
	//		}
	//	}

}
