// O. Bittel;
// 2.8.2023

package directedGraph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Klasse zur Analyse von Web-Sites.
 *
 * @author Oliver Bittel
 * @since 30.10.2023
 */
public class AnalyzeWebSite {
    public static void main(String[] args) throws IOException {
        // Graph aus Website erstellen und ausgeben:
        //DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("data/WebSiteKlein");
        DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("data/WebSiteGross");
        System.out.println("Anzahl Seiten: \t" + webSiteGraph.getNumberOfVertexes());
        System.out.println("Anzahl Links: \t" + webSiteGraph.getNumberOfEdges());
        //System.out.println(webSiteGraph);

        // Starke Zusammenhangskomponenten berechnen und ausgeben
        StrongComponents<String> sc = new StrongComponents<>(webSiteGraph);
        System.out.println(sc.numberOfComp());
        //System.out.println(sc);

        // Page Rank ermitteln und Top-100 ausgeben
        pageRank(webSiteGraph);
    }

    /**
     * Liest aus dem Verzeichnis dirName alle Web-Seiten und
     * baut aus den Links einen gerichteten Graphen.
     *
     * @param dirName Name eines Verzeichnis
     * @return gerichteter Graph mit Namen der Web-Seiten als Knoten und Links als gerichtete Kanten.
     */
    private static DirectedGraph buildGraphFromWebSite(String dirName) throws IOException {
        File webSite = new File(dirName);
        DirectedGraph<String> webSiteGraph = new AdjacencyListDirectedGraph();

        for (File f : webSite.listFiles()) {
            String from = f.getName();
            LineNumberReader in = new LineNumberReader(new FileReader(f));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("href")) {
                    String[] s_arr = line.split("\"");
                    String to = s_arr[1];
                    webSiteGraph.addEdge(from, to);
                }
            }
        }
        return webSiteGraph;
    }

    /**
     * pageRank ermittelt Gewichte (Ranks) von Web-Seiten
     * aufgrund ihrer Link-Struktur und gibt sie aus.
     *
     * @param g gerichteter Graph mit Web-Seiten als Knoten und Links als Kanten.
     */
    private static <V> void pageRank(DirectedGraph<V> g) {
    int nI = 10; // Anzahl der Iterationen
    double alpha = 0.5;

    // Definiere und initialisiere rankTable:
    // Ihr Code: ...
    Map<V, Double> rank = new HashMap<>();
    Map<V, Double> rankNew = new HashMap<>();

    int n = g.getNumberOfVertexes();
    double initialRank = 1.0 / n;

    for (V v : g.getVertexSet()) {
        rank.put(v, initialRank);
    }

    // Iteration:
    // Ihr Code: ...
    for (int i = 0; i < nI; i++) {
        for (V v : g.getVertexSet()) {
            rankNew.put(v, (1.0 - alpha) / n); // Grundwert
        }

        for (V v : g.getVertexSet()) {
            Set<V> successors = g.getSuccessorVertexSet(v);
            if (!successors.isEmpty()) {
                double share = alpha * rank.get(v) / successors.size();
                for (V w : successors) {
                    rankNew.put(w, rankNew.get(w) + share);
                }
            }
        }

        // swap rank and rankNew
        Map<V, Double> temp = rank;
        rank = rankNew;
        rankNew = temp;
    }

    // Rank Table ausgeben (nur für data/WebSiteKlein):
    // Ihr Code: ...
    if (n <= 20) {
        System.out.println("\nPageRanks:");
        for (V v : rank.keySet()) {
            System.out.printf("%-25s %6.4f\n", v, rank.get(v));
        }
    }

    // Nach Ranks sortieren Top 100 ausgeben (nur für data/WebSiteGross):
    // Ihr Code: ...
    if (n > 20) {
        System.out.println("\nTop 100 Seiten nach PageRank:");
        rank.entrySet().stream()
            .sorted(Map.Entry.<V, Double>comparingByValue().reversed())
            .limit(100)
            .forEach(e -> System.out.printf("%-40s %6.4f\n", e.getKey(), e.getValue()));
    }

    // Top-Seite mit ihren Vorgängern und Ranks ausgeben (nur für data/WebSiteGross):
    // Ihr Code: ...
    if (n > 20) {
        V top = Collections.max(rank.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("\nTop-Seite: " + top + " mit PageRank " + rank.get(top));
        System.out.println("Vorgänger der Top-Seite:");
        for (V pred : g.getPredecessorVertexSet(top)) {
            System.out.printf("%-40s %6.4f\n", pred, rank.get(pred));
        }
    }
}

}
