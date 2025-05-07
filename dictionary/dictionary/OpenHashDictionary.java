package dictionary;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class OpenHashDictionary<K, V> implements Dictionary<K, V> {

    // Marker für gelöschte Einträge
    private final Entry<K, V> DELETED = new Entry<>(null, null);

    private Entry<K, V>[] tab;
    private int size;

    public OpenHashDictionary() {
        this(31);
    }

    @SuppressWarnings("unchecked")
    public OpenHashDictionary(int n) {
        int capacity = nextPrime4iPlus3(n);
        tab = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
    }

    // übernommen aus 2- erteugt hashwert für schlüssle key
    private int h(K key) {
        int adr = key.hashCode();
        if (adr < 0)
            adr = -adr;
        return adr % tab.length;
    }

    //////
    // Berechnet den alternierenden quadratischen Sondierungsversatz s(j)
    private int s(int j) {
        int sign = (j % 2 == 0) ? -1 : 1;
        int offset = (j + 1) / 2;
        return sign * offset * offset;
    }

    //übernommen aus 2-20 sucht speicheradresse eines bestimmten keys in der hashtabelle
    private int searchAdr(K key) {
        int j = 0;
        int adr;
        do {
            adr = (h(key) + s(j)) % tab.length;
            adr = Math.floorMod(h(key) + s(j), tab.length);
            if (adr < 0) adr += tab.length;
            if (tab[adr] == null)
                return -1;
            if (tab[adr] != DELETED && tab[adr].getKey().equals(key))
                return adr;
            j++;
        } while (j < tab.length);
        return -1;
    }

    // Übernommen aus 2-22
    @Override
    public V insert(K key, V value) {
        // Rehash falls nötig (siehe 2-36)
        if ((double) size / tab.length > 0.5)
            grow();

        // Suche nach existierender Adresse
        int adr = searchAdr(key);
        if (adr != -1) {
            V oldValue = tab[adr].getValue();
            tab[adr].setValue(value);
            return oldValue;
        }

        // Neueintrag
        int j = 0;
        do {
            adr = (h(key) + s(j)) % tab.length;
            if (adr < 0) adr += tab.length;
            if (tab[adr] == null || tab[adr] == DELETED) {
                tab[adr] = new Entry<>(key, value);
                size++;
                return null;
            }
            j++;
        } while (j < tab.length);

        throw new IllegalStateException("Hashtable full");
    }

    //+bernommen aus 2-20 gibt den wert des schlüssels zurück mithilfe der adresse
    @Override
    public V search(K key) {
        int adr = searchAdr(key);
        if (adr == -1)
            return null;
        return tab[adr].getValue();
    }

    //übernommen aus 2-21
    @Override
    public V remove(K key) {
        int adr = searchAdr(key);
        if (adr == -1)
            return null;
        V old = tab[adr].getValue();
        tab[adr] = DELETED; // Markiert diesen Platz als "gelöscht"
        size--;
        return old;
    }

    @Override
    public int size() {
        return size;
    }

    //vergrößert hashtabelle wenn sie zu voll wird
    @SuppressWarnings("unchecked")
    private void grow() {
        Entry<K, V>[] old = tab;
        int newCap = nextPrime4iPlus3(2 * old.length); //Primzahl der form 4*i + 3
        tab = (Entry<K, V>[]) new Entry[newCap]; //neues, leeres Array mit neuer Kapazität
        size = 0;
        for (Entry<K, V> e : old) { //kopiert alle gültigen einträge in neue tabelle
            if (e != null && e != DELETED) {
                insert(e.getKey(), e.getValue());
            }
        }
    }

    // Sucht nächstgrößere Primzahl der Form 4i + 3, siehe Aufgabenstellung
    private int nextPrime4iPlus3(int n) {
        while (!(n % 4 == 3 && isPrime(n))) {
            n++;
        }
        return n;
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    ///javadoc
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() { //übersrpignt alle null und deleted felder
                while (i < tab.length) {
                    if (tab[i] != null && tab[i] != DELETED)
                        return true;
                    i++;
                }
                return false;
            }

            @Override
            public Entry<K, V> next() { //gibt aktuelles element zurückt und geht i weiter
                if (!hasNext())
                    throw new NoSuchElementException();
                return tab[i++];
            }
        };
    }

    //javadoc
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<K, V> e : tab) {
            if (e != null && e != DELETED) {
                sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
        }
        return sb.toString();
    }
}

