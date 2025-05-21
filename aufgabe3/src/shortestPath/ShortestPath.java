// O. Bittel;
// 25.3.2021; jetzt mit IndexMinPq
// 30.06.2024; Anpassung auf ungerichtete Graphen

package shortestPath;

import undirectedGraph.*;
import sim.SYSimulation;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Kürzeste Wege in Graphen mit A*- und Dijkstra-Verfahren.
 * 
 * @author Oliver Bittel
 * @since 30.06.2024
 * @param <V> Knotentyp.
 */
public class ShortestPath<V> {

	SYSimulation sim = null;

	// Heuristik: Schätzung für A*, wie weit ein Knoten noch vom Ziel entfernt ist
	// Dijkstra sucht kürzesten Weg von Start zu allen erreichbaren Knote, weis
	// nichts über das Ziel
	// A* sucht den kürzesten Weg zu einem bestimmten Ziel

	Map<V, Double> dist; // "Wie weit ist v vom Start?
	Map<V, V> pred; // "Wie kommt man zu v? Wer ist der Vorgänger"
	IndexMinPQ<V, Double> cand; // PriorityQueue: verwaltet alle offenen Knoten

	// ...
	private final UndirectedGraph<V> g; // Stadtplan
	private final Heuristic<V> h; // Heuritstik, Schätzung, wie weit es noch bis zum Ziel ist bei A*
	private V start = null;
	private V goal = null;

	/**
	 * Konstruiert ein Objekt, das im Graph g k&uuml;rzeste Wege
	 * nach dem A*-Verfahren berechnen kann.
	 * Die Heuristik h schätzt die Kosten zwischen zwei Knoten ab.
	 * Wird h = null gewählt, dann ist das Verfahren identisch
	 * mit dem Dijkstra-Verfahren.
	 * 
	 * @param g Gerichteter Graph
	 * @param h Heuristik. Falls h == null, werden kürzeste Wege nach
	 *          dem Dijkstra-Verfahren gesucht.
	 */
	public ShortestPath(UndirectedGraph<V> g, Heuristic<V> h) {
		dist = new HashMap<>();
		pred = new HashMap<>();
		cand = new IndexMinPQ<>();
		// ...
		this.g = g;
		this.h = h;
	}

	/**
	 * Diese Methode sollte nur verwendet werden,
	 * wenn kürzeste Wege in Scotland-Yard-Plan gesucht werden.
	 * Es ist dann ein Objekt für die Scotland-Yard-Simulation zu übergeben.
	 * <p>
	 * Ein typische Aufruf für ein SYSimulation-Objekt sim sieht wie folgt aus:
	 * <blockquote>
	 * 
	 * <pre>
	 * if (sim != null)
	 * 	sim.visitStation((Integer) v, Color.blue);
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param sim SYSimulation-Objekt.
	 */
	public void setSimulator(SYSimulation sim) {
		this.sim = sim;
	}

	/**
	 * Sucht den kürzesten Weg von Starknoten s zum Zielknoten g.
	 * <p>
	 * Falls die Simulation mit setSimulator(sim) aktiviert wurde, wird der Knoten,
	 * der als nächstes aus der Kandidatenliste besucht wird, animiert.
	 * 
	 * @param s Startknoten
	 * @param g Zielknoten
	 */
	public boolean searchShortestPath(V s, V g) {
		this.start = s;
		this.goal = g;

		dist.clear();
		pred.clear();
		cand.clear();

		dist.put(s, 0.0); // Startknoten zu sich selbst: Entfernung 0

		// prio bei Dijkstra = 0
		// prio bei A* = 0 + Heuristik[v,g]
		double prio = (h == null) ? 0.0 : h.estimatedCost(s, g);
		cand.add(s, prio); // Knoten, die noch untersucht werden sollen - füge den Startknoten in
							// Kandidatenliste ein

		while (!cand.isEmpty()) {
			V v = cand.removeMin(); // Knoten mit der kleinsten Priorität entfernen

			if (sim != null && v instanceof Integer)
				sim.visitStation((Integer) v, Color.BLUE); // Station vom Knoten v animieren
			if (v.equals(g))
				return true; // Zielknoten gefunden, aAbbruch

			for (V w : this.g.getNeighborSet(v)) { // für alle Nachbarn w von v
				double cost = dist.get(v) + this.g.getWeight(v, w); // Kosten von v nach w

				if (!dist.containsKey(w) || cost < dist.get(w)) { // Erster Weg zu w oder kürzerer Weg
					dist.put(w, cost); // neue Gesamtkosten
					pred.put(w, v); // Vorgänger merken
					// A* = d[v] + Heuristik(w,g)
					// Dijkstra = d[v]

					double priority = (h == null) ? cost : cost + h.estimatedCost(w, g);
					if (cand.get(w) != null)
						cand.change(w, priority); // prioritätswert ändern wenn w in liste ist
					else
						cand.add(w, priority); // sonst füge w neu ein
				}
			}
		}

		return false; // kein Weg gefunden
	}

	/**
	 * Liefert einen kürzesten Weg von Startknoten s nach Zielknoten g.
	 * Setzt eine erfolgreiche Suche von searchShortestPath(s,g) voraus.
	 * 
	 * @throws IllegalArgumentException falls kein kürzester Weg berechnet wurde.
	 * @return kürzester Weg als Liste von Knoten.
	 */
	public List<V> getShortestPath() {
		// ...
		if (goal == null || !dist.containsKey(goal))
			throw new IllegalArgumentException("Kein kürzester Weg berechnet.");

		LinkedList<V> path = new LinkedList<>();
		V v = goal;
		while (!v.equals(start)) {
			path.addFirst(v); // Knoten v vorne in Liste einfügen
			v = pred.get(v); // Vorgänger von v
			if (v == null)
				throw new IllegalArgumentException("Kein Pfad vorhanden.");
		}
		path.addFirst(start); // Startknoten noch vorne einfügen
		return path;
	}

	/**
	 * Liefert die Länge eines kürzesten Weges von Startknoten s nach Zielknoten g
	 * zurück.
	 * Setzt eine erfolgreiche Suche von searchShortestPath(s,g) voraus.
	 * 
	 * @throws IllegalArgumentException falls kein kürzester Weg berechnet wurde.
	 * @return Länge eines kürzesten Weges.
	 */
	public double getDistance() {
		// ...
		if (goal == null || !dist.containsKey(goal))
			throw new IllegalArgumentException("Kein kürzester Weg berechnet.");
		return dist.get(goal);
	}
}
