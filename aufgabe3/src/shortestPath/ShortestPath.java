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
		// bei neu aufruf historie löschen
		dist.clear();
		pred.clear();
		cand.clear();

		dist.put(s, 0.0); // Vom Startknoten s zu sich selbst kostet es 0

		// prio bei Dijkstra = bsísherige Entfernung vom Start
		// prio bei A* = bisherige Entfernung + Schätzung, wie weit es noch bis zum Ziel
		// ist
		double prio = (h == null) ? 0.0 : h.estimatedCost(s, g); // Wenn keine Heuristik benutzen, ist Priorität 0,
																	// ansonsten benutzen wir a, dann schäzte: wie weir
																	// ust s vom ziel g entfernt?
		cand.add(s, prio); // Knoten, die noch untersucht werden sollen = füge den Startknoten in die
							// Kandidatenliste ein mit der Priorität prio

		while (!cand.isEmpty()) {
			V v = cand.removeMin(); // nimmt den vielversprechendesten knoten mit kleinster prio aus der Liste

			// Animation Male den Knoten blau, das zeigt, dass er gerade unetrsucht wird
			if (sim != null && v instanceof Integer)
				sim.visitStation((Integer) v, Color.BLUE);

			// Wenn v das Zeil g ist, brich ab
			if (v.equals(g))
				return true;

			for (V w : this.g.getNeighborSet(v)) { // für alle Nachbarn w von v: überlege, ob du über v einen besseren
													// Weg zu w findest
				double cost = dist.get(v) + this.g.getWeight(v, w); // neue mögliche Weg zu w geht über v. Daher: Kosten
																	// bis v + Kosten von v nach w
				if (!dist.containsKey(w) || cost < dist.get(w)) { // wenn w noch gar nicht untersucht wurde oder der
																	// neue weg billiger als der bisher bekannte
					dist.put(w, cost); // aktualisiere die Kosten für w
					pred.put(w, v); // aktualisiere den Vorgänger von w auf v

					// Falls Dijkstra -> prio = cost
					// Falls A* -> prio = cost + Schätzung
					double priority = (h == null) ? cost : cost + h.estimatedCost(w, g);
					if (cand.get(w) != null) // wenn w schon drinnen sist, ändere priorität
						cand.change(w, priority);
					else
						cand.add(w, priority); // sonst füge w neu ein
				}
			}
		}

		return false; // Wenn man aus der Schleife kommt, ohne das Zeil g gefunden zu haben, gibts
						// keinen Weg
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
			path.addFirst(v);
			v = pred.get(v);
			if (v == null)
				throw new IllegalArgumentException("Kein Pfad vorhanden.");
		}
		path.addFirst(start);
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
