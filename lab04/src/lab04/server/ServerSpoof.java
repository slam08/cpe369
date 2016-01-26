/**
 * Imitates a server that periodically checks 
 * the database and logs metadata to a file.
 *
 * @author Scott Lam (slam08@calpoly.edu)
 */
package lab04.server;

import lab04.util.Config;

public class ServerSpoof {
    private Config config;

    public ServerSpoof(String configFile) {
       config = new Config(configFile); 
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
            System.err.println("\tServerSpoof <configuration file>");
        }
        ServerSpoof main = new ServerSpoof(args[0]);
        main.init();
    }
}
