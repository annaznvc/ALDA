package aufgabe4;

import java.util.*;

public class TelNet {
    private final int lbg; // Leitungsbegrenzung, maximale Distanz zwischen zwei direkt verbindbaren Knoten
    private final List<TelKnoten> knotenListe = new ArrayList<>(); // alle hinzugefügten knoten speichern
    private List<TelVerbindung> mst = null; // speichert verbindungen des minimalen spannbaums
    private int kosten = 0; // gesamtkosten der mst

    public TelNet(int lbg) {
        this.lbg = lbg;
    }

    /**
     * Fügt einen neuen Knoten mit den Koordinaten (x,y) hinzu. Nur wenn er noch
     * nicht in der Liste ist.
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return true, wenn der Knoten erfolgreich hinzugefügt wurde, false, wenn er
     *         bereits existiert.
     */
    public boolean addTelKnoten(int x, int y) {
        TelKnoten neu = new TelKnoten(x, y);
        if (knotenListe.contains(neu))
            return false;
        knotenListe.add(neu);
        return true;
    }

    /**
     * Berechnet den minimalen Spannbaum (MST) des Netzes mit dem Algorithmus von
     * Kruskal.
     * 
     * @return true, wenn ein MST gefunden wurde, false, wenn das Netz nicht
     *         zusammenhängend ist.
     */
    public boolean computeOptTelNet() {
        mst = new ArrayList<>();
        kosten = 0;

        // Alle gültigen Verbindungen erzeugen
        PriorityQueue<TelVerbindung> pq = new PriorityQueue<>(Comparator.comparingInt(TelVerbindung::c)); // Bernindungen
                                                                                                          // aufsteigend
                                                                                                          // nach ihren
                                                                                                          // kosten c
        // soriteren
        for (int i = 0; i < knotenListe.size(); i++) {
            TelKnoten u = knotenListe.get(i); // aktueller Knoten
            for (int j = i + 1; j < knotenListe.size(); j++) {
                TelKnoten v = knotenListe.get(j); // nächster Knoten
                int dist = Math.abs(u.x() - v.x()) + Math.abs(u.y() - v.y()); // Manhattan-Distanz
                if (dist <= lbg) {
                    pq.add(new TelVerbindung(u, v, dist)); // Verbindung hinzufügen, wenn sie innerhalb der lbg ist
                }
            }
        }

        // Union-Find initialisieren
        UnionFind<TelKnoten> uf = new UnionFind<>(new HashSet<>(knotenListe));

        // Kruskal ausführen
        while (!pq.isEmpty() && mst.size() < knotenListe.size() - 1) { // günstigste verbidnugn aus der priority queue
            TelVerbindung verbindung = pq.poll();
            TelKnoten u = verbindung.u();
            TelKnoten v = verbindung.v();
            if (!uf.find(u).equals(uf.find(v))) { // wenn die beiden Knoten nicht im selben Baum sind
                uf.union(u, v); // sie vereinigen
                mst.add(verbindung); // Verbindung zum MST hinzufügen
                kosten += verbindung.c(); // Gesamtkosten erhöhen
            }
        }

        return mst.size() == knotenListe.size() - 1; // Überprüfen, ob der MST alle Knoten verbindet
    }

    // Liefert ein optimales Telefonnetz als Liste von Telefonverbindungen zurück.
    public List<TelVerbindung> getOptTelNet() {
        if (mst == null)
            throw new IllegalStateException("Zuerst computeOptTelNet() aufrufen.");
        return mst;
    }

    // liefert die Gesamtkosten eines optimalen Telefonnetzes zurück.
    public int getOptTelNetKosten() {
        if (mst == null)
            throw new IllegalStateException("Zuerst computeOptTelNet() aufrufen.");
        return kosten;
    }

    // Zeichnet das gefundene optimale Telefonnetz mit der Größe xMax*yMax in ein
    // Fenster.
    public void drawOptTelNet(int xMax, int yMax) { // größe des gitters angeben
        if (mst == null)
            throw new IllegalStateException("Zuerst computeOptTelNet() aufrufen.");

        StdDraw.setXscale(0, xMax);
        StdDraw.setYscale(0, yMax);
        StdDraw.clear();

        for (TelKnoten k : knotenListe) {
            StdDraw.filledCircle(k.x(), k.y(), 0.1);
        }

        for (TelVerbindung v : mst) {
            StdDraw.line(v.u().x(), v.u().y(), v.v().x(), v.v().y()); // linie zwischen den Knoten zeichnen
        }
    }

    // Erzeugt n zufällige Knoten, und platziere sie im Gitterbereich von 0 bis xMax
    // und yMax.
    public void generateRandomTelNet(int n, int xMax, int yMax) {
        Random rand = new Random();
        while (knotenListe.size() < n) {
            int x = rand.nextInt(xMax + 1); // +1, damit 0 bis n einschliesslich
            int y = rand.nextInt(yMax + 1);
            addTelKnoten(x, y); // neuen Telefonknoten hinzufügen zur liste
        }
    }

    public int size() {
        return knotenListe.size();
    }

    @Override
    public String toString() {
        return "TelNet mit " + knotenListe.size() + " Knoten.";
    }

}
