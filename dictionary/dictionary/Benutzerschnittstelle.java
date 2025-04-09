package dictionary;

import java.util.Scanner;
import java.io.*;



//Hinweis: So muss es für r angegeben werden: C:\Users\annaz\Desktop\Alda\dictionary\dictionary\dtengl.txt




public class Benutzerschnittstelle {

    private static Dictionary<String, String> dict;

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Wörterbuch gestartet. Tippe 'exit' zum Beenden.");

        while (true) {
            System.out.print("> ");
            String cmd = sc.nextLine().trim(); //liest text ein, entfernt leerzeichen am anfang u ende
            if (cmd.equals("exit")) break;

            if (cmd.startsWith("create")) {
                if (cmd.contains("SortedArray")) dict = new SortedArrayDictionary<>();
                else if (cmd.contains("LinkedHash")) dict = new LinkedHashDictionary<>(31);
                else if (cmd.contains("OpenHash")) dict = new OpenHashDictionary<>(31);
                else if (cmd.contains("BinaryTree")) dict = new BinaryTreeDictionary<>();
                else System.out.println("Unbekannter Typ.");
            }//////
            else if (cmd.startsWith("r ")) {
                String[] parts = cmd.split(" "); //Text wird in einzelne Wörter aufgeteilt
                int count = 0; //Wie viele Wörterpaare wurden schon eingefügt
                int start = 1; //Position in parts, wo der Dateipfad steht
                int max = Integer.MAX_VALUE; //Maximale Anzahl an Wörtern, die eingelesen werden dürfen
                try { //Ist das, was in parts 1 steht, wirklich eine Zahl?
                    max = Integer.parseInt(parts[1]);
                    start = 2;
                } catch (Exception ignored) {}
                BufferedReader reader = new BufferedReader(new FileReader(parts[start])); //Fatei mit dem Pfad öffnen, die in parts start steht
                String line;
                while ((line = reader.readLine()) != null && count < max) {
                    String[] pair = line.strip().split(" ");
                    if (pair.length == 2) {
                        dict.insert(pair[0], pair[1]);
                        count++;
                    }
                }
                reader.close();
                System.out.println(count + " Einträge geladen.");
            }//einfach print schleife für alles
            else if (cmd.equals("p")) {
                for (var e : dict) {
                    System.out.println(e.getKey() + ": " + e.getValue());
                }
            }
            else if (cmd.startsWith("s ")) {
                String key = cmd.substring(2); //entfernt s am anfang
                String val = dict.search(key);
                System.out.println(val != null ? key + " -> " + val : "Nicht gefunden.");
            }
            else if (cmd.startsWith("i ")) {
                String[] pair = cmd.substring(2).split(" ", 2); //entfernt ersten 2 zeichen, also i und leerzeichen
                if (pair.length == 2) {
                    dict.insert(pair[0], pair[1]);
                    System.out.println("Hinzugefügt.");
                } else {
                    System.out.println("Format: i deutsch englisch");
                }
            }
            else if (cmd.startsWith("d ")) {
                String key = cmd.substring(2);
                String val = dict.remove(key);
                System.out.println(val != null ? "Gelöscht: " + key : "Nicht vorhanden.");
            }
            else {
                System.out.println("Unbekannter Befehl.");
            }
        }
        sc.close();
    }
}
