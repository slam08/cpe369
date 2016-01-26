import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import java.text.DecimalFormat;

import java.io.IOException;
import java.io.FileReader;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class BeFuddledFindTime {
    private static boolean isDouble(String value) {
        try {
            double d = Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isInteger(String value) {
        try {
            int i = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) throws JSONException, IOException {
        int amount = Integer.parseInt(args[0]);
        String fieldName = args[1];
        JsonFactory factory = new MappingJsonFactory();
        FileReader fileReader = new FileReader("beFuddled.out");
        JsonParser parser = factory.createParser(fileReader);
        JsonToken token = parser.nextToken();

        KeyValueStore kvstore = new KeyValueStore(2);
        kvstore.addCollection("beFuddled");
        KVCollection beFuddled = kvstore.getCollection("beFuddled");
        
        int cnt = 0;
        try {
            while (cnt < amount && token != JsonToken.END_ARRAY) {
                cnt++;
                token = parser.nextToken();
                if (token == JsonToken.START_OBJECT) {
                    JsonNode node = parser.readValueAsTree();
                    JSONObject obj = new JSONObject(node.toString());
                    beFuddled.put(cnt, obj);
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println(cnt + "/" + amount + " BeFuddled JSON Objects processed");
        }
        long start = System.nanoTime();        
        JSONArray arr;
        if (isInteger(args[2])) {
            arr = beFuddled.find(fieldName, Integer.parseInt(args[2]));
        } else if (isDouble(args[2])) {
            arr = beFuddled.find(fieldName, Double.parseDouble(args[2]));
        } else {
            arr = beFuddled.find(fieldName, args[2]);
        }
        //System.out.println(beFuddled.find(fieldName, fieldValue).length());
        long stop = System.nanoTime();
        DecimalFormat df = new DecimalFormat("#.##");
        double duration = (stop - start) / 1000000000.00;
        System.out.println("Found " + arr.length() + " items");
        System.out.println("Time taken: " +  df.format(duration) + " seconds");
    }
}
