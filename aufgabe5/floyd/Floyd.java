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
                    // Distanz von einem Knoten zu sich selbst ist 0
                    dist.get(v).put(w, 0.0);
                    pred.get(v).put(w, null);
                } else if (g.containsEdge(v, w)) {
                    // Direkte Kante vorhanden
                    dist.get(v).put(w, g.getWeight(v, w));
                    pred.get(v).put(w, v);
                } else {
                    // Keine direkte Kante
                    dist.get(v).put(w, Double.POSITIVE_INFINITY);
                    pred.get(v).put(w, null);
                }
            }
        }

        // Floyd-Warshall Algorithmus
        // Für jeden Zwischenknoten k
        for (V k : g.getVertexSet()) {
            // Für jeden Startknoten i
            for (V i : g.getVertexSet()) {
                // Für jeden Zielknoten j
                for (V j : g.getVertexSet()) {
                    double currentDist = dist.get(i).get(j);
                    double newDist = dist.get(i).get(k) + dist.get(k).get(j);

                    // Wenn der Weg über k kürzer ist
                    if (newDist < currentDist) {
                        dist.get(i).put(j, newDist);
                        pred.get(i).put(j, pred.get(k).get(j));
                    }
                }
            }
        }

        // Überprüfung auf negative Zyklen (optional)
        for (V v : g.getVertexSet()) {
            if (dist.get(v).get(v) < 0) {
                throw new IllegalArgumentException("Graph enthält einen negativen Zyklus!");
            }
        }
    }

    public double getDist(V v, V w) {
        return dist.get(v).get(w);
    }

    public List<V> getShortestPath(V v, V w) {
        List<V> path = new LinkedList<>();

        // Kein Weg vorhanden oder Startknoten = Zielknoten
        if (dist.get(v).get(w).equals(Double.POSITIVE_INFINITY)) {
            return path; // Leere Liste zurückgeben
        }

        if (v.equals(w)) {
            return path; // Leere Liste für gleiche Knoten
        }

        // Rekonstruktion des Pfades rückwärts
        V current = w;
        while (current != null && !current.equals(v)) {
            path.add(0, current); // Am Anfang einfügen
            current = pred.get(v).get(current);
        }

        // Wenn current null ist, gibt es keinen Pfad
        if (current == null) {
            return new LinkedList<>(); // Leere Liste zurückgeben
        }

        // Startknoten hinzufügen
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

            // Header-Zeile schreiben (erste Spalte leer, dann alle Knotennamen)
            writer.print(";");
            for (V node : nodes) {
                writer.print(node + ";");
            }
            writer.println();

            // Datenzeilen schreiben
            for (V fromNode : nodes) {
                writer.print(fromNode + ";"); // Zeilenbeschriftung

                for (V toNode : nodes) {
                    double distance = dist.get(fromNode).get(toNode);
                    if (distance == Double.POSITIVE_INFINITY) {
                        writer.print("-;");
                    } else {
                        writer.print(String.format("%.0f;", distance));
                    }
                }
                writer.println();
            }

            writer.close();
            System.out.println("CSV-Datei erfolgreich gespeichert: " + filename);

        } catch (java.io.FileNotFoundException e) {
            System.out.println("Fehler beim Speichern der Datei: " + e.getMessage());
        }
    }
}