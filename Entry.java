public class Entry<K, V> {
    private final K key;
    private V value;
    private Entry<K, V> next;

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Entry<K, V> getNext() {
        return next;
    }

    public void setNext(Entry<K, V> next) {
        this.next = next;
    }

    public Entry(K key, V value){
        this.key = key;
        this.value = value;
    }
}
