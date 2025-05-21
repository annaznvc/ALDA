package shortestPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Klasse für Scotland Yard Heuristik.
 * Bietet eine Methode estimatedCost an, die
 * die Distanz zweier Knoten im Scotland-Yard-Spielplan schätzt.
 * Die Heuristik wird für A* benötigt.
 *
 * @author Oliver Bittel
 * @since 30.06.2024
 */
public class ScotlandYardHeuristic implements Heuristic<Integer> {
    private Map<Integer, Point> coord; // Ordnet jedem Knoten seine Koordinaten zu

    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Der Konstruktor liest die (x,y)-Koordinaten (Pixelkoordinaten) aller Knoten
     * von der Datei ScotlandYard_Knoten.txt in eine Map ein.
     */
    public ScotlandYardHeuristic() throws FileNotFoundException {
        // Lese Koordinaten von ScotlandYard_Knoten.txt in eine Map.
        // ...
        coord = new HashMap<>();
        Scanner in = new Scanner(new File("data/ScotlandYard_Knoten.txt"));
        while (in.hasNextInt()) {
            int node = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            coord.put(node, new Point(x, y));
        }
        in.close();
    }

    /**
     * Liefert einen skalierten Euklidischen Abstand zwischen Knoten u und v zurück.
     * Da die Koordinaten von x und y in Pixeleinheiten sind, wird
     * der Euklidische Abstand mit einem Faktor zwischen 0.02 bis 0.1 skaliert.
     * 
     * @param u Knoten
     * @param v Knoten
     * @return skalierter Euklidischer Abstand als geschätze Kosten
     */
    public double estimatedCost(Integer u, Integer v) {
        // ...
        Point pu = coord.get(u);
        Point pv = coord.get(v);
        if (pu == null || pv == null)
            return 0.0;

        // Euklidischer Abstand
        double dx = pu.x - pv.x;
        double dy = pu.y - pv.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Skalorierung
        return dist * 0.02;
    }
}
