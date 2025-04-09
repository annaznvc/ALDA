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

    /////////////////////////////////////
    @Override
    public V insert(K key, V value) {
        V[] old = (V[]) new Object[1];
        root = insertR(key, value, root, null, old);
        return old[0];
    }



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

    @Override
    public V search(K key) {
        Node<K, V> t = root;
        while (t != null) {
            int cmp = key.compareTo(t.key);
            if (cmp < 0)
                t = t.left;
            else if (cmp > 0)
                t = t.right;
            else
                return t.value;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        V[] old = (V[]) new Object[1];
        root = removeR(key, root, old);
        return old[0];
    }

    private Node<K, V> removeR(K key, Node<K, V> t, V[] old) {
        if (t == null)
            return null;
        int cmp = key.compareTo(t.key);
        if (cmp < 0)
            t.left = removeR(key, t.left, old);
        else if (cmp > 0)
            t.right = removeR(key, t.right, old);
        else {
            old[0] = t.value;
            size--;
            if (t.left == null)
                return t.right;
            else if (t.right == null)
                return t.left;
            else {
                Node<K, V> u = t.right;
                while (u.left != null)
                    u = u.left;
                t.key = u.key;
                t.value = u.value;
                t.right = removeR(u.key, t.right, (V[]) new Object[1]);
            }
        }
        return balance(t);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            Stack<Node<K, V>> stack = new Stack<>();
            Node<K, V> current = root;

            @Override
            public boolean hasNext() {
                return !stack.isEmpty() || current != null;
            }

            @Override
            public Entry<K, V> next() {
                while (current != null) {
                    stack.push(current);
                    current = current.left;
                }
                Node<K, V> node = stack.pop();
                current = node.right;
                return new Entry<>(node.key, node.value);
            }
        };
    }

    private Node<K, V> balance(Node<K, V> t) {
        updateHeight(t);
        int bf = getBalance(t);
        if (bf == 2) {
            if (getBalance(t.right) < 0)
                t.right = rotateRight(t.right);
            return rotateLeft(t);
        } else if (bf == -2) {
            if (getBalance(t.left) > 0)
                t.left = rotateLeft(t.left);
            return rotateRight(t);
        }
        return t;
    }

    private void updateHeight(Node<K, V> t) {
        t.height = 1 + Math.max(height(t.left), height(t.right));
    }

    private int height(Node<K, V> t) {
        return t == null ? -1 : t.height;
    }

    private int getBalance(Node<K, V> t) {
        return height(t.right) - height(t.left);
    }

    private Node<K, V> rotateLeft(Node<K, V> t) {
        Node<K, V> u = t.right;
        t.right = u.left;
        if (u.left != null) u.left.parent = t;
        u.left = t;
        u.parent = t.parent;
        t.parent = u;
        updateHeight(t);
        updateHeight(u);
        return u;
    }

    private Node<K, V> rotateRight(Node<K, V> t) {
        Node<K, V> u = t.left;
        t.left = u.right;
        if (u.right != null) u.right.parent = t;
        u.right = t;
        u.parent = t.parent;
        t.parent = u;
        updateHeight(t);
        updateHeight(u);
        return u;
    }


    //////////////////////////////////7
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
