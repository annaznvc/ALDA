package floyd;

import java.util.*;

// Ermittelt für jeden Knoten v seine Relevanz.
// Die Relevanz eines Knotens v ist die Anzahl aller kürzesten Wege, die über v führen.
//
public class Relevance<V> {
    private Map<V, Integer> relevance = new HashMap<>(); // Relevanz für jeden Knoten v.
    private List<V> allNodesSortedByRelevance; // Liste aller Knoten, sortiert nach Relevanz

    public Relevance(Floyd<V> floyd) {
        // Initialisiere die Relevanz für jeden Knoten mit 0
        Set<V> allNodes = floyd.getAllNodes();
        for (V v : allNodes) {
            relevance.put(v, 0);
        }

        // Berechne die Relevanz für jeden Knoten v:
        // Für jeden kürzesten Weg von i nach j prüfen, ob er über v führt
        for (V i : allNodes) {
            for (V j : allNodes) {
                if (!i.equals(j) && floyd.getDist(i, j) != Double.POSITIVE_INFINITY) {
                    List<V> path = floyd.getShortestPath(i, j);

                    // Zähle alle Zwischenknoten (nicht Start- und Endknoten)
                    for (int k = 1; k < path.size() - 1; k++) {
                        V intermediateNode = path.get(k);
                        relevance.put(intermediateNode, relevance.get(intermediateNode) + 1);
                    }
                }
            }
        }

        // Sortiere alle Knoten nach ihrer Relevanz (absteigend):
        allNodesSortedByRelevance = new ArrayList<>(allNodes);
        allNodesSortedByRelevance.sort((v1, v2) -> {
            int relevanceComparison = Integer.compare(relevance.get(v2), relevance.get(v1));
            if (relevanceComparison != 0) {
                return relevanceComparison;
            }
            // Bei gleicher Relevanz alphabetisch sortieren für deterministische Reihenfolge
            return v1.toString().compareTo(v2.toString());
        });
    }

    List<V> getAllNodesSortedByRelevance() {
        return Collections.unmodifiableList(allNodesSortedByRelevance);
    }

    int getRelevance(V v) {
        return relevance.get(v);
    }
}