package dictionary;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class LinkedHashDictionary<K, V> implements Dictionary<K, V> {

    private Node<K, V>[] table;
    private int size;

    /**
     * Constructs an empty Hash table with default initial capacity of 31.
     */
    public LinkedHashDictionary() {
        this(31);
    }

    /**
     * Constructs an empty Hash table with initial capacity of at least n.
     * @param n initial capacity
     */
    @SuppressWarnings("unchecked")
    public LinkedHashDictionary(int n) {
        int capacity = nextPrime(n);
        table = (Node<K, V>[]) new Node[capacity];
        size = 0;
    }

    private static class Node<K, V> extends Dictionary.Entry<K, V> {
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            super(key, value);
            this.next = next;
        }
    }

    //übernommen aus 2-12
    private int hash(K key) {
        int h = key.hashCode();
        if (h < 0)
            h = -h;
        return h % table.length;
    }

    //Implementiert nach 2-17
    @Override
    public V insert(K key, V value) {
        // suche in tab[ h(key) ] nach Schlüssel key;
        int h = hash(key);
        Node<K, V> current = table[h];

        while (current != null) {
            if (current.getKey().equals(key)) {
                // Schlüssel bereits vorhanden;
                // alte Daten durch value ersetzen;
                V oldValue = current.getValue();
                current.setValue(value);
                return oldValue;
            }
            current = current.next;
        }

        // füge key am Anfang der Liste ein;
        table[h] = new Node<>(key, value, table[h]);
        size++;

        // falls Füllungsgrad > 2, dann Tabelle vergrößern und umkopieren
        if ((double) size / table.length > 2.0) {
            rehash();
        }

        return null;
    }



    //Implementiert wie 2-17
    @Override
    public V search(K key) {
        int h = hash(key);
        Node<K, V> current = table[h];

        // suche in tab[h] nach Schlüssel key;
        while (current != null) {
            if (current.getKey().equals(key))
                return current.getValue();
            current = current.next;
        }
        return null;
    }



    //Implementiert wie 2-17
    @Override
    public V remove(K key) {
        int h = hash(key);
        Node<K, V> current = table[h];
        Node<K, V> prev = null;

        // suche in tab[h] nach Schlüssel key;
        while (current != null) {
            if (current.getKey().equals(key)) {
                // entferne Knoten k aus Liste;
                if (prev == null)
                    table[h] = current.next;
                else
                    prev.next = current.next;
                size--;
                return current.getValue();
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    //javadoc
    @Override
    public int size() {
        return size;
    }


    /////////
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            int bucket = 0; //aktueller Index im table[]
            Node<K, V> current = null; //aktuelles Listenelement im Bucket

            @Override
            public boolean hasNext() {
                while (current == null && bucket < table.length) {
                    current = table[bucket++]; //gehe zum nächsten bucket, bis etwas gefunden wird
                }
                return current != null;
            }

            @Override
            public Entry<K, V> next() { //gibt aktuellen eintrag und springt zum nächsten knoten in der veketteten liste
                if (!hasNext())
                    throw new NoSuchElementException();
                Entry<K, V> e = current;
                current = current.next;
                return e;
            }
        };
    }

    //toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node<K, V> head : table) {
            Node<K, V> current = head;
            while (current != null) {
                sb.append(current.getKey()).append(": ").append(current.getValue()).append("\n");
                current = current.next;
            }
        }
        return sb.toString();
    }

    //z.B aus 20 wird 31, das brauchen wir, damit hashtabelle immer eine primzahlgröße hat
    private int nextPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }
    private boolean isPrime(int n) {
        if (n < 2)
            return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }


    //Implementiert nach 2-37
    @SuppressWarnings("unchecked")
    private void rehash() {
        Node<K, V>[] oldTable = table;
        int newCapacity = nextPrime(2 * oldTable.length);
        table = (Node<K, V>[]) new Node[newCapacity];
        size = 0;

        for (Node<K, V> head : oldTable) {
            Node<K, V> current = head;
            while (current != null) {
                // Einfügen beim Rehash erfolgt direkt ohne weitere Prüfung
                int h = hash(current.getKey());
                table[h] = new Node<>(current.getKey(), current.getValue(), table[h]);
                size++;
                current = current.next;
            }
        }
    }
}
