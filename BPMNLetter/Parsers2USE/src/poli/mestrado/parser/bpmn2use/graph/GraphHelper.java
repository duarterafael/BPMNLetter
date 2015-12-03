package poli.mestrado.parser.bpmn2use.graph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import poli.mestrado.parser.bpmn2use.graph.Edge.EdgeState;
import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.ProcessDiagram;
import poli.mestrado.parser.bpmn2use.tag.ProcessTag;
import poli.mestrado.parser.bpmn2use.tag.Swimlanes.ParticipantTag;
import poli.mestrado.parser.bpmn2use.tag.connectioElement.SequenceFlowTag;
import poli.mestrado.parser.bpmn2use.tag.event.StartEvent;
import poli.mestrado.parser.bpmn2use.tag.gateway.AbstractGatewayElement;
import poli.mestrado.parser.bpmn2use.tag.gateway.ExclusiveGateway;
import poli.mestrado.parser.bpmn2use.tag.gateway.ParallelGateway;
import poli.mestrado.parser.util.MyConstants;


/**
 * @author Rafael
 *
 */
public class GraphHelper {

	private static GraphHelper instance = null;
	private static  Graph graph;

	public static GraphHelper getInstance(){
		if(instance == null){
			instance = new GraphHelper();
		}
		return instance;
	}

	private GraphHelper(){

	}
	

	public Graph generateGraph(ProcessDiagram processDiagram){
		if(processDiagram.getSingleProcess() != null){
			return generateGraphSigleProcess(processDiagram.getSingleProcess());
		}
		
		List<Vertice> verticesList = new LinkedList<Vertice>();
		List<Edge>  edgeList =  new LinkedList<Edge>();


		for (ParticipantTag participantTag : processDiagram.getParticipantList()) {
			if(participantTag.getProcessRef() != null){
				for (AbstractBaseElement bpmnElement : participantTag.getProcessRef().getFlowElementList()) {
					verticesList.add(new Vertice(bpmnElement));
				}
				
				for (SequenceFlowTag sequenceFlowTag : participantTag.getProcessRef().getSequenceFlowList()) {
					edgeList.add(new Edge(getVeticeById(sequenceFlowTag.getSourceRef().getId(), verticesList), getVeticeById(sequenceFlowTag.getTargetRef().getId(), verticesList), sequenceFlowTag.getConditionExpression()));
				}
			}
		}
		
		
		graph = new Graph(verticesList, edgeList);
		return graph;
	}
	
	private Graph generateGraphSigleProcess(ProcessTag process){
		List<Vertice> verticesList = new LinkedList<Vertice>();
		List<Edge>  edgeList =  new LinkedList<Edge>();



		for (AbstractBaseElement bpmnElement : process.getFlowElementList()) {
			verticesList.add(new Vertice(bpmnElement));
		}

		for (SequenceFlowTag sequenceFlowTag : process.getSequenceFlowList()) {
			edgeList.add(new Edge(getVeticeById(sequenceFlowTag.getSourceRef().getId(), verticesList), getVeticeById(sequenceFlowTag.getTargetRef().getId(), verticesList), sequenceFlowTag.getConditionExpression()));
		}

		graph = new Graph(verticesList, edgeList);
		return graph;
	}



	private  Vertice getVeticeById(String id, List<Vertice> verticesList){
		for (Vertice vertice : verticesList) {
			if(vertice.getVeriticeElement().getId().equalsIgnoreCase(id)){
				return vertice;
			}
		}
		return null;
	}
	
