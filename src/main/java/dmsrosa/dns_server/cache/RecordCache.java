package dmsrosa.dns_server.cache;

import java.util.HashMap;
import java.util.Map;

import dmsrosa.dns_server.cache.DoublyLinkedListImpl.Node;
import dmsrosa.dns_server.messages.DnsRecord;
import dmsrosa.dns_server.utils.Pair;

public class RecordCache {
    private final Map<String, Pair<DnsRecord, Node<String>>> cache;
    private final DoublyLinkedListImpl<String> hist;
    private final int max;

    public RecordCache(int size) {
        this.max = size;
        this.cache = new HashMap<>(size);
        this.hist = new DoublyLinkedListImpl<>();
    }

    public synchronized void put(String key, DnsRecord value) {
        Pair<DnsRecord, Node<String>> p = cache.get(key);
        if (p != null) {
            hist.remove(p.getSecond());
        }
        if (hist.size() == max) {
            Node<String> k = hist.getTail();
            Node<String> node = cache.get(k.data).getSecond();
            cache.remove(k.data);
            hist.remove(node);
        }
        Node<String> n = new Node<>(key);
        hist.pushFront(n);
        cache.put(key, new Pair<>(value, n));
    }

    public synchronized DnsRecord get(String key) {
        Pair<DnsRecord, Node<String>> p = cache.get(key);
        if (p == null) {
            return null;
        }
        // Update the LRU ordering
        hist.remove(p.getSecond());
        hist.pushFront(p.getSecond());
        return p.getFirst();
    }
}
