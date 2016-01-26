/**
 * Load configuration JSON file.
 *
 */
package lab04.util;

import java.io.FileReader;
import java.io.FileNotFoundException;

import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.JSONException;

public class Config {
    private String configFile;
    public String mongoServer, mongoDB, words, clientLog, serverLog, wordFilter;
    public int mongoPort, delay;

    public Config(String configFile) {
        this.configFile = configFile;
        init();
    }

    private void init() {
        try {
            JSONObject obj  = new JSONObject(new JSONTokener(
                        new FileReader(this.configFile)));
            this.mongoServer = obj.getString("mongo");
            this.mongoDB = obj.getString("database");
            this.words = obj.getString("words");
            this.clientLog = obj.getString("clientLog");
            this.serverLog = obj.getString("serverLog");
            this.wordFilter = obj.getString("wordFilter");
            this.mongoPort = obj.getInt("port");
            this.delay = obj.getInt("delay");
        } catch (JSONException e) {
            System.err.println("Invalid format found for configuration file.");
            e.printStackTrace();
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found.");
            System.exit(1);
        }
    }

    public void printContents() {
        System.out.println(this.mongoServer);
        System.out.println(this.mongoDB);
        System.out.println(this.words);
        System.out.println(this.clientLog);
        System.out.println(this.serverLog);
        System.out.println(this.wordFilter);
        System.out.println(this.mongoPort);
        System.out.println(this.delay);
    }

}
