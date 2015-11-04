package poli.mestrado.parser.bpmn2use.graph;
import java.util.List;


public class Graph {
	
	private List<Vertice> vertices;
	private List<Edge>  edges;
	
	public Graph(List<Vertice> vertices, List<Edge> edges) {
		super();
		this.vertices = vertices;
		this.edges = edges;
	}

	public List<Vertice> getVertices() {
		return vertices;
	}

	public void setVertices(List<Vertice> vertices) {
		this.vertices = vertices;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	
	@Override
	public String toString() {
		String str = "";

		str += "\n-------------------------Vertice----------------------\n";
		for (Vertice vertice : vertices) {
			str += vertice.toString()+"\n";
		}
		
		str += "\n-------------------------Edge----------------------\n";
		for (Edge edge : edges) {
			str += edge.toString()+"\n";
		}
		
		return str;
	}
	
	
	

}
