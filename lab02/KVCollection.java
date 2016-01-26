import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class KVCollection {
    private Map<Integer, JSONObject> kvcollection;
    private int size;
    
    // Create a new KVCOllection object
    public KVCollection() {
        kvcollection = new HashMap<Integer, JSONObject>();
        size = 0;
    }
    
    /**
     * Removes all key-value pairs stored in the collection.
     *
     * @return A boolean value, true if successful, false otherwise
     */
    public boolean clear() {
        kvcollection.clear();
        return true;
    }

    /**
     * Checks if the collection contains the provided key.
     *
     * @return A boolean value, true if the key exists in 
     *          the collection, false otherwise
     */
    public boolean containsKey(int key) {
        return kvcollection.containsKey(key);
    }

    /**
     * Retrieve the JSONObject associated with the provided key.
     *
     * @param key An integer representing the key for the 
     *             JSONObject that will be retrieved
     * @return The JSONObject associated with the provided key
     */
    public JSONObject get(int key) {
        return kvcollection.get(key);
    }
    
    /**
     * Inserts the provided key-value pair into the collection.
     *
     * @param key The key for the new created entry
     * @param value The value for the newly created entry
     * @return An integer representing the status of 
     *          the operation
     *         1: key-value pair successfully inserted.
     *        -1: attempt to insert duplicate key. 
     *              key-value pair not inserted.
     *         0: key-value pair could not be inserted for 
     *              any reason other than duplicate key.
     */
    public int put(int key, JSONObject value) {
        if (kvcollection.containsKey(key)) {
            return -1;
        }
        kvcollection.put(key, value);
        size++;
        return 1;
    }

    /**
     * Removes the key-value pair associated with the 
     * provided key from the collection.
     *
     * @param key The key for key-value pair that will be removed
     * @return An integer representing the status of 
     *          the operation
     *         1: key-value pair successfully removed.
     *        -1: key not in collection. 
     *         0: key is in collection, but removal failed.
     */
    public int remove(int key) {
        if (!kvcollection.containsKey(key)) {
            return -1;
        }
        kvcollection.remove(key);
        return 1;
    }

    /**
     * Replaces the value associated with the provided 
     * key with the provided value.
     *
     * @param key The key associated with the value that will be replaced
     * @param value The value that will replace the current 
     *               value associate with the provided key
     * @return An integer representing the status of the operation
     *         1: key-value pair successfully replaced.
     *        -1: key not in collection. replace not performed.
     *         0: key-value pair in collection, but replace failed.
     */
    public int replace(int key, JSONObject value) {
        if (!kvcollection.containsKey(key)) {
            return -1;
        }
        kvcollection.put(key, value);
        return 1;
    }

    /**
     * Returns the current size of the collection.
     *
     * @return An integer representing the size
     *          of the collection.
     */
    public int size() {
        return this.size;
    }

    /**
     * If an object with the provided key exists,
     * replace the value with the new object.
     * Otherwise, inserts a new key-value pair.
     *
     * @param key The key for the key-value pair 
     *             that will be replaced/inserted.
     * @param value The value for the key-value pair
     *               that will be replaced/inserted.
     * @return An integer representing the status of the operation
     *         1: key-value pair successfully inserted
     *         2: key-value pair successfully replaced
     *         0: operation failed
     */
    public int upsert(int key, JSONObject value) {
        if (kvcollection.containsKey(key)) {
            kvcollection.put(key, value);    
            return 2;
        } 
        kvcollection.put(key, value);    
        return 1;
    }

    /**
     * Performs a search among all key-value pairs 
     * stored in the Collection instance for JSON objects
     * which satisfy the follwing conditions:
     *      - contains a field/attribute with the 
     *         name jsonFieldName
     *      - the content of the field/attribute is equal 
     *         to jsonFieldValue
     * and returns a JSONArray with following structure:
     *      {
     *          "key": <key of JSONObject in kvcollection>,
     *          "value": <JSON Object that satisfies conditions>
     *      }
     *
     * @param jsonFieldName The field name to search for
     * @param jsonFieldValue The value associated with the field name
     */
    JSONArray find(String jsonFieldName, 
                   Object jsonFieldValue) {
        JSONArray found = new JSONArray(); 
        for (Map.Entry<Integer, JSONObject> entry : 
                kvcollection.entrySet()) {
            JSONObject value = entry.getValue();
            try {
                if (value.get(jsonFieldName).equals(
                            jsonFieldValue)) {
                    JSONObject foundObj = new JSONObject();
                    foundObj.put("key", entry.getKey());
                    foundObj.put("value", value);
                    found.put(foundObj);
                }
            } catch (JSONException e) {
                return found;
            }
        }
        return found;
    }
}
