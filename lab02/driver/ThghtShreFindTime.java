import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import java.text.DecimalFormat;

import java.io.IOException;
import java.io.FileReader;

import org.json.JSONObject;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class ThghtShreFindTime {
    public static void main(String[] args) throws JSONException, IOException {
        int amount = Integer.parseInt(args[0]);
        String fieldName = args[1];
        Object fieldValue = args[2];
        JsonFactory factory = new MappingJsonFactory();
        FileReader fileReader = new FileReader("thghtShre.out");
        JsonParser parser = factory.createParser(fileReader);
        JsonToken token = parser.nextToken();

        KeyValueStore kvstore = new KeyValueStore(2);
        kvstore.addCollection("thghtShre");
        KVCollection thghtShre = kvstore.getCollection("thghtShre");
        
        int cnt = 0;
        try {
            while (cnt < amount && token != JsonToken.END_ARRAY) {
                cnt++;
                token = parser.nextToken();
                if (token == JsonToken.START_OBJECT) {
                    JsonNode node = parser.readValueAsTree();
                    JSONObject obj = new JSONObject(node.toString());
                    thghtShre.put(node.get("messageId").asInt(), obj);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println(cnt + "/" + amount + " ThghtShre JSON Objects processed");
        }
        long start = System.nanoTime();
        System.out.println(thghtShre.find(fieldName, fieldValue).length());
        long stop = System.nanoTime();
        DecimalFormat df = new DecimalFormat("#.##");
        double duration = (stop - start) / 1000000000.00;
        System.out.println("Time taken: " +  df.format(duration) + " seconds");
    }

}
