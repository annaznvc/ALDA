/*
 * Test der verscheidenen Dictionary-Implementierungen
 *
 * O. Bittel
 * 29.11.2024
 */
package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Static test methods for different Dictionary implementations.
 * @author oliverbittel
 */
public class DictionaryTest {

	/**
	 * @param args not used.
	 */
	public static void main(String[] args)  {
		//testSortedArrayDictionary();
		testLinkedHashDictionary();
		testOpenHashWithQuadraticProbingDictionary();
		testBinaryTreeDictionary();

		//cpuTime();
	}

	private static void cpuTime() {
		String filePath = "C:\\Users\\annaz\\Desktop\\Alda\\dictionary\\dictionary\\dtengl.txt";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			List<String> germanWords = new ArrayList<>();
			List<String> englishWords = new ArrayList<>();

			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.strip().split("\\s+");
				if (parts.length >= 2) {
					germanWords.add(parts[0]);
					englishWords.add(parts[1]);
				}
			}
			reader.close();

			int n = germanWords.size();
			int count = 8000;

			// ------------------- LinkedHashDictionary -------------------
			System.out.println("\nLinkedHashDictionary cpuTime");
			Dictionary<String, String> linked = new LinkedHashDictionary<>();

			System.out.println("Einlesen von " + count + " Wörtern");
			long start = System.nanoTime();
			for (int i = 0; i < count; i++) {
				linked.insert(germanWords.get(i), englishWords.get(i));
			}
			double insertTime = (System.nanoTime() - start) / 1_000_000.0;
			System.out.println("Einfügezeit: " + insertTime + " ms");

