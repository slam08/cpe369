/**
 * Naive approach at implementing a key-value store
 * using a HashMap of KVCollections.
 *
 * Author: Scott Lam (slam08@calpoly.edu)
 * Date: 1/13/16
 */
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;

public class KeyValueStore {
    private static final int NO_LIMIT = -1;

    private int size;
    private int limit;
    private Map<String, KVCollection> kvstore;

    public KeyValueStore() {
        kvstore = new HashMap<String, KVCollection>();
        this.size = 0;
        this.limit = NO_LIMIT;
    }

    public KeyValueStore(int limit) {
        kvstore = new HashMap<String, KVCollection>();
        this.size = 0;
        this.limit = limit;
    }

    public KeyValueStore(Set<String> collections) {
        kvstore = new HashMap<String, KVCollection>();
        this.size = collections.size();
        this.limit = collections.size();
        for (String key : collections) {
            kvstore.put(key, new KVCollection());
        }
    }

    /**
     * Remove all collections from key-value store.
     */
    public void clear() {
        kvstore.clear();
    }
    
    /**
     * Create an empty collection and add it to the 
     * key-value store under a given name.
     *
     * @param name The name associated with the newly 
     *              created collection
     * @return An integer that represents the status of 
     *          the operation
     *           1: collection successfully inserted
     *          -1: duplicate name. collection not created
     *          -2: limit on the number of collections 
     *               is exceeded
     *           0: collection not inserted for any other reason
     */
    public int addCollection(String name) {
        if (kvstore.get(name) != null) {
            return -1;
        }
        if (this.size == this.limit) {
            return -2;
        }
        kvstore.put(name, new KVCollection());
        this.size++;
        return 1;
    }
    
    /**
     * Return a KVCollection object associated with 
     * the provided name.
     *
     * @param name The key of the KVCollection to retreive
     * @return A KVCollection associated with the 
     *          provided name. 
     *         Returns null if there is no KVCollection 
     *          associated with name
     */
    public KVCollection getCollection(String name) {
        return kvstore.get(name);
    }

    /**
     * Returns a set of collection names (keys) in the 
     * key-value store.
     *
     * @return A set of strings representing all the keys 
     *          in the key-value store
     */
    public Set<String> list() {
        return kvstore.keySet();
    }

    /** 
     * Returns true if key-value store contains no 
     * collections, false otherwise.
     *
     * @return A boolean that is true of key-value store 
     *          is empty. false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Return the number of collections in key-value store.
     *
     * @return An integer representing the size of the 
     *          key-value store
     */
    public int size() {
        return size;
    }

    /**
     * Return the number of objects in all collections in 
     * the key-value store.
     *
     * @return An integer representing the number of 
     *          objects in all collections.
     */
    public int getNumObjects() {
        int numObjects = 0;
        for (Map.Entry<String, KVCollection> entry :
                kvstore.entrySet()) {
            numObjects += entry.getValue().size();
        }
        return numObjects;
    }

    /**
     * Return the maximum number of collections allowed by 
     * the key-value store, -1 if there is no limit.
     *
     * @return An integer representing the limit of the key-value store
     */
    public int getLimit() {
        return limit;
    }
}
