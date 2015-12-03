package poli.mestrado.document.management;

import java.io.IOException;
import java.util.List;

import poli.mestrado.parser.bpmn2use.graph.Graph;
import poli.mestrado.parser.bpmn2use.graph.GraphHelper;
import poli.mestrado.parser.bpmn2use.graph.Vertice;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.event.StartEvent;

import com.bpmn.letter.word.document.DocumentUtils;
import com.bpmn.letter.word.document.IDocumentMaker;
import com.bpmn.letter.word.document.WordDocumentHelper;


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

		documentMaker.insertCapa("CAPA");


		String processImagePath = documentPath.substring(0, documentPath.lastIndexOf(".")+1)+"png";
		documentMaker.insertImage(processImagePath);

		System.out.println("\n--------------------------------Camhinhos gerados--------------------------------------------------------");
		int qtdGateway = 0;
		for (List<Vertice> list : letterComponetes) {
			for (Vertice vertice : list) {
				String str = "";
//				if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_FORK){
//					qtdGateway ++;
//				}else if(GraphHelper.getKindGateway(vertice) == MyConstants.GATEWAY_JOIN){
//					qtdGateway --;
//				}
//				for (int j = 0; j < qtdGateway; j++) {
//					str+="\t";
//				}
				String str1 = bpmnElemt2Letter(vertice, processDiagram);
						
//						str+vertice.getVeriticeElement().getClass().getName().substring(vertice.getVeriticeElement().getClass().getName().lastIndexOf(".")+1, vertice.getVeriticeElement().getClass().getName().length())
//						+":"+vertice.getVeriticeElement().getName()+"("+vertice.getVeriticeElement().getId()+")"+"\n";
				System.out.println(str1);
				documentMaker.insertParagraph(str1);
			}
		}
		
		documentMaker.finishDocument();

	}
	
	private String bpmnElemt2Letter(Vertice vertice, ProcessDiagram processDiagram){
		String str = "";
		if(vertice.getVeriticeElement() instanceof StartEvent){
			str += DocumentUtils.getInstance().getI18NLabel(DocumentUtils.START_EVENT_PART_1);
					
			ParticipantTag NamePool =	processDiagram.getOwnerProcess(vertice.getVeriticeElement());
			if(NamePool != null){
				str += " "+NamePool.getName();
			}
			str += DocumentUtils.getInstance().getI18NLabel(DocumentUtils.START_EVENT_PART_2);
			if(vertice.getVeriticeElement().getName() != null && !vertice.getVeriticeElement().equals("")){
				str += vertice.getVeriticeElement().getName()+" e ";
			}
					
//					+ processDiagram.getOwnerProcess(vertice.getVeriticeElement()).getName()+" inicia com a solicitação "[, gerado pelo(a) «NamePool-SourceMessageFlow»].  Após recebimento";
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
