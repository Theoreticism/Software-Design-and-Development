package charlie.advisor;

/**
 * Pair class to make using the HashMap easier. Pairs the key (significant card
 * in hand) with the other key (dealer's up card) for analysis.
 * @author Christopher Lee
 */
public class Pair<K, V> {
    
    private final K item1;
    private final V item2;
    
    /*
     * Pair constructor.
     * @param item1 first item in pair
     * @param item2 second item in pair
     */
    public Pair(K item1, V item2) {
        this.item1 = item1;
        this.item2 = item2;
    }
    
    /*
     * Convenience method for creating a pair
     * @param k first object in Pair
     * @param v second object in Pair
     * @return Pair templatized with types of k and v
     */
    public static <K, V> Pair<K, V> create(K k, V v) {
        return new Pair<>(k, v);
    }
    
    /*
     * Compute a hash code using the hash codes of the objects in the Pair
     * Overriding hashCode from java.lang.Object is necessary for the hashMap to work,
     * as there is no intrinsic Pair object in Java.
     * @returns hashCode of the pair
     */
    @Override
    public int hashCode() {
        int result = item1 != null ? item1.hashCode() : 0;
        result = 31 * result + (item2 != null ? item2.hashCode() : 0);
        return result;
    }

    /*
     * Checks two objects for equality
     * Overriding equals from java.lang.Object is necessary for the hashMap to work,
     * as there is no intrinsic Pair object in Java.
     * @param obj the Pair to be checked for equality
     * @return true if the objects of the Pair are considered equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Pair pair = (Pair) obj;

        if (item1 != null ? !item1.equals(pair.item1) : pair.item1 != null) return false;
        if (item2 != null ? !item2.equals(pair.item2) : pair.item2 != null) return false;

        return true;
    }
    
    /*
     * Getter method for testing purposes
     */
    public K getKey() {
        return item1;
    }
    
    /*
     * Setter method for testing purposes
     */
    public V getValue() {
        return item2;
    }
    
}
