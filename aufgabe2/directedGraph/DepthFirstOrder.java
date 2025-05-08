// O. Bittel;
// 22.02.2017
package directedGraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Klasse für Tiefensuche.
 *
 * @author Oliver Bittel
 * @since 22.02.2017
 * @param <V> Knotentyp.
 */
public class DepthFirstOrder<V> {

    private final List<V> preOrder = new LinkedList<>();
    private final List<V> postOrder = new LinkedList<>();
    private final DirectedGraph<V> myGraph;
    private int numberOfDFTrees = 0; //zählt, wie viele Startpunkte es gab (für unzusammenhängende Teile)


    //visitDf implementiert wie in 8-2 zusätzlich mit pre und post order
    private void visitDF(V v, Set<V> visited) {
        visited.add(v);
        preOrder.add(v); //v zum ersten mal betreten
    
        for (V w : myGraph.getSuccessorVertexSet(v)) {
            if (!visited.contains(w)) {
                visitDF(w, visited);
            }
        }
    
        postOrder.add(v); //v nachdem alle rekursiven Aufrufe zu seinen Nachbarn abgeschlossen sind
    }
    


    /**
     * Führt eine Tiefensuche für g durch.
     *
     * @param g gerichteter Graph.
     */

    //implementiert wie 8-12
   public DepthFirstOrder(DirectedGraph<V> g) {
    this.myGraph = g; //speichert graphen g in my graph, damit visitDF damit arbeiten kann
    Set<V> visited = new HashSet<>();

    for (V v : g.getVertexSet()) {
        if (!visited.contains(v)) { //wenn knoten unbesucht, starte DFs
            numberOfDFTrees++; //zähle neuen DFS Baum hoch
            visitDF(v, visited);
        }
    }
}


    /**
     * Liefert eine nicht modifizierbare Liste (unmodifiable view) mit einer
     * Pre-Order-Reihenfolge zurück.
     *
     * @return Pre-Order-Reihenfolge der Tiefensuche.
     */
    public List<V> preOrder() {
        return Collections.unmodifiableList(preOrder);
    }

    /**
     * Liefert eine nicht modifizierbare Liste (unmodifiable view) mit einer
     * Post-Order-Reihenfolge zurück.
     *
     * @return Post-Order-Reihenfolge der Tiefensuche.
     */
    public List<V> postOrder() {
        return Collections.unmodifiableList(postOrder);
    }

    /**
     *
     * @return Anzahl der Bäume des Tiefensuchwalds.
     */
    public int numberOfDFTrees() {
        return numberOfDFTrees;
    }

    public static void main(String[] args) {
        DirectedGraph<Integer> g = new AdjacencyListDirectedGraph<>();
        g.addEdge(1, 2);
        g.addEdge(2, 5);
        g.addEdge(5, 1);
        g.addEdge(2, 6);
        g.addEdge(3, 7);
        g.addEdge(4, 3);
        g.addEdge(4, 6);
        //g.addEdge(7,3);
        g.addEdge(7, 4);

        DepthFirstOrder<Integer> dfs = new DepthFirstOrder<>(g);
        System.out.println(dfs.numberOfDFTrees());	// 2
        System.out.println(dfs.preOrder());		// [1, 2, 5, 6, 3, 7, 4]
        System.out.println(dfs.postOrder());	// [5, 6, 2, 1, 4, 7, 3]
    }
}
