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
            parent.put(element, element); // jedes element ist zunächst sein eigener elternknoten
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
        // ...
        if (!parent.containsKey(e))
            throw new IllegalArgumentException("Element nicht enthalten: " + e);

        T p = parent.get(e); // hole aktuellen Elternknoten p
        if (!p.equals(e)) { // falls e nicht Wurzel ist
            T root = find(p); // gehe zur wurzel
            parent.put(e, root); // verlinke e direkt mit der wurzel, Pfadkompression
            return root;
        }
        return p; // e ist schon die wurzel
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
        // ...
        if (!parent.containsKey(e1) || !parent.containsKey(e2))
            throw new IllegalArgumentException("Element(e) nicht enthalten");

        T root1 = find(e1);
        T root2 = find(e2);

        if (root1.equals(root2))
            return; // Bereits vereint

        // Vergleich ranking beider bäume
        int rank1 = rank.get(root1);
        int rank2 = rank.get(root2);

        // Kleineren Baum an größeren anhängen
        if (rank1 < rank2) {
            parent.put(root1, root2);
        } else if (rank1 > rank2) {
            parent.put(root2, root1);
        } else { // bei gleichheit frei wählbar wen man unter wen hängt
            parent.put(root2, root1);
            rank.put(root1, rank1 + 1); // root 1 ist jetzt eine ebene höher
        }

        size--; // Zwei gruppen zu einer geworden

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