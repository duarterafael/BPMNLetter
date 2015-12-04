package poli.mestrado.parser.bpmn2use;

import java.util.ArrayList;
import java.util.List;

import poli.mestrado.parser.bpmn2use.graph.Edge;
import poli.mestrado.parser.bpmn2use.graph.GraphHelper;
import poli.mestrado.parser.bpmn2use.graph.Vertice;
import poli.mestrado.parser.bpmn2use.tag.dataObject.DataObject;
import poli.mestrado.parser.bpmn2use.tag.event.EndEvent;
import poli.mestrado.parser.bpmn2use.tag.gateway.AbstractGatewayElement;
import poli.mestrado.parser.bpmn2use.tag.gateway.ExclusiveGateway;
import poli.mestrado.parser.bpmn2use.tag.task.AbstractTaskElement;
import poli.mestrado.parser.util.MyConstants;
import poli.mestrado.parser.util.Utility;

public class GenerateUseShellCmdScriptsManager {
	private static GenerateUseShellCmdScriptsManager instance = null;

	private GenerateUseShellCmdScriptsManager(){
	}

	public static GenerateUseShellCmdScriptsManager getInstance(){
		if(instance == null){
			instance = new GenerateUseShellCmdScriptsManager();
		}
		return instance;
	}

	public void generateAsslScriptsManiAndMethod( List<List<Vertice>> listaCaminhos){
		UseAsslFileHelper.deleteFiles();
		UseAsslFileHelper.getInstance().resetPahtCount();
		for (List<Vertice> caminho : listaCaminhos) {
			task2AsslFunction(caminho);
			generateMain(caminho);
			UseAsslFileHelper.getInstance().closeFile();
			UseAsslFileHelper.getInstance().incremetPathCount();
		}
	}
	
	public void generateAsslScriptOnlyMain(List<List<Vertice>> listaCaminhos){
		UseAsslFileHelper.deleteFiles();
		UseAsslFileHelper.getInstance().resetPahtCount();
		for (List<Vertice> caminho : listaCaminhos) {
			UseAsslFileHelper.getInstance().writeLine(generateOnlyMainPerPath(caminho));
			UseAsslFileHelper.getInstance().closeFile();
			UseAsslFileHelper.getInstance().incremetPathCount();
		}
	}
	private void createdataObjectList(List<DataObject> currentTaskDataObjectList, List<DataObject> listDataObjets){
		
		for (DataObject do1 : currentTaskDataObjectList) {
			boolean existe = false;
			for (DataObject do2 : listDataObjets) {
				if(do1 != null && do2 != null && do1.getName() != null && 
				   do1.getItemSubjectRef() != null &&
				   do2.getName() != null && 
				   do2.getItemSubjectRef() != null &&
				   do1.getName().equals(do2.getName()) && do1.getItemSubjectRef().equals(do2.getItemSubjectRef())){
					existe = true;
					break;
				}
			}
			if(!existe){
				listDataObjets.add(do1);
				existe = false;
			}
		}
	}
	