			long successTime = 0;
			for (int i = 0; i < count; i++) {
				long t0 = System.nanoTime();
				linked.search(germanWords.get(i));
				successTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d vorhandenen Wörtern: %.4f ms%n", count, successTime / 1_000_000.0);

			long failTime = 0;
			for (int i = 0; i < count; i++) {
				long t0 = System.nanoTime();
				linked.search("nichtGefunden_" + i);
				failTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d nicht vorhandenen Wörtern: %.4f ms%n%n", count, failTime / 1_000_000.0);

			Dictionary<String, String> linkedFull = new LinkedHashDictionary<>();
			System.out.println("Einlesen von " + n + " Wörtern");
			start = System.nanoTime();
			for (int i = 0; i < n; i++) {
				linkedFull.insert(germanWords.get(i), englishWords.get(i));
			}
			insertTime = (System.nanoTime() - start) / 1_000_000.0;
			System.out.println("Einfügezeit: " + insertTime + " ms");

			successTime = 0;
			for (int i = 0; i < n; i++) {
				long t0 = System.nanoTime();
				linkedFull.search(germanWords.get(i));
				successTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d vorhandenen Wörtern: %.4f ms%n", n, successTime / 1_000_000.0);

			failTime = 0;
			for (int i = 0; i < n; i++) {
				long t0 = System.nanoTime();
				linkedFull.search("nichtGefunden_" + i);
				failTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d nicht vorhandenen Wörtern: %.4f ms%n%n", n, failTime / 1_000_000.0);

			// ------------------- OpenHashDictionary -------------------
			System.out.println("OpenHashDictionary cpuTime");
			Dictionary<String, String> open = new OpenHashDictionary<>();

			System.out.println("Einlesen von " + count + " Wörtern");
			start = System.nanoTime();
			for (int i = 0; i < count; i++) {
				open.insert(germanWords.get(i), englishWords.get(i));
			}
			insertTime = (System.nanoTime() - start) / 1_000_000.0;
			System.out.println("Einfügezeit: " + insertTime + " ms");

			successTime = 0;
			for (int i = 0; i < count; i++) {
				long t0 = System.nanoTime();
				open.search(germanWords.get(i));
				successTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d vorhandenen Wörtern: %.4f ms%n", count, successTime / 1_000_000.0);

			failTime = 0;
			for (int i = 0; i < count; i++) {
				long t0 = System.nanoTime();
				open.search("nichtGefunden_" + i);
				failTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d nicht vorhandenen Wörtern: %.4f ms%n%n", count, failTime / 1_000_000.0);

			Dictionary<String, String> openFull = new OpenHashDictionary<>();
			System.out.println("Einlesen von " + n + " Wörtern");
			start = System.nanoTime();
			for (int i = 0; i < n; i++) {
				openFull.insert(germanWords.get(i), englishWords.get(i));
			}
			insertTime = (System.nanoTime() - start) / 1_000_000.0;
			System.out.println("Einfügezeit: " + insertTime + " ms");

			successTime = 0;
			for (int i = 0; i < n; i++) {
				long t0 = System.nanoTime();
				openFull.search(germanWords.get(i));
				successTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d vorhandenen Wörtern: %.4f ms%n", n, successTime / 1_000_000.0);

			failTime = 0;
			for (int i = 0; i < n; i++) {
				long t0 = System.nanoTime();
				openFull.search("nichtGefunden_" + i);
				failTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d nicht vorhandenen Wörtern: %.4f ms%n%n", n, failTime / 1_000_000.0);

			// ------------------- BinaryTreeDictionary -------------------
			System.out.println("BinaryTreeDictionary cpuTime");
			Dictionary<String, String> tree = new BinaryTreeDictionary<>();

			System.out.println("Einlesen von " + count + " Wörtern");
			start = System.nanoTime();
			for (int i = 0; i < count; i++) {
				tree.insert(germanWords.get(i), englishWords.get(i));
			}
			insertTime = (System.nanoTime() - start) / 1_000_000.0;
			System.out.println("Einfügezeit: " + insertTime + " ms");

			successTime = 0;
			for (int i = 0; i < count; i++) {
				long t0 = System.nanoTime();
				tree.search(germanWords.get(i));
				successTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d vorhandenen Wörtern: %.4f ms%n", count, successTime / 1_000_000.0);

			failTime = 0;
			for (int i = 0; i < count; i++) {
				long t0 = System.nanoTime();
				tree.search("nichtGefunden_" + i);
				failTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d nicht vorhandenen Wörtern: %.4f ms%n%n", count, failTime / 1_000_000.0);

			Dictionary<String, String> treeFull = new BinaryTreeDictionary<>();
			System.out.println("Einlesen von " + n + " Wörtern");
			start = System.nanoTime();
			for (int i = 0; i < n; i++) {
				treeFull.insert(germanWords.get(i), englishWords.get(i));
			}
			insertTime = (System.nanoTime() - start) / 1_000_000.0;
			System.out.println("Einfügezeit: " + insertTime + " ms");

			successTime = 0;
			for (int i = 0; i < n; i++) {
				long t0 = System.nanoTime();
				treeFull.search(germanWords.get(i));
				successTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d vorhandenen Wörtern: %.4f ms%n", n, successTime / 1_000_000.0);

			failTime = 0;
			for (int i = 0; i < n; i++) {
				long t0 = System.nanoTime();
				treeFull.search("nichtGefunden_" + i);
				failTime += System.nanoTime() - t0;
			}
			System.out.printf("Suchen von %d nicht vorhandenen Wörtern: %.4f ms%n", n, failTime / 1_000_000.0);

		} catch (IOException e) {
			System.out.println("Fehler beim Einlesen der Datei: " + e.getMessage());
		}
	}







	private static void testSortedArrayDictionary() {
		Dictionary<String, String> dict = new SortedArrayDictionary<>();
		testDict(dict);
	}

	private static void testLinkedHashDictionary() {
		Dictionary<String, String> dict = new LinkedHashDictionary<>(7);
		testDict(dict);
	}

	private static void testOpenHashWithQuadraticProbingDictionary() {
		Dictionary<String, String> dict = new OpenHashDictionary<>(7);
		testDict(dict);
	}

	private static void testBinaryTreeDictionary() {
		Dictionary<String, String> dict = new BinaryTreeDictionary<>();
		testDict(dict);

		BinaryTreeDictionary<Integer, Integer> btd = new BinaryTreeDictionary<>();
		btd.insert(10, 0);
		btd.insert(20, 0);
		btd.insert(50, 0);
		System.out.println("insert:");
		btd.prettyPrint();

		btd.insert(40, 0);
		btd.insert(30, 0);
		System.out.println("insert:");
		btd.prettyPrint();

		btd.insert(21, 0);
		System.out.println("insert:");
		btd.prettyPrint();

		btd.insert(35, 0);
		btd.insert(45, 0);
		System.out.println("insert:");
		btd.prettyPrint();

		System.out.println("For Each Loop:");
		for (Dictionary.Entry<Integer, Integer> e : btd) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}

		btd.remove(30);
		System.out.println("remove:");
		btd.prettyPrint();

		btd.remove(35);
		btd.remove(40);
		btd.remove(45);
		System.out.println("remove:");
		btd.prettyPrint();

		btd.remove(50);
		System.out.println("remove:");
		btd.prettyPrint();

		System.out.println("For Each Loop:");
		for (Dictionary.Entry<Integer, Integer> e : btd) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}

	private static void testDict(Dictionary<String, String> dict) {
		System.out.println("===== New Test Case ========================");
		System.out.println("test " + dict.getClass());
		System.out.println(dict.insert("gehen", "go") == null);		// true
		String s = new String("gehen");
		System.out.println(dict.search(s) != null);					// true
		System.out.println(dict.search(s).equals("go"));			// true
		System.out.println(dict.insert(s, "walk").equals("go"));	// true
		System.out.println(dict.search("gehen").equals("walk"));	// true
		System.out.println(dict.remove("gehen").equals("walk"));	// true
		System.out.println(dict.remove("gehen") == null); 			// true

		dict.insert("starten", "start");
		dict.insert("gehen", "go");
		dict.insert("schreiben", "write");
		dict.insert("reden", "say");
		dict.insert("arbeiten", "work");
		dict.insert("lesen", "read");
		dict.insert("singen", "sing");
		dict.insert("schwimmen", "swim");
		dict.insert("rennen", "run");
		dict.insert("beten", "pray");
		dict.insert("tanzen", "dance");
		dict.insert("schreien", "cry");
		dict.insert("tauchen", "dive");
		dict.insert("fahren", "drive");
		dict.insert("spielen", "play");
		dict.insert("planen", "plan");
		dict.insert("diskutieren", "discuss");

		System.out.println(dict.size());
		for (Dictionary.Entry<String, String> e : dict) {
			System.out.println(e.getKey() + ": " + e.getValue() + " search: " + dict.search(e.getKey()));
		}
	}
}
