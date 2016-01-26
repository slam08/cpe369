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

public class ThghtShreMaxMemory {
    public static void main(String[] args) throws JSONException, IOException {
        JsonFactory factory = new MappingJsonFactory();
        FileReader fileReader = new FileReader("thghtShre.out");
        JsonParser parser = factory.createParser(fileReader);
        JsonToken token = parser.nextToken();

        KeyValueStore kvstore = new KeyValueStore(2);
        kvstore.addCollection("thghtShre");
        KVCollection thghtShre = kvstore.getCollection("thghtShre");
        
        int cnt = 0;
        try {
            while (token != JsonToken.END_ARRAY) {
                cnt++;
                token = parser.nextToken();
                if (token == JsonToken.START_OBJECT) {
                    JsonNode node = parser.readValueAsTree();
                    JSONObject obj = new JSONObject(node.toString());
                    thghtShre.put(node.get("messageId").asInt(), obj);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println(cnt + " ThghtShre JSON Objects to fill memory");
        }
    }
}
