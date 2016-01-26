import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.IOException;
import java.io.FileReader;

import org.json.JSONObject;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class BeFuddledMaxMemory {
    public static void main(String[] args) throws JSONException, IOException {
        JsonFactory factory = new MappingJsonFactory();
        FileReader fileReader = new FileReader("beFuddled.out");
        JsonParser parser = factory.createParser(fileReader);
        JsonToken token = parser.nextToken();

        KeyValueStore kvstore = new KeyValueStore(2);
        kvstore.addCollection("beFuddled");
        KVCollection beFuddled = kvstore.getCollection("beFuddled");

        int id = 0;
        try {
            while (token != JsonToken.END_ARRAY) {
                token = parser.nextToken();
                if (token == JsonToken.START_OBJECT) {
                    JsonNode node = parser.readValueAsTree();
                    JSONObject obj = new JSONObject(node.toString());
                    beFuddled.put(id++, obj);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println(id + " BeFuddled JSON Objects to fill memory");
        }
    }
}
