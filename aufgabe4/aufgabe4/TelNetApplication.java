package aufgabe4;

public class TelNetApplication {
    public static void main(String[] args) {
        // testBeispielnetz(); // Aufgabe 4
        testGrossesNetz(); // Aufgabe 5
    }

    public static void testBeispielnetz() {
        TelNet netz = new TelNet(7);

        netz.addTelKnoten(1, 1);
        netz.addTelKnoten(3, 1);
        netz.addTelKnoten(4, 2);
        netz.addTelKnoten(3, 4);
        netz.addTelKnoten(2, 6);
        netz.addTelKnoten(7, 6);
        netz.addTelKnoten(4, 7);

        boolean ok = netz.computeOptTelNet(); // Kruskal Algorithmus starten um MST zu berechnen
        System.out.println("Beispielnetz MST gefunden: " + ok);
        System.out.println("Gesamtkosten: " + netz.getOptTelNetKosten());

        for (TelVerbindung v : netz.getOptTelNet()) {
            System.out.println(v);
        }

        netz.drawOptTelNet(8, 8);
    }

    public static void testGrossesNetz() {
        TelNet netz = new TelNet(100);
        netz.generateRandomTelNet(1000, 1000, 1000);

        boolean ok = netz.computeOptTelNet();
        System.out.println("Großes Netz MST gefunden: " + ok);
        System.out.println("Kosten: " + netz.getOptTelNetKosten());

        netz.drawOptTelNet(1000, 1000);
    }
}
