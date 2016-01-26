/**
 * Imitates a client that sends messages that
 * are saved into the database.
 *
 * @author Scott Lam (slam08@calpoly.edu)
 */
package lab04.client;


import java.io.FileOutputStream;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import lab04.util.Config;

public class ClientSpoof {
    private Config config;
    private MongoDatabase database;
    private MongoClient client;

    public ClientSpoof(String configFile) {
        this.config = new Config(configFile);
        this.client = new MongoClient(this.config.mongoServer, this.config.mongoPort);
        this.database = client.getDatabase(this.config.mongoDB);
    }

    private void execute() {
        while (true) {
            
        }
    }

    private void init() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down...");
                System.out.flush();
            }
        });
        execute();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid number of arguments");
            System.err.println("Usage:");
            System.err.println("\tClientSpoof <configuration file>");
            System.exit(1);
        }
        ClientSpoof main = new ClientSpoof(args[0]);
        main.init();
    }
}
