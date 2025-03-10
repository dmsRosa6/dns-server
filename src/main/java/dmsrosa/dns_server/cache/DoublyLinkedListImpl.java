package dmsrosa.dns_server.cache;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedListImpl<T> implements Iterable<T> {

    public static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(T data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedListImpl() {
        head = null;
        tail = null;
        size = 0;
    }

    public void pushFront(T data) {
        Node<T> newNode = new Node<>(data);
        pushFront(newNode);
    }

    public void pushFront(Node<T> newNode) {
        newNode.next = head;
        newNode.prev = null;
        if (head != null) {
            head.prev = newNode;
        } else {
            // If list was empty, tail also points to newNode.
            tail = newNode;
        }
        head = newNode;
        size++;
    }

    public void pushBack(T data) {
        Node<T> newNode = new Node<>(data);
        pushBack(newNode);
    }

    public void remove(Node<T> node) {
        if (node == null) {
            return;
        }

        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
        size--;
    }

    public void pushBack(Node<T> newNode) {
        newNode.prev = tail;
        newNode.next = null;
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        size++;
    }

    public T popFront() {
        if (head == null) {
            return null;
        }
        T data = head.data;
        head = head.next;
        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }
        size--;
        return data;
    }

    public T popBack() {
        if (tail == null) {
            return null;
        }
        T data = tail.data;
        tail = tail.prev;
        if (tail != null) {
            tail.next = null;
        } else {
            // List became empty.
            head = null;
        }
        size--;
        return data;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
