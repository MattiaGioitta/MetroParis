package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

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
		/*List<Fermata> visita = m.visitaInAmpiezza(m.fermate.get(0));
		System.out.println(visita);
		
		Map<Fermata,Fermata> albero = m.creaAlberoInVisitaInAmpiezza(m.fermate.get(0));
		for (Fermata f : albero.keySet()) {
			System.out.format("%s <- %s\n", f, albero.get(f));
		}
		*/
		List<Fermata> cammino = m.camminiMinimi(m.fermate.get(0), m.fermate.get(1));		
		System.out.println(cammino);
	}
	/**
	 * Visita l'intero grafo con la strategia del Breadth First
	 * e ritorna l'insieme dei vertici incontrati.
	 * @param source
	 * @return insieme dei vertici incontrati
	 */
	public List<Fermata> visitaInAmpiezza(Fermata source) {
		List<Fermata> visita = new ArrayList<Fermata>();
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.graph, source);
        while(bfv.hasNext()) {
        	visita.add(bfv.next());       	
        }
        return visita;
	}
	
	public List<Fermata> visitaInProfondita(Fermata source) {
		List<Fermata> visita = new ArrayList<Fermata>();
		DepthFirstIterator<Fermata, DefaultEdge> bfv = new DepthFirstIterator<>(this.graph, source);
        while(bfv.hasNext()) {
        	visita.add(bfv.next());       	
        }
        return visita;
	}
	
	public Map<Fermata, Fermata> creaAlberoInVisitaInAmpiezza(Fermata source){
		Map<Fermata,Fermata> albero = new HashMap<>();
		albero.put(null,source);
		GraphIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.graph, source);
	    bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				// la visita considera un arco
			    // questo arco ha scoperto un nuovo vertice, se si mi chiedo da dove
				//
				DefaultEdge edge = e.getEdge(); //arco di tipo A->B o B->A
				Fermata a = graph.getEdgeSource(edge);
				Fermata b = graph.getEdgeTarget(edge);
				if(albero.containsKey(a)) {
					 albero.put(b, a);
					 }
				else
				{
					albero.put(a, b);
				}
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    
	    while(bfv.hasNext()) {
	    	bfv.next(); // estrai l'elemento e ignoralo
	    }
	    
	    return albero;
	}
	
	public List<Fermata> camminiMinimi(Fermata partenza, Fermata arrivo) {
		DijkstraShortestPath<Fermata, DefaultEdge> dij = new DijkstraShortestPath<>(this.graph);
		GraphPath cammino = dij.getPath(partenza, arrivo);
		return cammino.getVertexList();
		
		
	}
	

}
