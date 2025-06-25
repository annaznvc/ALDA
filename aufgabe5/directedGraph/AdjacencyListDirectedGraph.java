// O. Bittel;
// 19.03.2018

package directedGraph;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

/**
 * Implementierung von DirectedGraph mit einer doppelten TreeMap
 * für die Nachfolgerknoten und einer einer doppelten TreeMap
 * für die Vorgängerknoten.
 * <p>
 * Beachte: V muss vom Typ Comparable&lt;V&gt; sein.
 * <p>
 * Entspicht einer Adjazenzlisten-Implementierung
 * mit schnellem Zugriff auf die Knoten.
 * 
 * 
 * @author Oliver Bittel
 * @since 19.03.2018
 * @param <V> Knotentyp.
 */
public class AdjacencyListDirectedGraph<V> implements DirectedGraph<V> {
    // doppelte Map für die Nachfolgerknoten: (führende Map)
    private final Map<V, Map<V, Double>> succ = new TreeMap<>();

    // doppelte Map für die Vorgängerknoten:
    private final Map<V, Map<V, Double>> pred = new TreeMap<>();

    private int numberEdge = 0;

    /**
     * Fügt neuen Knoten zum Graph dazu.
     * 
     * @param v Knoten.
     * @return true, wenn Knoten neu hinzugefügt wurde.
     */

    @Override
    public boolean addVertex(V v) {
        if (succ.containsKey(v)) // ist der knoten v schin im graphen enthalten?
            return false;

        succ.put(v, new TreeMap<>());
        pred.put(v, new TreeMap<>());
        return true;
    }

    /**
     * Fügt neue Kante mit Gewicht weight zum Graph dazu.
     * Falls einer der beiden Knoten noch nicht im Graphen vorhanden ist, dann wird
     * er dazugefügt.
     * Falls die Kante schon vorhanden ist, dann wird das Gewicht mit weight
     * überschrieben.
     * return true, falls Kante noch nicht vorhanden war.
     * v - Startknoten
     * w - Zielknoten
     * weight - Gewicht
     */
    @Override
    public boolean addEdge(V v, V w, double weight) {
        if (!succ.containsKey(v))
            addVertex(v);
        if (!succ.containsKey(w))
            addVertex(w);

        if (succ.get(v).containsKey(w)) { // prüfe, ob kane v -> w bereits existiert
            succ.get(v).put(w, weight); // einfach gewicht überschreiben mit dem neuen
            pred.get(w).put(v, weight);
            return false;
        } else {
            succ.get(v).put(w, weight);
            pred.get(w).put(v, weight);
            numberEdge++;
            return true;
        }
    }

    /***
     * Fügt neue Kante (mit Gewicht 1) zum Graph dazu.
     * Falls einer der beiden Knoten noch nicht im Graphen vorhanden ist, dann wird
     * er dazugefügt.
     * Falls die Kante schon vorhanden ist, dann wird das Gewicht mit 1
     * überschrieben.
     * Parameter:
     * v - Startknoten
     * w - Zielknoten
     * Gibt zurück:
     * true, falls Kante noch nicht vorhanden war.
     */
    @Override
    public boolean addEdge(V v, V w) {
        return addEdge(v, w, 1.0);
    }

    /**
     * Prüft ob Knoten v im Graph vorhanden ist.
     * true, falls Knoten vorhanden ist
     */
    @Override
    public boolean containsVertex(V v) {
        return succ.containsKey(v);
    }

    /**
     * Prüft ob Kante im Graph vorhanden ist.
     * Parameter:
     * v - Startknoten
     * w - Endknoten
     * Gibt zurück:
     * true, falls Kante vorhanden ist.
     */
    @Override
    public boolean containsEdge(V v, V w) {
        return succ.containsKey(v) && succ.get(v).containsKey(w);
    }

    /**
     * 
     * Liefert Gewicht der Kante zurück.
     */
    @Override
    public double getWeight(V v, V w) {
        if (succ.containsKey(v) && succ.get(v).containsKey(w))
            return succ.get(v).get(w);
        else
            return Double.NaN;
    }

    /**
     * Liefert Eingangsgrad des Knotens v zurück.
     * Das ist die Anzahl der Kanten mit Zielknoten v.
     */
    @Override
    public int getInDegree(V v) {
        if (pred.containsKey(v))
            return pred.get(v).size();
        else
            return 0;
    }

