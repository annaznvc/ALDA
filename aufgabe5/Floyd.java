package floyd;

import java.io.PrintWriter;
import java.util.*;

import directedGraph.*;

// Ermittelt alle kürzesten Wege für gerichtete Graphen mit dem Floyd Algorithmus
public class Floyd<V> {
    private final Map<V, Map<V, Double>> dist = new TreeMap<>(); // Distanz-Matrix
    private final Map<V, Map<V, V>> pred = new TreeMap<>(); // Vorgänger-Matrix für kürzeste Wege

    public Floyd(DirectedGraph<V> g) {
        // Initialisierung der Distanz- und Vorgängermatrix
        for (V v : g.getVertexSet()) {
            dist.put(v, new TreeMap<>());
            pred.put(v, new TreeMap<>());
            for (V w : g.getVertexSet()) {
                if (v.equals(w)) {
                    dist.get(v).put(w, 0.0);
                    pred.get(v).put(w, null);
                } else if (g.containsEdge(v, w)) {
                    dist.get(v).put(w, g.getWeight(v, w));
                    pred.get(v).put(w, v);
                } else {
                    dist.get(v).put(w, Double.POSITIVE_INFINITY);
                    pred.get(v).put(w, null);
                }
            }
        }

        // Floyd-Warshall Algorithmus
        for (V k : g.getVertexSet()) {
            for (V i : g.getVertexSet()) {
                for (V j : g.getVertexSet()) {
                    double alt = dist.get(i).get(j);
                    double neu = dist.get(i).get(k) + dist.get(k).get(j);
                    if (neu < alt) {
                        dist.get(i).put(j, neu);
                        pred.get(i).put(j, pred.get(k).get(j));
                    }
                }
            }
        }

        // Überprüfung auf negative Zyklen
        for (V v : g.getVertexSet()) {
            if (dist.get(v).get(v) < 0)
                throw new IllegalArgumentException("Negativer Zyklus gefunden!");
        }
    }

    public double getDist(V v, V w) {
        return dist.get(v).get(w);
    }

    public List<V> getShortestPath(V v, V w) {
        List<V> path = new LinkedList<>();
        if (dist.get(v).get(w).equals(Double.POSITIVE_INFINITY) || v.equals(w))
            return path;
        V current = w;
        while (current != null && !current.equals(v)) {
            path.add(0, current);
            current = pred.get(v).get(current);
        }
        if (current == null)
            return new LinkedList<>(); // Kein Pfad
        path.add(0, v);
        return path;
    }

    public Set<V> getAllNodes() {
        return Collections.unmodifiableSet(dist.keySet());
    }

    public void print() {
        for (V i : dist.keySet()) {
            for (V j : dist.keySet())
                if (dist.get(i).get(j).equals(Double.POSITIVE_INFINITY))
                    System.out.printf("%4s", "-");
                else
                    System.out.printf("%4.0f", dist.get(i).get(j));
            System.out.println();
        }
        System.out.println();
    }

    void saveAsCsv(String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            List<V> nodes = new ArrayList<>(dist.keySet());
            // Header
            writer.print(";");
            for (V w : nodes)
                writer.print(w + ";");
            writer.println();
            // Rows
            for (V v : nodes) {
                writer.print(v + ";");
                for (V w : nodes) {
                    double d = dist.get(v).get(w);
                    if (d == Double.POSITIVE_INFINITY)
                        writer.print("-;");
                    else
                        writer.print(String.format("%.0f;", d));
                }
                writer.println();
            }
            writer.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}