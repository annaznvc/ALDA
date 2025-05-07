package directedGraph;

import java.util.*;

public class StrongComponents<V> {
    private final Map<Integer, Set<V>> comp = new TreeMap<>();
    private int numberOfComp = 0;

    /**
     * Ermittelt alle strengen Zusammenhangskomponenten mit dem Kosaraju-Sharir Algorithmus.
     * @param g gerichteter Graph.
     */
    public StrongComponents(DirectedGraph<V> g) {
        // 1. PostOrder im Originalgraph
        Set<V> visited = new HashSet<>();
        LinkedList<V> postOrder = new LinkedList<>();

        for (V v : g.getVertexSet()) {
            if (!visited.contains(v)) {
                dfsPostOrder(v, g, visited, postOrder);
            }
        }

        // 2. Transponierter Graph (alle Kanten umdrehen)
        DirectedGraph<V> gt = transpose(g);

        // 3. DFS im transponierten Graphen in umgekehrter PostOrder
        visited.clear();
        Collections.reverse(postOrder);

        for (V v : postOrder) {
            if (!visited.contains(v)) {
                Set<V> oneComponent = new TreeSet<>();
                dfsCollectComponent(v, gt, visited, oneComponent);
                comp.put(numberOfComp++, oneComponent);
            }
        }
    }

    // ... Tiefensuche zum Sammeln der PostOrder-Reihenfolge
    private void dfsPostOrder(V v, DirectedGraph<V> g, Set<V> visited, List<V> postOrder) {
        visited.add(v);
        for (V w : g.getSuccessorVertexSet(v)) {
            if (!visited.contains(w)) {
                dfsPostOrder(w, g, visited, postOrder);
            }
        }
        postOrder.add(v);
    }

    // ... Transponierten Graphen erzeugen (alle Kanten umdrehen)
    private DirectedGraph<V> transpose(DirectedGraph<V> g) {
        DirectedGraph<V> gt = new AdjacencyListDirectedGraph<>();
        for (V v : g.getVertexSet()) {
            for (V w : g.getSuccessorVertexSet(v)) {
                gt.addEdge(w, v); // Kante umkehren
            }
        }
        return gt;
    }

    // ... Tiefensuche im transponierten Graphen zum Einsammeln einer Komponente
    private void dfsCollectComponent(V v, DirectedGraph<V> g, Set<V> visited, Set<V> component) {
        visited.add(v);
        component.add(v);
        for (V w : g.getSuccessorVertexSet(v)) {
            if (!visited.contains(w)) {
                dfsCollectComponent(w, g, visited, component);
            }
        }
    }

    public int numberOfComp() {
        return numberOfComp;
    }

    public Set<V> getComp(int i) {
        return comp.get(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Set<V>> entry : comp.entrySet()) {
            sb.append("Component ").append(entry.getKey()).append(": ");
            for (V v : entry.getValue()) {
                sb.append(v).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DirectedGraph<Integer> g = new AdjacencyListDirectedGraph<>();
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 1);
        g.addEdge(2, 3);
        g.addEdge(3, 1);

        g.addEdge(1, 4);
        g.addEdge(5, 4);

        g.addEdge(5, 7);
        g.addEdge(6, 5);
        g.addEdge(7, 6);

        g.addEdge(7, 8);
        g.addEdge(8, 2);

        StrongComponents<Integer> sc = new StrongComponents<>(g);

        System.out.println("Anzahl starker Zusammenhangskomponenten: " + sc.numberOfComp());
        System.out.println(sc);
			// Component 0: 5, 6, 7, 
        	// Component 1: 8, 
            // Component 2: 1, 2, 3, 
            // Component 3: 4, 
    }
}
