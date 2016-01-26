import java.util.Random;
import java.util.ArrayList;

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


public class RoundRobinShardingRetrieval {
    public double execute(String[] args) throws JSONException, IOException {
        Random rand = new Random();
        FileReader fileReader = new FileReader("thghtShre.out");
        JsonFactory factory = new MappingJsonFactory();
        int upperBound = Integer.parseInt(args[0]);
        int numObj = 100000;
        KeyValueStore kvstore = new KeyValueStore();
        for (int j=1; j<=upperBound; j++) {
            kvstore.addCollection(Integer.toString(j));
        }

        JsonParser parser = factory.createParser(fileReader);
        JsonToken token = parser.nextToken();
        int cnt = 0;
        while (cnt < numObj && token != JsonToken.END_ARRAY) {
            cnt++;
            token = parser.nextToken();
            if (token == JsonToken.START_OBJECT) {
                JsonNode node = parser.readValueAsTree();
                JSONObject obj = new JSONObject(node.toString());
                String storeKey = Integer.toString(cnt % upperBound + 1);
                kvstore.getCollection(storeKey).put(
                        node.get("messageId").asInt(), obj);
            }
        }
        parser.close();
        int findThis = rand.nextInt(cnt) + 1;
        //System.out.println("findThis: " + findThis);
        JSONObject found = null;
        long start = System.nanoTime();
        for (String key : kvstore.list()) {
            KVCollection kvc = kvstore.getCollection(key);
            //System.out.println("Collection: " + key + " size: " + kvc.size());
            if (kvc.containsKey(findThis)) {
                found = kvc.get(findThis);
            }
        }
        long stop = System.nanoTime();
        DecimalFormat df = new DecimalFormat("#.##");
        double duration = (double) (stop - start) / 1000000.0;
        System.out.println("(" + upperBound + " collections) Time taken: " + 
                duration + " milliseconds");
        return duration;
    }

    public double mean(ArrayList<Double> values) {
        double total = 0.0;
        for (double v : values) {
            total += v;
        }
        return total / values.size();
    }

    public double standardDeviation(ArrayList<Double> values, double mean) {
        double total = 0.0;
        for (double v : values) {
            total += Math.pow(v - mean, 2);
        }
        return Math.sqrt(total / values.size());
    }

    public void init(String[] args) throws JSONException, IOException {
        ArrayList<Double> values = new ArrayList<Double>();
        for (int i=0; i<=Integer.parseInt(args[1]); i++) {
            values.add(execute(args));
        }
        // throw away first value because it is unusually large for each run
        values.remove(0);
        DecimalFormat df = new DecimalFormat("#.###");
        double mean = mean(values);
        double stddev = standardDeviation(values, mean);
        System.out.println("Mean: " + df.format(mean) + " ms");
        System.out.println("Standard Deviation: " + df.format(stddev) + " ms");
    }

    public static void main(String[] args) throws JSONException, IOException {
        RoundRobinShardingRetrieval main = new RoundRobinShardingRetrieval();
        main.init(args);
    }
}
