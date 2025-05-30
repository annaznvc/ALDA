// O. Bittel
// 20.02.2025

package aufgabe4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Klasse fÃ¼r Union-Find-Strukturen.
 * UnterstÃ¼tzt die effiziente Verwaltung einer Partionierung einer Menge
 * (disjunkte Zerlegung in Teilmengen).
 *
 * Union-Find-Struktur mit Pfadkompression und Union-by-Rank.
 *
 * Im Durchschnitt haben unionByRank und findWithCompression praktisch eine
 * Laufzeit von O(1).
 *
 * @author Oliver Bittel
 * @since 24.01.2025
 */
public class UnionFind<T> {
    // ...
    // Seite 14-19: Sind Eltemente von einem beliebigen Typ, dann kann fürs
    // Elternfeld ne Map verwendet werden
    private final Map<T, T> parent = new HashMap<>(); // für jedes element elternknoten speichern
    private final Map<T, Integer> rank = new HashMap<>(); // hilfswert: welcher baum ist höher?
    private int size;

    /**
     * Legt eine neue Union-Find-Struktur mit allen 1-elementigen Teilmengen von s
     * an.
     * 
     * @param s Menge von Elementen, fÃ¼r die eine Partionierung verwaltet werden
     *          soll.
     *
     */
    public UnionFind(Set<T> s) {
        // ...
        for (T element : s) {
            // parent.put(element, element); // jedes element ist zunächst sein eigener
            // elternknoten
            rank.put(element, 0); // anfangs ist jeder baum 0 hoch
        }
        size = s.size(); // Anfangs ist Anzahl Teilbäume = Anzahl Elemente
    }

    /**
     * Liefert den ReprÃ¤sentanten der Teilmenge zurÃ¼ck, zu der e gehÃ¶rt.
     * Pfadkompression wird angewendet.
     * Finde den Repräsentanten (Wurzel) der Teilmenge, zu der e gehört
     * 
     * @param e Element
     * @throws IllegalArgumentException falls e nicht in der Partionierung vorkommt.
     * @return ReprÃ¤sentant der Teilmenge, zu der e gehÃ¶rt.
     */

    public T find(T e) {
        // Initialisiere das Element dynamisch, falls es nicht existiert
        parent.putIfAbsent(e, e);
        rank.putIfAbsent(e, 0);

        // Pfadkompression durchführen
        if (!parent.get(e).equals(e)) {
            parent.put(e, find(parent.get(e)));
        }

        return parent.get(e); // Gib die Wurzel zurück
    }

    /**
     * Vereinigt die beiden Teilmengen, die e1 bzw. e2 enthalten.
     * Die Vereinigung wird nur durchgefÃ¼hrt,
     * falls die beiden Mengen unterschiedlich sind.
     * Es wird union-by-rank durchgefÃ¼hrt.
     * 
     * @param e1 Element.
     * @param e2 Element.
     * @throws IllegalArgumentException falls e1 und e2 keine Elemente der
     *                                  Union-Find-Struktur sind.
     */
    public void union(T e1, T e2) {
        // Dynamically initialize elements if they are not present
        parent.putIfAbsent(e1, e1);
        rank.putIfAbsent(e1, 0);
        parent.putIfAbsent(e2, e2);
        rank.putIfAbsent(e2, 0);

        // Finde die Wurzeln der beiden Elemente
        T root1 = find(e1);
        T root2 = find(e2);

        // Wenn beide Elemente bereits in derselben Teilmenge sind, nichts tun
        if (root1.equals(root2))
            return;

        // Vergleiche die Ränge der beiden Bäume
        int rank1 = rank.get(root1);
        int rank2 = rank.get(root2);

        // Hänge den kleineren Baum unter den größeren
        if (rank1 < rank2) {
            parent.put(root1, root2);
        } else if (rank1 > rank2) {
            parent.put(root2, root1);
        } else {
            // Bei gleicher Höhe: Wähle einen beliebigen und erhöhe den Rang
            parent.put(root2, root1);
            rank.put(root1, rank1 + 1);
        }

        // Reduziere die Anzahl der Teilmengen
        size--;
    }

    /**
     * Ausgabe der Union-Find-Struktur zu Testzwecken.
     */
    public void print() {
        // ...
        System.out.println("Union-Find Struktur:");
        for (T element : parent.keySet()) {
            System.out.println(element + " -> " + parent.get(element));
        }

    }

    /**
     * Liefert die Anzahl der Teilmengen in der Partitionierung zurÃ¼ck.
     * 
     * @return Anzahl der Teilmengen.
     */
    public int size() {
        // ...
        return size;
    }

    public static void main(String[] args) {
        Set<Integer> s = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        UnionFind<Integer> uf = new UnionFind<>(s);
        uf.union(1, 2);
        uf.print();
        uf.union(3, 4);
        uf.print();
        uf.union(2, 4);
        uf.print();
        uf.find(4);
        uf.print();
        uf.union(9, 10);
        uf.union(7, 8);
        uf.print();
        uf.union(7, 9);
        uf.print();
        uf.union(1, 7);
        uf.print();
        uf.find(10);
        uf.print();
    }
}