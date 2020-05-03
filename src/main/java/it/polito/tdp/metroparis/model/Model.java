package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata,DefaultEdge> graph;
	private List<Fermata> fermate;
	private Map<Integer,Fermata> fermateIdMap;
	
	public Model() {
		this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
		this.fermateIdMap = new HashMap<>();
		for(Fermata f : this.fermate) {
			this.fermateIdMap.put(f.getIdFermata(),f);
		}
		//CREAZIONE DEI VERTICI
		Graphs.addAllVertices(this.graph, this.fermate);
		
		//System.out.println(this.graph);
		
		//CREAZIONE DEGLI ARCHI
		//METODO 1: TUTTE LE COPPIE
		/*for (Fermata fp : this.fermate) {
			for(Fermata fa : this.fermate) {
				if(dao.fermateConnesse(fp, fa)) {
					this.graph.addEdge(fa, fp);
				}
					
				}
			}*/
		//CREAZIONE ARCHI
		//METODO 2: DA UN VERTICE TROVA TUTTI I CONNESSI
		
		for (Fermata fp : this.fermate) {
			List<Fermata> connesse = dao.fermateSuccessive(fp, fermateIdMap);
			
			for(Fermata fa : connesse) {//complessità: n*grado medio dei vertici-->n*densità del grafo
				this.graph.addEdge(fp, fa);
			}
		}
		
		//CREAZIONE ARCHI
		//CHIEDO AL DATABASE
		
		List<CoppiaFermate> coppieFermate = dao.coppieFermate(this.fermateIdMap);
	    for(CoppiaFermate c : coppieFermate){
			this.graph.addEdge(c.getFa(),c.getFp());
		}
		
		
		//System.out.println(this.graph);
		System.out.format("Grafo caricato con %d vertici e %d archi", this.graph.vertexSet().size(),this.graph.edgeSet().size());
		
		}
	
	public static void main(String args[]) {
		Model m = new Model();
	}

}