    /**
     * Liefert Ausgangsgrad des Knotens v zurück.
     * Das ist die Anzahl der Kanten mit Quellknoten v.
     */
    @Override
    public int getOutDegree(V v) {
        if (succ.containsKey(v))
            return succ.get(v).size();
        else
            return 0;
    }

    @Override
    public Set<V> getVertexSet() {
        return Collections.unmodifiableSet(succ.keySet()); // nicht modifizierbare Sicht
    }

    /**
     * Liefert eine nicht modifizierbare Sicht (unmodifiable view) auf die Menge
     * aller Vorgängerknoten von v zurück.
     * Das sind alle die Knoten, von denen eine Kante zu v führt.
     */
    @Override
    public Set<V> getPredecessorVertexSet(V v) {
        if (pred.containsKey(v))
            return Collections.unmodifiableSet(pred.get(v).keySet());
        else
            return Collections.emptySet();
    }

    /**
     * Liefert eine nicht modifizierbare Sicht (unmodifiable view) auf die Menge
     * aller Nachfolgerknoten von v zurück.
     * Das sind alle die Knoten, zu denen eine Kante von v führt.
     */
    @Override
    public Set<V> getSuccessorVertexSet(V v) {
        if (succ.containsKey(v))
            return Collections.unmodifiableSet(succ.get(v).keySet());
        else
            return Collections.emptySet();
    }

    // Liefert Anzahl der Knoten im Graph zurück.
    @Override
    public int getNumberOfVertexes() {
        return succ.size();
    }

    // Liefert Anzahl der Kanten im Graph zurück.
    @Override
    public int getNumberOfEdges() {
        return numberEdge;
    }

    // Erzeugt einen invertierten Graphen, indem jede Kante dieses Graphens in
    // umgekehrter Richtung abgespeichert wird.
    @Override
    public DirectedGraph<V> invert() {
        AdjacencyListDirectedGraph<V> inverted = new AdjacencyListDirectedGraph<>();

        for (var v : getVertexSet()) {
            inverted.addVertex(v);

            for (var w : getSuccessorVertexSet(v)) {
                double gewicht = getWeight(v, w);
                inverted.addEdge(w, v, gewicht);
            }
        }

        return inverted;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (V v : succ.keySet()) {
            for (V w : succ.get(v).keySet()) {
                double weight = succ.get(v).get(w);
                sb.append(v)
                        .append(" --> ")
                        .append(w)
                        .append(" weight = ")
                        .append(weight)
                        .append("\n");
            }
        }

        return sb.toString();
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
        g.addEdge(7, 4);

        System.out.println(g.getNumberOfVertexes()); // 7
        System.out.println(g.getNumberOfEdges()); // 8
        System.out.println(g.getVertexSet()); // 1, 2, ..., 7
        System.out.println(g);
        // 1 --> 2 weight = 1.0
        // 2 --> 5 weight = 1.0
        // 2 --> 6 weight = 1.0
        // 3 --> 7 weight = 1.0
        // ...

        System.out.println("");
        System.out.println(g.getOutDegree(2)); // 2
        System.out.println(g.getSuccessorVertexSet(2)); // 5, 6
        System.out.println(g.getInDegree(6)); // 2
        System.out.println(g.getPredecessorVertexSet(6)); // 2, 4

        System.out.println("");
        System.out.println(g.containsEdge(1, 2)); // true
        System.out.println(g.containsEdge(2, 1)); // false
        System.out.println(g.getWeight(1, 2)); // 1.0
        g.addEdge(1, 2, 5.0);
        System.out.println(g.getWeight(1, 2)); // 5.0

        System.out.println("");
        System.out.println(g.invert());
        // 1 --> 5 weight = 1.0
        // 2 --> 1 weight = 5.0
        // 3 --> 4 weight = 1.0
        // 4 --> 7 weight = 1.0
        // ...

        Set<Integer> s = g.getSuccessorVertexSet(2);
        System.out.println(s);
        s.remove(5); // Laufzeitfehler! Warum?

        /**
         * getSuccessorVertexSet(v) gibt ein unveränderbares Set mit
         * Collections.unmodifiableSet() zurück.
         * Das Set darf nur gelesen, aber nicht verändert werden.
         * Deswegen Laufzeitfehler
         * 
         */
    }
}
