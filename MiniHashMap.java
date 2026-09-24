import java.util.Optional;

public class MiniHashMap<K, V> {
    private static final double LOAD_FACTOR = 0.75;
    private static final int BUCKETS_SIZE = 16;
    private Entry<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public MiniHashMap() {
        buckets = (Entry<K, V>[]) new Entry<?, ?>[BUCKETS_SIZE];
        size = 0;
    }

    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        int bucketIndex = this.getBucketIndex(key);
        V returnedValue =  this.addToBucket(key, value, bucketIndex);
        if(this.size > this.buckets.length * LOAD_FACTOR){
            this.resize();
        }
        return returnedValue;
    }
    
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int bucketIndex = this.getBucketIndex(key);
        return this.findInSimpleLinkedList(key, this.buckets[bucketIndex]).map(Entry::getValue).orElse(null);
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int bucketIndex = this.getBucketIndex(key);
        return this.findInSimpleLinkedList(key, this.buckets[bucketIndex]).isPresent();
    }

    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int bucketIndex = this.getBucketIndex(key);
        return this.remove(key, bucketIndex, this.buckets[bucketIndex]); 
    }

    public int size(){
        return this.size;
    }

    public boolean isEmpty(){
        return this.size == 0;
    }

    private int getBucketIndex(K key) {
        int hashCode = this.spreadHash(key);
        return hashCode & (buckets.length - 1);
    }

    private int spreadHash(K key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private V addToBucket(K key, V value, int index) {
        if (this.buckets[index] == null) {
            this.buckets[index] = new Entry<K, V>(key, value);
            this.size++;
            return null;
        }
        Entry<K, V> firstBucketEntry = this.buckets[index];
        Optional<Entry<K, V>> foundEntry = this.findInSimpleLinkedList(key, firstBucketEntry);
        if (!foundEntry.isPresent()) {
            Entry<K, V> newEntry = new Entry<K, V>(key, value);
            newEntry.setNext(firstBucketEntry);
            this.buckets[index] = newEntry;
            this.size++;
            return null;
        } else {
            Entry<K, V> oldEntry = foundEntry.get();
            V oldValue = oldEntry.getValue();
            foundEntry.get().setValue(value);
            return oldValue;
        }
    }

    private Optional<Entry<K, V>> findInSimpleLinkedList(K key, Entry<K, V> entry) {
        while (entry != null) {
            if (entry.getKey().equals(key)) {
                return Optional.of(entry);
            }
            entry = entry.getNext();
        }
        return Optional.empty();
    }

    private V remove(K key, int bucketIndex, Entry<K, V> entry){
        Entry<K, V> prevEntry = null;
        while(entry != null){
            if(prevEntry == null && entry.getKey().equals(key)){
                this.buckets[bucketIndex] = entry.getNext();
                this.size--;
                return entry.getValue();
            }
            if(entry.getKey().equals(key)){
                prevEntry.setNext(entry.getNext());
                this.size--;
                return entry.getValue();
            }
            prevEntry = entry;
            entry = entry.getNext();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize(){
        Entry<K,V>[] oldBuckEntries = this.buckets;
        this.buckets = (Entry<K,V>[]) new Entry<?, ?>[oldBuckEntries.length * 2];
        for(Entry<K, V> entry : oldBuckEntries){
            while(entry != null){
                Entry<K, V> nextEntry = entry.getNext();
                int newBucketIndex = this.getBucketIndex(entry.getKey());
                entry.setNext(this.buckets[newBucketIndex]);
                this.buckets[newBucketIndex] =  entry;
                entry = nextEntry;
            }
        }
    }
}
