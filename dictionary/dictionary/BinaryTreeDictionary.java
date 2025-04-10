package dictionary;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class BinaryTreeDictionary<K extends Comparable<K>, V> implements Dictionary<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        int height;
        Node<K, V> left;
        Node<K, V> right;
        Node<K, V> parent;

        Node(K k, V v) {
            key = k;
            value = v;
            height = 0;
            left = null;
            right = null;
            parent = null;
        }
    }

    private Node<K, V> root = null;
    private int size = 0;

    //übernommen aus 3-12
    @Override
    public V insert(K key, V value) {
        V[] old = (V[]) new Object[1];
        root = insertR(key, value, root, null, old);
        return old[0];
    }

    // Übernommen aus 4-18
    private Node<K, V> insertR(K key, V value, Node<K, V> t, Node<K, V> parent, V[] old) {
        if (t == null) {
            size++;
            Node<K, V> newNode = new Node<>(key, value);
            newNode.parent = parent;
            return newNode;
        }
        int cmp = key.compareTo(t.key);
        if (cmp < 0)
            t.left = insertR(key, value, t.left, t, old);
        else if (cmp > 0)
            t.right = insertR(key, value, t.right, t, old);
        else {
            old[0] = t.value;
            t.value = value;
        }
        return balance(t);
    }

    // Übernommen aus 3-10
    @Override
    public V search(K key) {
        return searchR(key, root);
    }

    private V searchR(K key, Node<K, V> p) {
        if (p == null)
            return null;
        else if (key.compareTo(p.key) < 0)
            return searchR(key, p.left);
        else if (key.compareTo(p.key) > 0)
            return searchR(key, p.right);
        else
            return p.value;
    }

    //übernommen aus 3-16
    private V oldValue; // Rückgabeparameter für remove

    @Override
    public V remove(K key) {
        root = removeR(key, root);
        return oldValue;
    }

    private Node<K, V> removeR(K key, Node<K, V> p) {
        if (p == null) {
            oldValue = null;
        } else if (key.compareTo(p.key) < 0) {
            p.left = removeR(key, p.left);
        } else if (key.compareTo(p.key) > 0) {
            p.right = removeR(key, p.right);
        } else if (p.left == null || p.right == null) {
            // p muss gelöscht werden (0 oder 1 Kind)
            oldValue = p.value;
            size--;
            p = (p.left != null) ? p.left : p.right;
        } else {
            // p hat zwei Kinder
            MinEntry<K, V> min = new MinEntry<>();
            p.right = getRemMinR(p.right, min);
            oldValue = p.value;
            p.key = min.key;
            p.value = min.value;
            size--;
        }
        return (p != null) ? balance(p) : null;
    }

    //löscht im Baum p den Knoten mit kleinstem Schlüssel und liefert dessen Daten zurück
    private Node<K, V> getRemMinR(Node<K, V> p, MinEntry<K, V> min) {
        assert p != null;
        if (p.left == null) {
            min.key = p.key;
            min.value = p.value;
            return p.right;
        } else {
            p.left = getRemMinR(p.left, min);
            return balance(p); // AVL-Balancierung beim Rückweg
        }
    }

    //Hildsdatentyp für Rücgabeparameter min von getRemMinR
    private static class MinEntry<K, V> {
        private K key;
        private V value;
    }

    @Override
    public int size() {
        return size;
    }

    
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            Stack<Node<K, V>> stack = new Stack<>(); //noch zu bearbeitenden Knoten speichern
            Node<K, V> current = root;

            @Override
            public boolean hasNext() {
                return !stack.isEmpty() || current != null; //stack nicht leer oder current nicht null (wir haben noch einen Teilbaum zu betreten)
            }

            @Override
            public Entry<K, V> next() {
                while (current != null) { //solange wir weiter nach links können, gehen wir links
                    stack.push(current);
                    current = current.left; //wir merken uns die knoten
                }
                Node<K, V> node = stack.pop(); //wenn wir am linken ende angekommen sind, holen wir letzten gespeichert Knoten aus dem Stack
                current = node.right; //setzen current auf seinen rechten teimbaum (in order: links, wurzel, rechts)
                return new Entry<>(node.key, node.value); //aktuellen Knoten als Entry zurückliefern
            }
        };
    }

    //Übernommen aus 4-18
    private Node<K, V> balance(Node<K, V> p) {
        if (p == null)
            return null;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        if (getBalance(p) == -2) {
            if (getBalance(p.left) <= 0)
                p = rotateRight(p);
            else
                p = rotateLeftRight(p);
        } else if (getBalance(p) == +2) {
            if (getBalance(p.right) >= 0)
                p = rotateLeft(p);
            else
                p = rotateRightLeft(p);
        }
        return p;
    }

    private int getHeight(Node<K, V> p) {
        return (p == null) ? -1 : p.height;
    }

    private int getBalance(Node<K, V> p) {
        return getHeight(p.right) - getHeight(p.left);
    }

    //übernommen aus 4-20
    private Node<K, V> rotateRight(Node<K, V> p) {
        assert p.left != null;
        Node<K, V> q = p.left;
        p.left = q.right;
        q.right = p;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }

    //übernommen aus 4-21
    private Node<K, V> rotateLeft(Node<K, V> p) {
        assert p.right != null;
        Node<K, V> q = p.right;
        p.right = q.left;
        q.left = p;
        p.height = Math.max(getHeight(p.left), getHeight(p.right)) + 1;
        q.height = Math.max(getHeight(q.left), getHeight(q.right)) + 1;
        return q;
    }
    private Node<K, V> rotateLeftRight(Node<K, V> p) {
        assert p.left != null;
        p.left = rotateLeft(p.left);
        return rotateRight(p);
    }
    private Node<K, V> rotateRightLeft(Node<K, V> p) {
        assert p.right != null;
        p.right = rotateRight(p.right);
        return rotateLeft(p);
    }

    //vorgegeben:
    public void prettyPrint() {
        printR(0, root);
    }

    private void printR(int level, Node<K, V> p) {
        printLevel(level);
        if (p == null) {
            System.out.println("#");
        } else {
            System.out.println(p.key + " " + p.value + "^" + ((p.parent == null) ? "null" : p.parent.key));
            if (p.left != null || p.right != null) {
                printR(level + 1, p.left);
                printR(level + 1, p.right);
            }
        }
    }

    private static void printLevel(int level) {
        if (level == 0) return;
        for (int i = 0; i < level - 1; i++) System.out.print("   ");
        System.out.print("|__");
    }
}
