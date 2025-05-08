// O. Bittel;
// 30.07.2024

package directedGraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Klasse zur Erstellung einer topologischen Sortierung.
 * @author Oliver Bittel
 * @since 22.02.2017
 * @param <V> Knotentyp.
 */
public class TopologicalSort<V> {
    private List<V> ts = new LinkedList<>(); // topologisch sortierte Folge

	/**
	 * Führt eine topologische Sortierung für g mit Tiefensuche durch.
	 * @param g gerichteter Graph.
	 */

	//implementiert nach 8-36
	public TopologicalSort(DirectedGraph<V> g) {
        //1)Prüfen, ob ein Zyklus im Graphen ist
        DirectedCycle<V> dc = new DirectedCycle<>(g);
        if (dc.hasCycle()) { //aus Klasse DirectedCycle
            return; //keine topologische Sortierung möglich
        }

        //2)Postorder Reihenfolge ermitteln
        Set<V> visited = new HashSet<>();
        LinkedList<V> postOrder = new LinkedList<>();
        for (V v : g.getVertexSet()) {
            if (!visited.contains(v)) { //wenn v nicht bescuht wurde, starte tiefensuche bei v
                dfs(v, g, visited, postOrder); //wenn alle Nachfolger behandelt wurden, wird er zur postOrder-Liste hinzugefügt
            }
        }

        // 3. Invertiere PostOrder → topologische Sortierung
        Collections.reverse(postOrder); //post-order gibt alle knoten nachdem alle nachfolger verarbeitet wurden, aber für topolgische sortierung brauchen wir knoten bevor ihre nachfolger kommen
        ts.addAll(postOrder);
    }

	private void dfs(V v, DirectedGraph<V> g, Set<V> visited, List<V> postOrder) {
        visited.add(v);
        for (V w : g.getSuccessorVertexSet(v)) { ///alle nachfolger w von v durchlaufen
            if (!visited.contains(w)) { //wenn w nicht besucht, gehe tiefer in den graphen
                dfs(w, g, visited, postOrder);
            }
        }
        postOrder.add(v); // Post-Order-liste hinzufügen nachdem alle nachfolger durchlaufen wurden
    }

    
	/**
	 * Liefert eine nicht modifizierbare Liste (unmodifiable view) zurück,
	 * die topologisch sortiert ist.
	 * @return topologisch sortierte Liste, falls topologsche Sortierung möglich ist, sonst null.
	 */
	public List<V> topologicalSortedList() {
        return ts.isEmpty() ? null : Collections.unmodifiableList(ts);
    }
    

	public static void main(String[] args) {
		test1();
		test2();
	}

	private static void test1() {
		DirectedGraph<Integer> g = new AdjacencyListDirectedGraph<>();
		g.addEdge(1, 3);
		g.addEdge(2, 3);
		g.addEdge(3, 4);
		g.addEdge(3, 5);
		g.addEdge(4, 6);
		g.addEdge(5, 6);
		g.addEdge(6, 7);
		//g.addEdge(7, 1); // Kante führt zu Zyklus

		TopologicalSort<Integer> ts = new TopologicalSort<>(g);
		
		if (ts.topologicalSortedList() != null) {
			System.out.println("Topologische Sortierung:");
			System.out.println(ts.topologicalSortedList()); // [2, 1, 3, 5, 4, 6, 7]
		}
	}

	private static void test2() {
		// Morgendliches Anziehen:
		DirectedGraph<String> anziehGraph = new AdjacencyListDirectedGraph<>();
		anziehGraph.addEdge("Socken", "Schuhe");
		anziehGraph.addEdge("Unterhose", "Hose");
		anziehGraph.addEdge("Hose", "Schuhe");
		anziehGraph.addEdge("Hose", "Gürtel");
		anziehGraph.addEdge("Unterhemd", "Hemd");
		anziehGraph.addEdge("Hemd", "Pulli");
		anziehGraph.addEdge("Pulli", "Mantel");
		anziehGraph.addEdge("Gürtel", "Mantel");
		anziehGraph.addEdge("Mantel", "Schal");
		anziehGraph.addEdge("Schuhe", "Handschuhe");
		anziehGraph.addEdge("Schal", "Handschuhe");
		anziehGraph.addEdge("Mütze", "Handschuhe");

		TopologicalSort<String> ts = new TopologicalSort<>(anziehGraph);

		if (ts.topologicalSortedList() != null) {
			System.out.println("Topologische Sortierung:");
			System.out.println(ts.topologicalSortedList());
		}
	}
}

/**
 *  
 *
 * 
 */
