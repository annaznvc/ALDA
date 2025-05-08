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

        DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("data/WebSiteKlein");
        // DirectedGraph<String> webSiteGraph =
        // buildGraphFromWebSite("data/WebSiteGross");

        // System.out.println("Anzahl Seiten: \t" + webSiteGraph.getNumberOfVertexes());
        // System.out.println("Anzahl Links: \t" + webSiteGraph.getNumberOfEdges());
        System.out.println(webSiteGraph);

        // Starke Zusammenhangskomponenten berechnen und ausgeben
        StrongComponents<String> sc = new StrongComponents<>(webSiteGraph);
        System.out.println(sc.numberOfComp());
        System.out.println(sc);

        // Page Rank ermitteln und Top-100 ausgeben
        pageRank(webSiteGraph);
    }

    /**
     * Liest aus dem Verzeichnis dirName alle Web-Seiten und
     * baut aus den Links einen gerichteten Graphen.
     *
     * @param dirName Name eines Verzeichnis
     * @return gerichteter Graph mit Namen der Web-Seiten als Knoten und Links als
     *         gerichtete Kanten.
     */
    private static DirectedGraph buildGraphFromWebSite(String dirName) throws IOException {

        File webSite = new File(dirName); // verzeichnis dirName öffnen
        DirectedGraph<String> webSiteGraph = new AdjacencyListDirectedGraph(); // neuen, leeren, gerichteten Graph

        for (File f : webSite.listFiles()) { // über jede Datei im Verzeichni iterieren
            String from = f.getName(); // Merke dir den Dateinamen

            // HTML Datei Zeile für Zeile lesen
            LineNumberReader in = new LineNumberReader(new FileReader(f));
            String line;
            while ((line = in.readLine()) != null)

            {
                if (line.contains("href")) { // href kommt nur in link-zeilen vor, also zeilen filtern, die nur einen
                                             // Link enthalten
                    String[] s_arr = line.split("\""); // Zeile an jedem Anführungszeichen aufteilene und Teile in eienm
                                                       // Array speichern
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
        Map<V, Double> rank = new HashMap<>(); // Für jede Seite v: Wie wichtig ist sie aktuell?
        Map<V, Double> rankNew = new HashMap<>(); // neue pagerank jede runde berechnet, bevor er am ende in rank
                                                  // übernommen wird

        int n = g.getNumberOfVertexes(); // zähle, wie viele knoten es im graphen gibt
        double initialRank = 1.0 / n; // anfangs soll jede seite gleich wichtig sein z.b 1.0/4 = 0.25

        for (V v : g.getVertexSet()) { // für jede seite im graphen
            rank.put(v, initialRank); // trage sie in die rank map ein, gib ihr den startwert initialrank
        }

        // Iteration:
        // Ihr Code: ...
        for (int i = 0; i < nI; i++) {
            for (V v : g.getVertexSet()) {
                rankNew.put(v, (1.0 - alpha) / n); // für jede seite um graphen: berechne den zufallsanteil
            }

            for (V v : g.getVertexSet()) { // verteilung über die links
                Set<V> successors = g.getSuccessorVertexSet(v);
                if (!successors.isEmpty()) {
                    double share = alpha * rank.get(v) / successors.size(); // berechne wie viel der aktuelle pager<nk
                                                                            // pro link weitergibt
                    for (V w : successors) { // jeder nachfolger bekommt seine anteil
                        rankNew.put(w, rankNew.get(w) + share); // seiten verteilen einen anteil ihres pageRanks an die
                                                                // Seiten, auf die sie verlinken
                    }
                }
            }

            // swap rank and rankNew, übernehme die neuen werte als aktuellen stand
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
        if (n > 20) { // nuur wenn graph höchstens 20 seiten hat, kleine websiten sind besser für
                      // guten überblick
            System.out.println("\nTop 100 Seiten nach PageRank:");
            rank.entrySet().stream()
                    .sorted(Map.Entry.<V, Double>comparingByValue().reversed())
                    .limit(100)
                    .forEach(e -> System.out.printf("%-40s %6.4f\n", e.getKey(), e.getValue()));
        }

        // Top-Seite mit ihren Vorgängern und Ranks ausgeben (nur für
        // data/WebSiteGross):
        // Ihr Code: ...
        if (n > 20) {
            V top = Collections.max(rank.entrySet(), Map.Entry.comparingByValue()).getKey(); // sucht seite mit dem
                                                                                             // höchsten wert in der
                                                                                             // rank map, gib nur
                                                                                             // seitennamen zurück
            System.out.println("\nTop-Seite: " + top + " mit PageRank " + rank.get(top));
            System.out.println("Vorgänger der Top-Seite:");
            for (V pred : g.getPredecessorVertexSet(top)) { // vorgänger der top seite anzeigen, zeige ihrne namen und
                                                            // wie viele pagerank sie selbst hat: wer trägt zur
                                                            // wichtigkeit der top seite bei?
                System.out.printf("%-40s %6.4f\n", pred, rank.get(pred));
            }
        }
    }

}
