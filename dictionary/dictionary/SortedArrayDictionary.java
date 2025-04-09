package dictionary;


import java.util.Comparator;
import java.util.Iterator;


//Implementation of the Dictionary interface as sorted array.

public class SortedArrayDictionary<K extends Comparable<K>, V> implements Dictionary<K, V> {


    private Entry<K, V>[] data;
    private int size;
    private Comparator<? super K> cmp; // optionaler Comparator zur Sortierung


    //Constructs an empty array with an initial capacity sufficient to hold 32 entries. Entries are sorted using the natural ordering of the keys.
    @SuppressWarnings("unchecked")
    public SortedArrayDictionary(){
        data = new Entry[32];
        size = 0;
        cmp = null; // natürliche Ordnung verwenden
    }


    //Constructs an empty array with an initial capacity sufficient to hold 32 entries.
    @SuppressWarnings("unchecked")
    public SortedArrayDictionary(Comparator<? super K> cmp) {
        data = new Entry[32];
        size = 0;
        this.cmp = cmp;
    }


    //Übernommen aus Vorlesung 1-21
    @Override
    public V insert(K key, V value) {
        int i = searchKey(key); // Position des Schlüssels (falls vorhanden)


        // Vorhandener Eintrag wir überschrieben
        if (i != -1) {
            V oldValue = data[i].getValue();
            data[i].setValue(value);
            return oldValue;
        }


        // Neueintrag:
        if (data.length == size) {
            grow(); // Array vergrößern, wie bei ArrayDictionary
        }


        int j = size - 1;
        while (j >= 0 && compare(key, data[j].getKey()) < 0) {
            data[j + 1] = data[j]; // nach rechts verschieben
            j--;
        }


        data[j + 1] = new Dictionary.Entry<>(key, value);
        size++;
        return null;
    }


    //Übernommen aus Volresung 1-20 ----gibt Index des Schlüssels zurück
    private int searchKey(K key) {
        int li = 0;
        int re = size - 1;


        while (re >= li) {
            int m = (li + re) / 2;
            int cmp = compare(key, data[m].getKey());
            if (cmp < 0)
                re = m - 1;
            else if (cmp > 0)
                li = m + 1;
            else
                return m; // key gefunden
        }


        return -1; // key nicht gefunden
    }


    // Übernommen aus 1-16 --- Gibt Wert des Schlüssels zurück
    @Override
    public V search(K key) {
        int i = searchKey(key);
        if (i == -1)
            return null;
        return data[i].getValue();
    }


    //wenn Kapazität des Arrays überschritten wird, verdopeln wir
    @SuppressWarnings("unchecked")
    private void grow() {
        Dictionary.Entry<K, V>[] newData =
                (Dictionary.Entry<K, V>[]) new Dictionary.Entry[2 * data.length];
        for (int i = 0; i < size; i++)
            newData[i] = data[i];
        data = newData;
    }


    //übernommen aus 1-18
    @Override
    public V remove(K key) {
        int i = searchKey(key);
        if (i == -1)
            return null;


        V oldValue = data[i].getValue();
        for (int j = i; j < size - 1; j++) {
            data[j] = data[j + 1]; // Lücke schließen
        }
        data[--size] = null; // letztes Element löschen
        return oldValue;
    }


    //siehe javadoc
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(data[i].getKey()).append(": ").append(data[i].getValue()).append("\n");
        }
        return sb.toString();
    }


    @Override
    public int size() {
        return size;
    }


    //siehe javadoc: Returns an iterator over the elements in this dictionary in ascending order.
    @Override
    public Iterator<Dictionary.Entry<K, V>> iterator() {
        return new Iterator<>() {
            private int current = 0; //current zeigt auf aktuellen Eintrag im Array


            @Override
            public boolean hasNext() { //prüfen, obs noch was gibt
                return current < size;
            }


            @Override
            public Dictionary.Entry<K, V> next() { //gibt aktuellen Eintrag zurück und geht eins weiter
                return data[current++];
            }
        };
    }


    //Vergleicht zwei Schlüssel, entweder mit Comparator oder natürlicher Ordnung
    private int compare(K key1, K key2) {
        if (cmp != null) {
            return cmp.compare(key1, key2);
        }
        return key1.compareTo(key2);
    }


}