	public  List<List<Vertice>> generatePaths(){
		List<List<Vertice>> listaCaminhos = new LinkedList<List<Vertice>> ();
		//Add um caminho vazio, que será add vertices ou será duplicado caso sejá um gateway fork
		listaCaminhos.add(new LinkedList<Vertice>());

		//é o conjunto de vertices que são alcaçados mas que ainda possuem arestas a serem exploradas
		List<Vertice> alcancados = new LinkedList<Vertice>();
		//é o conjunto de vertices que possuem todas as arestas exploradas
		List<Vertice> explorados = new LinkedList<Vertice>();
		//r = vertice raiz(para o BPMN é o vertice de start_event)
		//v = vertice de origem
		//w = vertice de destino
		Vertice r = null, v = null, w = null;

		//Lista de caminhos auxiliar para ajudar em caso de duplicação de caminhos
		List<List<Vertice>> listaFragmentosCaminhos = new LinkedList<List<Vertice>> ();

		//conjunto de vertices encadeados por arestas
		List<Vertice> caminho = new LinkedList<Vertice>();


		//Escolhendo o no raiz, no caso para o startEvent
		for (Vertice vertice : graph.getVertices()) {
			if(vertice.getVeriticeElement() instanceof StartEvent){
				r = vertice;
				break;
			}
		}

		r.changeState();
		alcancados.add(r);



		while(!alcancados.isEmpty()){
			v = alcancados.get(0);

			if(getKindGateway(v) == MyConstants.GATEWAY_FORK){
				w = v;
				listaFragmentosCaminhos.clear(); 

				for (List<Vertice> caminhoAux : listaCaminhos) {
					Vertice ultimoVertice = caminhoAux.get(caminhoAux.size()-1);
					//Lista de arestas que possuem o ultimo vertice de cada caminho como vertice de origem
					List<Edge> listaArestasUltimoVertice = getAllEdgeLinkedToVertice(ultimoVertice, MyConstants.SOURCE_VERTICE);
					if(listaArestasUltimoVertice != null){
						for (Edge aresta : listaArestasUltimoVertice) {
							if(aresta.getTargetVertice().equals(v)){
								caminhoAux.add(v);
							}
						}
					}

				}
				//Todos as arestas que possuiem o gateway fork como vertice de origem
				List<Edge> listaVerticesGatewayFork = getAllEdgeLinkedToVertice(w, MyConstants.SOURCE_VERTICE);
				//Fraquimento auxiliar de camnho.Representa um caminho que parte de um gateway
				List<Edge> fragmentoCaminho =  null;
				//posição que um gateway aninhado será incerido na lista de alcaçados. 
				int j = 0;
				//Percorrei todos os vertices que possuem o gateway como vertice de origem
				for (Edge edge : listaVerticesGatewayFork) {

					edge.changeState();
					List<Vertice> listaVerticeAux = new LinkedList<Vertice>();
					w = edge.getTargetVertice();

					//	A partir do primeira areta após um gateway do tipo fork pega todo os vertices 
					//e add no fragmento de caminho até um gateway join (que indica o fim da bifurcação) 
					//ou outro gateway fork(que indica gateway são aninhados) 
					do{
						//Como não é um gateway pode trasacionar dois estados diretos, de inexplorado para explorado
						w.changeState();
						w.changeState();

						//Se for gateway  significa que é aninhado e deve quebra o laço do mais externo para comeaçar novamente outro laço do mais interno
						if(w.getVeriticeElement() instanceof AbstractGatewayElement){
							break;
						}else{
							//Se não é um gateway fork adciona no framento de caminho							
							fragmentoCaminho = getAllEdgeLinkedToVertice(w, MyConstants.SOURCE_VERTICE);
							if(fragmentoCaminho.isEmpty())
								break;
							Edge arestaInexplorada = existeArestaNãoExplorada(fragmentoCaminho);
							listaVerticeAux.add(w);
							if(arestaInexplorada != null){
								arestaInexplorada.changeState();
							}
							alcancados.remove(w);
							explorados.add(w);
							//
							w = arestaInexplorada.getTargetVertice();

						}
					}while(getKindGateway(w) != MyConstants.GATEWAY_JOIN);
					//Aqui add o gateway fork ou join na lista de alcançados
					if(!alcancados.contains(w)){
						alcancados.add(j, w);
						j++;
					}

					//					------Sinceramente não sei pq coloquei isso
					if(!listaVerticeAux.contains(w)){
						listaVerticeAux.add(w);
					}
					
					if(listaVerticeAux.size()>1){
						listaVerticeAux.remove(listaVerticeAux.size()-1);
					}
					//					----------Ate aqui, ver possivilidade de remoção desse codigo

					//Add o framente de caminho a lista de framentos de caminhos
					listaFragmentosCaminhos.add(listaVerticeAux);

				}

				//Como gera exceção ao alterar um coleção ao mesmo tempo que intera na mesma é feito um cópia para auxiliar a construção da lista de caminhos				
				List<List<Vertice>> listaCaminhosAux = new LinkedList<List<Vertice>>(); 

				//Cada tipo de gateway possui um comportamento distinto para sua lista de fraqmentos de caminhos, sendo esse if esle representa a intepretação de comportamento.

				//O gateway Exclusivo escolhe um caminho que atenda a uma das condições descritas nas arestas que partem do gateway fork
				//Sendo assim casa fragemto de caminho sera anexado a uma copida dos caminhos que porssuem ultimo vertice o gateway fork em questão
				if(v.getVeriticeElement() instanceof ExclusiveGateway){

					//Esse primeiro for faz um cópia de todos os caminhos, aqueles que tem o ultimo vertice igual ao gateway em questão são copiados na quantidade de fragmentos de caminhos existentes
					for (List<Vertice> caminhoAntigo : listaCaminhos) {
						if(caminhoAntigo.get(caminhoAntigo.size()-1).equals(v)){
							for(int i = 0;  i<listaFragmentosCaminhos.size(); i++){
								listaCaminhosAux.add(new LinkedList<Vertice>(caminhoAntigo));
							}
						}else{
							listaCaminhosAux.add(new LinkedList<Vertice>(caminhoAntigo));
						}
					}
					int i = 0;
					//Add o fragmento de caminho no caminho certo
					for (List<Vertice> fragmentoCaminhoAux : listaCaminhosAux) {
						if(fragmentoCaminhoAux.get(fragmentoCaminhoAux.size()-1).equals(v)){
							if(i >= listaFragmentosCaminhos.size()){
								i= 0;
							}
							fragmentoCaminhoAux.addAll(listaFragmentosCaminhos.get(i));
							i++;
						}
					}

				}else if(v.getVeriticeElement() instanceof ParallelGateway){
					// O gateway paralelo não gera caminhos divergentes o primeiro vertice de cada caminho e acessado para cada fragmente de caminho depois o segundo e assim sucessivamente
					List<Vertice> caminhoParalelo = new ArrayList<Vertice>();
					int tam_maior_lista = -1;
					for (List<Vertice> caminhoAtual : listaFragmentosCaminhos) {
						if(caminhoAtual.size() > tam_maior_lista){
							tam_maior_lista = caminhoAtual.size();

						}
					}

					int i = 0;
					int K = 0;
					do{

						if(i<listaFragmentosCaminhos.get(K).size()){
							//							System.out.println(listaCaminhosAux.get(K).get(i));
							caminhoParalelo.add(listaFragmentosCaminhos.get(K).get(i));
						}

						if(K == listaFragmentosCaminhos.size()-1){
							i++;
							K = 0;
							continue;
						}else{
							K++;
						}
					}while(i<tam_maior_lista && K <listaFragmentosCaminhos.size());

					//Esse primeiro for faz um cópia de todos os caminhos, aqueles que tem o ultimo vertice igual ao gateway em questão são copiados na quantidade de fragmentos de caminhos existentes
					for (List<Vertice> caminhoAntigo : listaCaminhos) {
						listaCaminhosAux.add(new LinkedList<Vertice>(caminhoAntigo));
					}

					//Add o fragmento de caminho no caminho certo
					for (List<Vertice> fragmentoCaminhoAux : listaCaminhosAux) {
						if(fragmentoCaminhoAux.get(fragmentoCaminhoAux.size()-1).equals(v)){
							fragmentoCaminhoAux.addAll(caminhoParalelo);
						}
					}


				}
				//Passa a referencia da lista de caminhos mais atual para variavel que será retornada
				listaCaminhos = listaCaminhosAux;

				alcancados.remove(v);
				explorados.add(v);



			}else{

				//Obtem todas as arestas que possuem o vertice em gestão com vertice de origem
				List<Edge> arestas = getAllEdgeLinkedToVertice(v, MyConstants.SOURCE_VERTICE);
				//Obterm um aresta inexplorada ou null caso não exista alguma
				Edge arestaInexplorada = existeArestaNãoExplorada(arestas);
				if(arestaInexplorada != null){
					//muda o estado da aresta da explorada
					arestaInexplorada.changeState();
					//obterm o vertice destino
					w = arestaInexplorada.getTargetVertice();
					//Caso o vertice n foi alcaçado ainda, muda seu estado e add a lista de alcançados
					if(!verticePertenceAosAlcancado(w, alcancados)){
						w.changeState();
						if(v.getVeriticeElement() instanceof ParallelGateway && getKindGateway(v) == MyConstants.GATEWAY_JOIN ){
							alcancados.add(1,w);
						}else{
							alcancados.add(w);
						}
							
					}
				}else{//Caso todas as arestas forma alcaçadas
					//muda o estado do vertice pela segunda vez, no caso para explorado
					v.changeState();

					alcancados.remove(v);
					explorados.add(v);

					//O primeiro vertice é adcionado a primeira e unico caminho
					if(listaCaminhos.get(0).isEmpty()){
						listaCaminhos.get(0).add(v);
					}else{//Se o vertice não esta em uma bifurcação pode ser adcionado a todo caminho onde o ultimo vertice possui uma aresta que liga-se a V
						for (List<Vertice> caminhoAux : listaCaminhos) {
							Vertice ultimoVertice = caminhoAux.get(caminhoAux.size()-1);
							//Lista de arestas que possuem o ultimo vertice de cada caminho como vertice de origem
							List<Edge> listaArestasUltimoVertice = getAllEdgeLinkedToVertice(ultimoVertice, MyConstants.SOURCE_VERTICE);

							if(listaArestasUltimoVertice != null){
								for (Edge aresta : listaArestasUltimoVertice) {
									if(aresta.getTargetVertice().equals(v)){
										caminhoAux.add(v);
									}
								}
							}
						}
					}

				}
			}

		}

		return listaCaminhos;
	}

	
	public  List<List<Vertice>> generateLetter(){
		List<List<Vertice>> listaCaminhos = new LinkedList<List<Vertice>> ();
		//Add um caminho vazio, que será add vertices ou será duplicado caso sejá um gateway fork
		

		//é o conjunto de vertices que são alcaçados mas que ainda possuem arestas a serem exploradas
		List<Vertice> alcancados = new LinkedList<Vertice>();
		//é o conjunto de vertices que possuem todas as arestas exploradas
		List<Vertice> explorados = new LinkedList<Vertice>();
		//r = vertice raiz(para o BPMN é o vertice de start_event)
		//v = vertice de origem
		//w = vertice de destino
		Vertice r = null, v = null, w = null;

		//Lista de caminhos auxiliar para ajudar em caso de duplicação de caminhos
		List<List<Vertice>> listaFragmentosCaminhos = new LinkedList<List<Vertice>> ();


		//Escolhendo o no raiz, no caso para o startEvent
		for (Vertice vertice : graph.getVertices()) {
			if(vertice.getVeriticeElement() instanceof StartEvent){
				r = vertice;
				break;
			}
		}

		r.changeState();
		alcancados.add(r);



		while(!alcancados.isEmpty()){
			v = alcancados.get(0);

			if(getKindGateway(v) == MyConstants.GATEWAY_FORK){
				w = v;
				listaFragmentosCaminhos.clear(); 

				for (List<Vertice> caminhoAux : listaCaminhos) {
					Vertice ultimoVertice = caminhoAux.get(caminhoAux.size()-1);
					//Lista de arestas que possuem o ultimo vertice de cada caminho como vertice de origem
					List<Edge> listaArestasUltimoVertice = getAllEdgeLinkedToVertice(ultimoVertice, MyConstants.SOURCE_VERTICE);
					if(listaArestasUltimoVertice != null){
						for (Edge aresta : listaArestasUltimoVertice) {
							if(aresta.getTargetVertice().equals(v)){
								caminhoAux.add(v);
							}
						}
					}

				}
				//Todos as arestas que possuiem o gateway fork como vertice de origem
				List<Edge> listaVerticesGatewayFork = getAllEdgeLinkedToVertice(w, MyConstants.SOURCE_VERTICE);
				//Fraquimento auxiliar de camnho.Representa um caminho que parte de um gateway
				List<Edge> fragmentoCaminho =  null;
				//posição que um gateway aninhado será incerido na lista de alcaçados. 
				int j = 0;
				//Percorrei todos os vertices que possuem o gateway como vertice de origem
				for (Edge edge : listaVerticesGatewayFork) {

					edge.changeState();
					List<Vertice> listaVerticeAux = new LinkedList<Vertice>();
					w = edge.getTargetVertice();

					//	A partir do primeira areta após um gateway do tipo fork pega todo os vertices 
					//e add no fragmento de caminho até um gateway join (que indica o fim da bifurcação) 
					//ou outro gateway fork(que indica gateway são aninhados) 
					do{
						//Como não é um gateway pode trasacionar dois estados diretos, de inexplorado para explorado
						w.changeState();
						w.changeState();

						//Se for gateway  significa que é aninhado e deve quebra o laço do mais externo para comeaçar novamente outro laço do mais interno
						if(w.getVeriticeElement() instanceof AbstractGatewayElement){
							break;
						}else{
							//Se não é um gateway fork adciona no framento de caminho							
							fragmentoCaminho = getAllEdgeLinkedToVertice(w, MyConstants.SOURCE_VERTICE);
							if(fragmentoCaminho.isEmpty())
								break;
							Edge arestaInexplorada = existeArestaNãoExplorada(fragmentoCaminho);
							listaVerticeAux.add(w);
							if(arestaInexplorada != null){
								arestaInexplorada.changeState();
							}
							alcancados.remove(w);
							explorados.add(w);
							//
							w = arestaInexplorada.getTargetVertice();

						}
					}while(getKindGateway(w) != MyConstants.GATEWAY_JOIN);
					//Aqui add o gateway fork ou join na lista de alcançados
					if(!alcancados.contains(w)){
						alcancados.add(j, w);
						j++;
					}

					//					------Sinceramente não sei pq coloquei isso
					if(!listaVerticeAux.contains(w)){
						listaVerticeAux.add(w);
					}
					
					if(listaVerticeAux.size()>1){
						listaVerticeAux.remove(listaVerticeAux.size()-1);
					}
					//					----------Ate aqui, ver possivilidade de remoção desse codigo

					//Add o framente de caminho a lista de framentos de caminhos
					listaFragmentosCaminhos.add(listaVerticeAux);

				}

				//Como gera exceção ao alterar um coleção ao mesmo tempo que intera na mesma é feito um cópia para auxiliar a construção da lista de caminhos				
//				List<List<Vertice>> listaCaminhosAux = new LinkedList<List<Vertice>>(); 

				//Cada tipo de gateway possui um comportamento distinto para sua lista de fraqmentos de caminhos, sendo esse if esle representa a intepretação de comportamento.

				//O gateway Exclusivo escolhe um caminho que atenda a uma das condições descritas nas arestas que partem do gateway fork
				//Sendo assim casa fragemto de caminho sera anexado a uma copida dos caminhos que porssuem ultimo vertice o gateway fork em questão
				if(v.getVeriticeElement() instanceof ExclusiveGateway){

					listaCaminhos.addAll(listaFragmentosCaminhos);
				}else if(v.getVeriticeElement() instanceof ParallelGateway){
					// O gateway paralelo não gera caminhos divergentes o primeiro vertice de cada caminho e acessado para cada fragmente de caminho depois o segundo e assim sucessivamente
					List<Vertice> caminhoParalelo = new ArrayList<Vertice>();
					int tam_maior_lista = -1;
					for (List<Vertice> caminhoAtual : listaFragmentosCaminhos) {
						if(caminhoAtual.size() > tam_maior_lista){
							tam_maior_lista = caminhoAtual.size();

						}
					}

					int i = 0;
					int K = 0;
					do{

						if(i<listaFragmentosCaminhos.get(K).size()){
							//							System.out.println(listaCaminhosAux.get(K).get(i));
							caminhoParalelo.add(listaFragmentosCaminhos.get(K).get(i));
						}

						if(K == listaFragmentosCaminhos.size()-1){
							i++;
							K = 0;
							continue;
						}else{
							K++;
						}
					}while(i<tam_maior_lista && K <listaFragmentosCaminhos.size());


					listaCaminhos.get(listaCaminhos.size()-1).addAll(caminhoParalelo);


				}

				alcancados.remove(v);
				explorados.add(v);



			}else{

				//Obtem todas as arestas que possuem o vertice em gestão com vertice de origem
				List<Edge> arestas = getAllEdgeLinkedToVertice(v, MyConstants.SOURCE_VERTICE);
				//Obterm um aresta inexplorada ou null caso não exista alguma
				Edge arestaInexplorada = existeArestaNãoExplorada(arestas);
				if(arestaInexplorada != null){
					//muda o estado da aresta da explorada
					arestaInexplorada.changeState();
					//obterm o vertice destino
					w = arestaInexplorada.getTargetVertice();
					//Caso o vertice n foi alcaçado ainda, muda seu estado e add a lista de alcançados
					if(!verticePertenceAosAlcancado(w, alcancados)){
						w.changeState();
						if(v.getVeriticeElement() instanceof ParallelGateway && getKindGateway(v) == MyConstants.GATEWAY_JOIN ){
							alcancados.add(1,w);
						}else{
							alcancados.add(w);
						}
							
					}
				}else{//Caso todas as arestas forma alcaçadas
					//muda o estado do vertice pela segunda vez, no caso para explorado
					v.changeState();

					alcancados.remove(v);
					explorados.add(v);
					
					
					LinkedList lst = new LinkedList<Vertice>();
					lst.add(v);
					listaCaminhos.add(lst);
//					listaCaminhos.get(listaCaminhos.size()-1).add(v);

					//O primeiro vertice é adcionado a primeira e unico caminho
//					if(listaCaminhos.get(0).isEmpty()){
//						listaCaminhos.get(0).add(v);
//					}else{
//						listaCaminhos.get(listaCaminhos.size()).add(v);
//						//Se o vertice não esta em uma bifurcação pode ser adcionado a todo caminho onde o ultimo vertice possui uma aresta que liga-se a V
//						for (List<Vertice> caminhoAux : listaCaminhos) {
//							Vertice ultimoVertice = caminhoAux.get(caminhoAux.size()-1);
//							//Lista de arestas que possuem o ultimo vertice de cada caminho como vertice de origem
//							List<Edge> listaArestasUltimoVertice = getAllEdgeLinkedToVertice(ultimoVertice, Constants.SOURCE_VERTICE);
//
//							if(listaArestasUltimoVertice != null){
//								for (Edge aresta : listaArestasUltimoVertice) {
//									if(aresta.getTargetVertice().equals(v)){
//										caminhoAux.add(v);
//									}
//								}
//							}
//						}
//					}

				}
			}

		}

		return listaCaminhos;
	}
	

//	public  List<List<Vertice>> generatePaths(){
//		List<List<Vertice>> listaCaminhos = new LinkedList<List<Vertice>> ();
//		//Add um caminho vazio, que será add vertices ou será duplicado caso sejá um gateway fork
//		listaCaminhos.add(new LinkedList<Vertice>());
//
//		//é o conjunto de vertices que são alcaçados mas que ainda possuem arestas a serem exploradas
//		List<Vertice> alcancados = new LinkedList<Vertice>();
//		//é o conjunto de vertices que possuem todas as arestas exploradas
//		List<Vertice> explorados = new LinkedList<Vertice>();
//		//r = vertice raiz(para o BPMN é o vertice de start_event)
//		//v = vertice de origem
//		//w = vertice de destino
//		Vertice r = null, v = null, w = null;
//
//		//Lista de caminhos auxiliar para ajudar em caso de duplicação de caminhos
//		List<List<Vertice>> listaFragmentosCaminhos = new LinkedList<List<Vertice>> ();
//
//		//conjunto de vertices encadeados por arestas
//		List<Vertice> caminho = new LinkedList<Vertice>();
//
//
//		//Escolhendo o no raiz, no caso para o startEvent
//		for (Vertice vertice : graph.getVertices()) {
//			if(vertice.getVeriticeElement() instanceof StartEvent){
//				r = vertice;
//				break;
//			}
//		}
//
//		r.changeState();
//		alcancados.add(r);
//
//
//
//		while(!alcancados.isEmpty()){
//			v = alcancados.get(0);
//
//			if(getKindGateway(v) == Constants.GATEWAY_FORK){
//				w = v;
//				listaFragmentosCaminhos.clear(); 
//
//				for (List<Vertice> caminhoAux : listaCaminhos) {
//					Vertice ultimoVertice = caminhoAux.get(caminhoAux.size()-1);
//					//Lista de arestas que possuem o ultimo vertice de cada caminho como vertice de origem
//					List<Edge> listaArestasUltimoVertice = getAllEdgeLinkedToVertice(ultimoVertice, Constants.SOURCE_VERTICE);
//					if(listaArestasUltimoVertice != null){
//						for (Edge aresta : listaArestasUltimoVertice) {
//							if(aresta.getTargetVertice().equals(v)){
//								caminhoAux.add(v);
//							}
//						}
//					}
//
//				}
//				//Todos as arestas que possuiem o gateway fork como vertice de origem
//				List<Edge> listaVerticesGatewayFork = getAllEdgeLinkedToVertice(w, Constants.SOURCE_VERTICE);
//				//Fraquimento auxiliar de camnho.Representa um caminho que parte de um gateway
//				List<Edge> fragmentoCaminho =  null;
//				//posição que um gateway aninhado será incerido na lista de alcaçados. 
//				int j = 0;
//				//Percorrei todos os vertices que possuem o gateway como vertice de origem
//				for (Edge edge : listaVerticesGatewayFork) {
//
//					edge.changeState();
//					List<Vertice> listaVerticeAux = new LinkedList<Vertice>();
//					w = edge.getTargetVertice();
//
//					//	A partir do primeira areta após um gateway do tipo fork pega todo os vertices 
//					//e add no fragmento de caminho até um gateway join (que indica o fim da bifurcação) 
//					//ou outro gateway fork(que indica gateway são aninhados) 
//					do{
//						//Como não é um gateway pode trasacionar dois estados diretos, de inexplorado para explorado
//						w.changeState();
//						w.changeState();
//
//						//Se for gateway  significa que é aninhado e deve quebra o laço do mais externo para comeaçar novamente outro laço do mais interno
//						if(w.getVeriticeElement() instanceof AbstractGatewayElement){
//							break;
//						}else{
//							//Se não é um gateway fork adciona no framento de caminho							
//							fragmentoCaminho = getAllEdgeLinkedToVertice(w, Constants.SOURCE_VERTICE);
//							if(fragmentoCaminho.isEmpty())
//								break;
//							Edge arestaInexplorada = existeArestaNãoExplorada(fragmentoCaminho);
//							listaVerticeAux.add(w);
//							if(arestaInexplorada != null){
//								arestaInexplorada.changeState();
//							}
//							alcancados.remove(w);
//							explorados.add(w);
//							//
//							w = arestaInexplorada.getTargetVertice();
//
//						}
//					}while(getKindGateway(w) != Constants.GATEWAY_JOIN);
//					//Aqui add o gateway fork ou join na lista de alcançados
//					if(!alcancados.contains(w)){
//						alcancados.add(j, w);
//						j++;
//					}
//
//					//					------Sinceramente não sei pq coloquei isso
//					if(!listaVerticeAux.contains(w)){
//						listaVerticeAux.add(w);
//					}
//
//					listaVerticeAux.remove(listaVerticeAux.size()-1);
//					//					----------Ate aqui, ver possivilidade de remoção desse codigo
//
//					//Add o framente de caminho a lista de framentos de caminhos
//					listaFragmentosCaminhos.add(listaVerticeAux);
//
//				}
//
//				//Como gera exceção ao alterar um coleção ao mesmo tempo que intera na mesma é feito um cópia para auxiliar a construção da lista de caminhos				
//				List<List<Vertice>> listaCaminhosAux = new LinkedList<List<Vertice>>(); 
//
//				//Cada tipo de gateway possui um comportamento distinto para sua lista de fraqmentos de caminhos, sendo esse if esle representa a intepretação de comportamento.
//
//				//O gateway Exclusivo escolhe um caminho que atenda a uma das condições descritas nas arestas que partem do gateway fork
//				//Sendo assim casa fragemto de caminho sera anexado a uma copida dos caminhos que porssuem ultimo vertice o gateway fork em questão
//				if(v.getVeriticeElement() instanceof ExclusiveGateway){
//
//					//Esse primeiro for faz um cópia de todos os caminhos, aqueles que tem o ultimo vertice igual ao gateway em questão são copiados na quantidade de fragmentos de caminhos existentes
//					for (List<Vertice> caminhoAntigo : listaCaminhos) {
//						if(caminhoAntigo.get(caminhoAntigo.size()-1).equals(v)){
//							for(int i = 0;  i<listaFragmentosCaminhos.size(); i++){
//								listaCaminhosAux.add(new LinkedList<Vertice>(caminhoAntigo));
//							}
//						}else{
//							listaCaminhosAux.add(new LinkedList<Vertice>(caminhoAntigo));
//						}
//					}
//					int i = 0;
//					//Add o fragmento de caminho no caminho certo
//					for (List<Vertice> fragmentoCaminhoAux : listaCaminhosAux) {
//						if(fragmentoCaminhoAux.get(fragmentoCaminhoAux.size()-1).equals(v)){
//							if(i >= listaFragmentosCaminhos.size()){
//								i= 0;
//							}
//							fragmentoCaminhoAux.addAll(listaFragmentosCaminhos.get(i));
//							i++;
//						}
//					}
//
//				}else if(v.getVeriticeElement() instanceof ParallelGateway){
//					// O gateway paralelo não gera caminhos divergentes o primeiro vertice de cada caminho e acessado para cada fragmente de caminho depois o segundo e assim sucessivamente
//					List<Vertice> caminhoParalelo = new ArrayList<Vertice>();
//					int tam_maior_lista = -1;
//					for (List<Vertice> caminhoAtual : listaFragmentosCaminhos) {
//						if(caminhoAtual.size() > tam_maior_lista){
//							tam_maior_lista = caminhoAtual.size();
//
//						}
//					}
//
//					int i = 0;
//					int K = 0;
//					do{
//
//						if(i<listaFragmentosCaminhos.get(K).size()){
//							//							System.out.println(listaCaminhosAux.get(K).get(i));
//							caminhoParalelo.add(listaFragmentosCaminhos.get(K).get(i));
//						}
//
//						if(K == listaFragmentosCaminhos.size()-1){
//							i++;
//							K = 0;
//							continue;
//						}else{
//							K++;
//						}
//					}while(i<tam_maior_lista && K <listaFragmentosCaminhos.size());
//
//					//Esse primeiro for faz um cópia de todos os caminhos, aqueles que tem o ultimo vertice igual ao gateway em questão são copiados na quantidade de fragmentos de caminhos existentes
//					for (List<Vertice> caminhoAntigo : listaCaminhos) {
//						listaCaminhosAux.add(new LinkedList<Vertice>(caminhoAntigo));
//					}
//
//					//Add o fragmento de caminho no caminho certo
//					for (List<Vertice> fragmentoCaminhoAux : listaCaminhosAux) {
//						if(fragmentoCaminhoAux.get(fragmentoCaminhoAux.size()-1).equals(v)){
//							fragmentoCaminhoAux.addAll(caminhoParalelo);
//						}
//					}
//
//
//				}
//				//Passa a referencia da lista de caminhos mais atual para variavel que será retornada
//				listaCaminhos = listaCaminhosAux;
//
//				alcancados.remove(v);
//				explorados.add(v);
//
//
//
//			}else{
//
//				//Obtem todas as arestas que possuem o vertice em gestão com vertice de origem
//				List<Edge> arestas = getAllEdgeLinkedToVertice(v, Constants.SOURCE_VERTICE);
//				//Obterm um aresta inexplorada ou null caso não exista alguma
//				Edge arestaInexplorada = existeArestaNãoExplorada(arestas);
//				if(arestaInexplorada != null){
//					//muda o estado da aresta da explorada
//					arestaInexplorada.changeState();
//					//obterm o vertice destino
//					w = arestaInexplorada.getTargetVertice();
//					//Caso o vertice n foi alcaçado ainda, muda seu estado e add a lista de alcançados
//					if(!verticePertenceAosAlcancado(w, alcancados)){
//						w.changeState();
//						if(v.getVeriticeElement() instanceof ParallelGateway && getKindGateway(v) == Constants.GATEWAY_JOIN ){
//							alcancados.add(1,w);
//						}else{
//							alcancados.add(w);
//						}
//							
//					}
//				}else{//Caso todas as arestas forma alcaçadas
//					//muda o estado do vertice pela segunda vez, no caso para explorado
//					v.changeState();
//
//					alcancados.remove(v);
//					explorados.add(v);
//
//					//O primeiro vertice é adcionado a primeira e unico caminho
//					if(listaCaminhos.get(0).isEmpty()){
//						listaCaminhos.get(0).add(v);
//					}else{//Se o vertice não esta em uma bifurcação pode ser adcionado a todo caminho onde o ultimo vertice possui uma aresta que liga-se a V
//						for (List<Vertice> caminhoAux : listaCaminhos) {
//							Vertice ultimoVertice = caminhoAux.get(caminhoAux.size()-1);
//							//Lista de arestas que possuem o ultimo vertice de cada caminho como vertice de origem
//							List<Edge> listaArestasUltimoVertice = getAllEdgeLinkedToVertice(ultimoVertice, Constants.SOURCE_VERTICE);
//
//							if(listaArestasUltimoVertice != null){
//								for (Edge aresta : listaArestasUltimoVertice) {
//									if(aresta.getTargetVertice().equals(v)){
//										caminhoAux.add(v);
//									}
//								}
//							}
//						}
//					}
//
//				}
//			}
//
//		}
//
//		return listaCaminhos;
//	}

