package it.polito.tdp.crimes.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.EdgeSetFactory;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {

	EventsDao dao;
	Graph<String, DefaultWeightedEdge> grafo;
	List<String> parziale;
	List<String> soluzione;
	String target;

	public List<String> getCategorieReati() {
		dao = new EventsDao();
		List<String> result = new ArrayList<String>();
		for (Event e : dao.listAllEvents())
			if (result.contains(e.getOffense_category_id()) == false)
				result.add(e.getOffense_category_id());

		return result;
	}

	public List<Month> getMesi() {
		dao = new EventsDao();
		return dao.getMesi();
	}

	public Graph<String, DefaultWeightedEdge> creaGrafo(Month mese, String categoria) {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao = new EventsDao();
		List<Event> eventi = dao.listGraphEvents(mese.getValue(), categoria);

		for (Event e : eventi)
			grafo.addVertex(e.getOffense_type_id());

		List<Adiacenze> archi = dao.getAdiacenze(categoria, mese.getValue());

		for (Adiacenze a : archi) {
			DefaultWeightedEdge e = grafo.getEdge(a.getTipo1(), a.getTipo2());
			if (e != null)
				grafo.setEdgeWeight(e, grafo.getEdgeWeight(e) + a.getPeso());
			else
				Graphs.addEdge(grafo, a.getTipo1(), a.getTipo2(), a.getPeso());
		}

		return grafo;
	}

	public List<String> archiSopraLaMedia(Graph<String, DefaultWeightedEdge> g) {
		List<String> res = new ArrayList<String>();
		double media = calcolaMedia(g);
		String s = "";
		for (DefaultWeightedEdge e : g.edgeSet())
			if (g.getEdgeWeight(e) > media) {
				s = g.getEdgeSource(e) + " " + g.getEdgeTarget(e) + " " + g.getEdgeWeight(e);
				res.add(s);
			}
		return res;

	}

	public double calcolaMedia(Graph<String, DefaultWeightedEdge> g) {
		double somma = 0;
		for (DefaultWeightedEdge e : g.edgeSet())
			somma += g.getEdgeWeight(e);

		return somma / g.edgeSet().size();
	}

	public List<String> calcolaPercorso(String source, String target) {
		parziale = new ArrayList<String>();
		soluzione = new ArrayList<String>();
		this.target = target;
		ricorsione(parziale, source);
		return soluzione;
	}

	public void ricorsione(List<String> parziale, String source) {

		for (DefaultWeightedEdge e : grafo.edgeSet())
			if (grafo.getEdgeSource(e).equals(source) && parziale.contains(grafo.getEdgeTarget(e)) == false) {
				parziale.add(grafo.getEdgeSource(e));

				if (grafo.getEdgeTarget(e).equals(target))
					if (parziale.size() + 1 > soluzione.size()) {
						parziale.add(grafo.getEdgeTarget(e));
						soluzione = new ArrayList<String>(parziale);
						return;
					}

				ricorsione(parziale, grafo.getEdgeTarget(e));
				parziale.remove(parziale.size() - 1);
			}
	}

}
