package poli.mestrado.document.management;

import java.io.IOException;
import java.util.List;

import poli.mestrado.parser.bpmn2use.graph.Graph;
import poli.mestrado.parser.bpmn2use.graph.GraphHelper;
import poli.mestrado.parser.bpmn2use.graph.Vertice;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.LaneTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.MessageFlowTag;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;
import poli.mestrado.parser.bpmn2use.tag.event.AbstractEventElement;
import poli.mestrado.parser.bpmn2use.tag.event.EnumEventElementType;
import poli.mestrado.parser.bpmn2use.tag.event.StartEvent;
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


//		String ext = documentPath.substring(documentPath.lastIndexOf(".")+1);
//
//		IDocumentMaker documentMaker = null;
//
//		if(ext.equals("doc")){
//			documentMaker = WordDocumentHelper.getInstance(documentPath);
//		}else if(ext.equals("pdf")){
//
//		}else if(ext.equals("html")){
//
//		}
//
//		documentMaker.insertCapa("CAPA");


//		String processImagePath = documentPath.substring(0, documentPath.lastIndexOf(".")+1)+"png";
//		documentMaker.insertImage(processImagePath);

		System.out.println("\n--------------------------------Camhinhos gerados--------------------------------------------------------");
		int qtdGateway = 0;
		String str = "";
		for (List<Vertice> list : letterComponetes) {
			for (Vertice vertice : list) {
//				if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
//					qtdGateway ++;
//				}else if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
//					qtdGateway --;
//				}
//				for (int j = 0; j < qtdGateway; j++) {
//					str+="\t";
//				}
				str += bpmnElemt2Letter(vertice, processDiagram);
						
//						str+vertice.getVeriticeElement().getClass().getName().substring(vertice.getVeriticeElement().getClass().getName().lastIndexOf(".")+1, vertice.getVeriticeElement().getClass().getName().length())
//						+":"+vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+")"+"\n";
//				documentMaker.insertParagraph(str1);
			}
		}
		System.out.println(str);
		
//		documentMaker.finishDocument();

	}
	
	private String bpmnElemt2Letter(Vertice vertice, ProcessDiagram processDiagram){
		String str = "";
		if(vertice.getVeriticeElement() instanceof StartEvent){
			str += startEventFragment(vertice, processDiagram);
		}else if(vertice.getVeriticeElement() instanceof AbstractTaskElement){
			str += taskFragment(vertice, processDiagram);
		}
		return str;
	}
	
	private String taskFragment(Vertice vertice, ProcessDiagram processDiagram){
		String str = "";
		str += " "+"a taref"+" "+vertice.getVeriticeElement().getName()+" "+"será realizada";
		LaneTag ownerLane = processDiagram.getOwnerLane(vertice.getVeriticeElement());
		if(ownerLane != null){
			str += " "+"pelo(a)"+" "+ownerLane.getName()+".";
		}
		AbstractTaskElement abstractTaskElement = ((AbstractTaskElement)vertice.getVeriticeElement());
		
		if(abstractTaskElement.getDataInputList() != null && !abstractTaskElement.getDataInputList().isEmpty()){
			str += " "+"O processamento da tarefa demanda o recebimento do(s) seguinte(s) elemento(s):"+" ";
			for (DataObject dataInput : abstractTaskElement.getDataInputList()) {
				str += dataInput.getName()+", ";
			}
			str = str.substring(0, str.length()-1);
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
		
		return str;
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
		
		if(((AbstractEventElement)vertice.getVeriticeElement()).getEventElementType().equals(EnumEventElementType.MESSAGE)){
			str += " Após o recebimento, será realizado(a) ";
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