	/*
	 * original
	 */
	//	public  List<List<Edge>> generatePaths(){
	//		List<Vertice> alcancados = new LinkedList<Vertice>();
	//		List<Vertice> explorados = new LinkedList<Vertice>();
	//		Vertice r = null, v, w = null;
	//		
	//		//Escolhendo o no raiz, no caso para o startEvent
	//		for (Vertice vertice : graph.getVertices()) {
	//			if(vertice.getVeriticeElement() instanceof StartEvent){
	//				r = vertice;
	//				break;
	//			}
	//		}
	//		
	//		r.changeState();
	//		alcancados.add(r);
	//
	//		while(!alcancados.isEmpty()){
	//			v = alcancados.get(0);
	//			
	//			List<Edge> edges = getAllEdgeLinkedToVertice(v, 0);
	//			Edge edgeUnexplored = existeArestaNãoExplorada(edges);
	//			
	//			if(edgeUnexplored != null){
	//				edgeUnexplored.changeState();
	//				w = edgeUnexplored.getTargetVertice();
	//				if(!verticePertenceAosAlcancado(w, alcancados)){
	//					w.changeState();
	//					alcancados.add(w);
	//				}
	//			}else{
	//				v.changeState();
	//				alcancados.remove(v);
	//				explorados.add(v);
	//			}
	//			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	//			System.out.println(listVerticeList(alcancados, "ALCANÇADOS"));
	//			System.out.println(listVerticeList(explorados, "EXPLORADOS"));
	//		}
	//		
	//		return null;
	//	}

