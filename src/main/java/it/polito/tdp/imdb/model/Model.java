package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	
	private SimpleWeightedGraph<Actor, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer, Actor> mapActor;
	//Inizializziamo la Mappa

	
	public Model() {
		dao = new ImdbDAO();
		mapActor = new HashMap<Integer, Actor>();
		dao.listAllActors(mapActor);
	}
	
	public String creaGrafo(String g) {
		
		//Definiamolo
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Aggiungiamo i vertici
		Graphs.addAllVertices(this.grafo, dao.listActorForGenre(g));
		
		//Aggiungiamo gli archi
		for(LinkActors link: dao.getAllLinkForGenre(mapActor, g)) {
			Graphs.addEdgeWithVertices(this.grafo, link.getA1(), link.getA2(),link.getPeso());			
		}
		
		
		return String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),this.grafo.edgeSet().size());
		
	}	
	
	
	public List<String> getGeneri() {
		return dao.listAllGenre();
	}

	public Set<Actor> getVertex() {
		return  this.grafo.vertexSet();
	}

//	 in ordine alfabetico (campo last_name), tutti gli attori “collegati” ad a, ovvero tutti gli attori che si 
//	 possono raggiungere nel grafo, anche attraverso più passaggi, a partire da a.
	public List<Actor> getListaVicini(Actor a) {
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(grafo);
		List<Actor> actors = new ArrayList<>(ci.connectedSetOf(a));
		actors.remove(a);
		Collections.sort(actors, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.lastName.compareTo(o2.lastName);
			}
			
		});
		return actors;
	}

}