	private String generateOnlyMainPerPath(List<Vertice> caminho){
		String auxStr = "";
		String args = "";
		int qtdTab = 0;
		List<DataObject> listDataObjets =  new ArrayList<DataObject>();
		
		for(int i = 0; i<caminho.size(); i++){
			Vertice vertice = caminho.get(i);
			
			if(vertice.getVeriticeElement() instanceof AbstractTaskElement){
				AbstractTaskElement currentTask = ((AbstractTaskElement) vertice.getVeriticeElement());
				createdataObjectList(currentTask.getDataInputList(), listDataObjets);
				createdataObjectList(currentTask.getDataOutputList(), listDataObjets);
				
				

				auxStr += getStrTab(qtdTab)+currentTask.getAsslComan();
			}else if(vertice.getVeriticeElement() instanceof ExclusiveGateway && ((AbstractGatewayElement) vertice.getVeriticeElement()).getKindGateway() == MyConstants.GATEWAY_FORK){
				List<Edge> arestas = GraphHelper.getInstance().getAllEdgeLinkedToVertice(vertice, MyConstants.SOURCE_VERTICE);
				for (Edge edge : arestas) {
					if(edge.getTargetVertice().equals(caminho.get(i+1))){
						auxStr += getStrTab(qtdTab)+edge.getAsslConditionComand();
						auxStr += getStrTab(qtdTab)+"begin";
						qtdTab++;
					}
				}
			}else if(vertice.getVeriticeElement() instanceof ExclusiveGateway && ((AbstractGatewayElement) vertice.getVeriticeElement()).getKindGateway() == MyConstants.GATEWAY_JOIN){
				qtdTab --;
				auxStr += getStrTab(qtdTab)+"end;";
			}else if(vertice.getVeriticeElement() instanceof EndEvent){
				auxStr += getStrTab(qtdTab)+"end;";
			}
		}
		if(!listDataObjets.isEmpty()){
			args = "var ";
			for (DataObject dataObject : listDataObjets) {
				args += dataObject.toString()+", ";
			}
			
			args = args.substring(0, args.length()-2)+";";
		}
		return "procedure Main()"+args+"\nbegin\n"+auxStr;
	}
	

	private void task2AsslFunction(List<Vertice> caminho){
		for (Vertice vertice : caminho) {
			if(vertice.getVeriticeElement() instanceof AbstractTaskElement){
				UseAsslFileHelper.getInstance().writeLine(vertice.getVeriticeElement().toString());
			}
		}
	}
	private void generateMain(List<Vertice> caminho){
		String auxStr = "";
		String args = "";
		int qtdTab = 0;
		List<DataObject> listDataObjets =  new ArrayList<DataObject>();
		
		for(int i = 0; i<caminho.size(); i++){
			Vertice vertice = caminho.get(i);
			
			if(vertice.getVeriticeElement() instanceof AbstractTaskElement){
				AbstractTaskElement currentTask = ((AbstractTaskElement) vertice.getVeriticeElement());
				createdataObjectList(currentTask.getDataInputList(), listDataObjets);
				createdataObjectList(currentTask.getDataOutputList(), listDataObjets);
				auxStr += getStrTab(qtdTab)+currentTask.getAsslCall();
			}else if(vertice.getVeriticeElement() instanceof ExclusiveGateway && ((AbstractGatewayElement) vertice.getVeriticeElement()).getKindGateway() == MyConstants.GATEWAY_FORK){
				List<Edge> arestas = GraphHelper.getInstance().getAllEdgeLinkedToVertice(vertice, MyConstants.SOURCE_VERTICE);
				for (Edge edge : arestas) {
					if(edge.getTargetVertice().equals(caminho.get(i+1))){
						auxStr += getStrTab(qtdTab)+edge.getAsslConditionComand();
						auxStr += getStrTab(qtdTab)+"begin";
						qtdTab++;
					}
				}
			}else if(vertice.getVeriticeElement() instanceof ExclusiveGateway && ((AbstractGatewayElement) vertice.getVeriticeElement()).getKindGateway() == MyConstants.GATEWAY_JOIN){
				qtdTab --;
				auxStr += getStrTab(qtdTab)+"end;";
			}else if(vertice.getVeriticeElement() instanceof EndEvent){
				auxStr += getStrTab(qtdTab)+"end;";
			}
		}
		if(!listDataObjets.isEmpty()){
			args = "var ";
			for (DataObject dataObject : listDataObjets) {
				args += dataObject.toString()+", ";
			}
			
			args = args.substring(0, args.length()-2)+";";
		}
		UseAsslFileHelper.getInstance().writeLine( "procedure Main()"+args+"\nbegin\n"+auxStr);
	}

	private String getStrTab(int qtdTab){
		String strTab = "\n";
		for(int i = 0; i < qtdTab; i++ ){
			strTab += "\t";
		}
		return strTab;
	}

}