	private String listVerticeList(List<Vertice> verticeList, String strPara){
		String str = strPara+"= {";
		for (Vertice vertice : verticeList) {
			str += vertice.toString()+", ";
		}
		if(str.endsWith(", ")){
			str = str.substring(0, str.length()-2);
		}

		str += "}";
		return str;
	}

	public boolean verticePertenceAosAlcancado(Vertice v, List<Vertice> alcancados){
		for (Vertice vertice : alcancados) {
			if(vertice.equals(v)){
				return true;
			}
		}
		return false;
	}

	public Edge existeArestaNãoExplorada(List<Edge> listEdge){
		for (Edge edge : listEdge) {
			if(!edge.getState().equals(EdgeState.EXPLORED)){
				return edge;
			}
		}
		return null;
	}


	/**
	 * @param v
	 * @param kindOfVertice  0-souce vertice / 1-targe Vertice
	 * @return
	 */
	public static List<Edge> getAllEdgeLinkedToVertice(Vertice v, int kindOfVertice){
		List<Edge> allEdgeLinkedToVerticeList =  new LinkedList<Edge>();

		for (Edge edge : graph.getEdges()) {

			if(kindOfVertice == MyConstants.SOURCE_VERTICE){
				if(edge.getSourceVertice().equals(v)){
					allEdgeLinkedToVerticeList.add(edge);
				}
			}else if(kindOfVertice == MyConstants.TARGET_VERTICE){
				if(edge.getTargetVertice().equals(v)){
					allEdgeLinkedToVerticeList.add(edge);
				}
			}
		}
		return allEdgeLinkedToVerticeList;
	}

	public boolean isAllEdgeWasExploredToSouceVertice(Vertice vertice){
		// Só rotorna verdade se todos as arestas que tenha como vertice de origem o parametro forem explorados
		for (Edge edge : graph.getEdges()) {
			if(edge.getSourceVertice().equals(vertice) && !edge.getState().equals(EdgeState.EXPLORED)){
				return false;
			}
		}
		return true;

	}


	/*
	 * 1:Fork--->Qtd Vertices de entrada() < Qtd Vertices de saida
	 * -1:Join-->Qtd Vertices de entrada > Qtd Vertices de saida
	 * 0:unknown-->Qtd Vertice de entrada == Qtd Vertice saida OR O vertice do parametri não é um Gateway
	 */
	/**
	 * @param v
	 * @return 
	 * Qtd Arestas de entrada--->Artestas que tem o parametro V como vertice Target
	 * Qtd Arestas de Saida--->Artestas que tem o parametro V como vertice Source
	 * Fork--->Qtd Arestas de entrada() < Qtd Arestas de saida
	 * Join-->Qtd Arestas de entrada > Qtd Arestas de saida
	 * unknown-->Qtd Arestas de entrada == Qtd Arestas saida OR O vertice do parametri não é um Gateway
	 */
	public static int getKindGateway(Vertice v){
		if(v.getVeriticeElement() instanceof AbstractGatewayElement){
			List<Edge> inputEdgeList = getAllEdgeLinkedToVertice(v, MyConstants.TARGET_VERTICE);
			List<Edge> outputEdgeList = getAllEdgeLinkedToVertice(v, MyConstants.SOURCE_VERTICE);
			if(inputEdgeList.size() < outputEdgeList.size()){
				return MyConstants.GATEWAY_FORK;
			}else if(inputEdgeList.size() > outputEdgeList.size()){
				return MyConstants.GATEWAY_JOIN;
			}else{
				return MyConstants.GATEWAY_UNKNOWN;
			}

		}else{
			return MyConstants.GATEWAY_UNKNOWN;
		}
	}






}
