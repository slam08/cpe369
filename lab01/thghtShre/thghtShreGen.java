import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class thghtShreGen {
    private static String PUBLIC = "public";
    private static String PROTECTED = "protected";
    private static String PRIVATE = "private";
    private static String SUBSCRIBERS = "subscribers";
    private static String SELF = "self";
    private static String ALL = "all";

    /**
     * Returns a status with the following probabilities:
     * - public: 0.60
     * - protected: 0.20
     * - private: 0.20
     *
     * @return a String representing the status.
     */
    private String getStatus() {
        int publicPercent = 60;
        int protectedPercent = 20;
        Random rand = new Random();

        int value = rand.nextInt(100) + 1;
        if (value <= publicPercent) {
            return PUBLIC;
        } else if (value <= publicPercent + protectedPercent) {
            return PROTECTED;
        } else {
            return PRIVATE;
        }
    }

    /**
     * Returns a private recipient with the following probabilities:
     * - user: 0.70
     * - self: 0.30
     *
     * @param selfUID a String representing the userId.
     * @return a String representing a recipient.
     */
    private String getPrivateRecipient(String selfUID) {
        Random rand = new Random();
        int value = rand.nextInt(100) + 1;
        int idPercent = 70;

        if (value <= idPercent) {
            String uid = getUserId();
            if (uid.equals(selfUID)) {
                return SELF;
            }
            return uid;
        } else {
            return SELF;
        }
    }

    /**
     * Returns a protected recipient with the following probabilities:
     * - subscriber: 0.70
     * - user: 0.15
     * - self: 0.15
     *
     * @param selfUID a String representing the userId.
     * @return a String representing a recipient.
     */
    private String getProtectedRecipient(String selfUID) {
        Random rand = new Random();
        int value = rand.nextInt(100) + 1;
        int subscriberPercent = 70;
        int idPercent = 15;

        if (value <= subscriberPercent) {
            return SUBSCRIBERS;
        } else if (value <= subscriberPercent + idPercent) {
            String uid = getUserId();
            if (uid.equals(selfUID)) {
                return SELF;
            }
            return uid;
        } else {
            return SELF;
        }
    }

    /**
     * Returns a public recipient with the following probabilities:
     * - all: 0.40
     * - subscriber: 0.40
     * - user: 0.10
     * - self: 0.10
     *
     * @param selfUID a String representing the userId.
     * @return a String representing a recipient.
     */
    private String getPublicRecipient(String selfUID) {
        Random rand = new Random();
        int value = rand.nextInt(100) + 1;
        int subscriberPercent = 40;
        int allPercent = 40;
        int idPercent = 10;

        if (value <= subscriberPercent) {
            return SUBSCRIBERS;
        } else if (value <= subscriberPercent + allPercent) {
            return ALL;
        } else if (value <= subscriberPercent + allPercent + idPercent) {
            String uid = getUserId();
            if (uid.equals(selfUID)) {
                return SELF;
            }
            return uid;
        } else {
            return SELF;
        }
    }

    /**
     * Returns a random messageId less than the messageId associated with the current message.
     *
     * @param messageId an Integer representing the current messageId.
     * @return an Integer representing the random messageId.
     */
    private int getResponseId(int messageId) {
        Random rand = new Random();
        int offset = rand.nextInt(messageId - 1) + 1;
        return messageId - offset;
    }

    /**
     * Returns a random userId between u1 and u10000.
     *
     * @return a String representing the userId.
     */
    private String getUserId() {
        int maxUsers = 10000;

        Random rand = new Random();
        return "u" + rand.nextInt(maxUsers) + 1;
    }

    /**
     * Returns a String representing the message text, with a random word count between 2 and 20. Also selects
     * words randomly from the dictionary.
     *
     * @param dictionary an ArrayList representing the possible words.
     * @return a String representing the message text.
     */
    private String generateText(ArrayList<String> dictionary) {
        Random rand = new Random();
        String text = "";
        // 2 to 20 words
        int numWords = rand.nextInt(19) + 2;

        for (int i = 1; i <= numWords; i++) {
            text += dictionary.get(rand.nextInt(dictionary.size()));
            if (i != numWords) {
                text += " ";
            }
        }
        return text;
    }

    /**
     * Parses the textfile which holds the dictionary (sense.txt) and places all words in an ArrayList.
     *
     * @return an ArrayList which represents the dictionary.
     * @throws IOException If the file (sense.txt) is unavailable.
     */
    private ArrayList<String> parseDictionary() throws IOException {
        BufferedReader brIn = new BufferedReader(new FileReader("sense.txt"));
        String line;

        ArrayList<String> words = new ArrayList<String>();
        while((line = brIn.readLine()) != null){
            words.add(line);
        }

        return words;
    }

    /**
     * Generates all of the logs for ThoughtShare.
     *
     * @param args commandline arguments passed into the program.
     * @throws JSONException If any errors occur with creating/writing the JSON objects.
     * @throws IOException If the output file cannot be created.
     */
    public void generateLogs(String[] args) throws JSONException, IOException {
        String fileName = args[0];
        FileWriter writer = new FileWriter(fileName);
        int numLogs = Integer.parseInt(args[1]);

        ArrayList<String> dictionary = parseDictionary();
        Random rand = new Random();
        int responsePercent = 5;

        writer.write("[");

        for (int messageId = 1; messageId <= numLogs; messageId++) {
            String uid = getUserId();

            // Generate random data and add it to the log record
            JSONObject logRecord = new JSONObject();
            logRecord.put("messageId", messageId);
            logRecord.put("user", uid);
            String status = getStatus();
            logRecord.put("status", status);
            String recipient;
            if (status.equals(PRIVATE)) {
                recipient = getPrivateRecipient(uid);
            } else if (status.equals(PROTECTED)) {
                recipient = getProtectedRecipient(uid);
            } else {
                recipient = getPublicRecipient(uid);
            }
            logRecord.put("recipient", recipient);
            if (rand.nextInt(100) + 1 <= responsePercent) {
                logRecord.put("in-response", getResponseId(messageId));
            }
            logRecord.put("text", generateText(dictionary));

            logRecord.write(writer);
            if (messageId != numLogs) {
                writer.write(",");
            }
        }

        writer.write("]");
        writer.close();
    }

    public static void main(String[] args) throws JSONException, IOException {
        thghtShreGen main = new thghtShreGen();
        main.generateLogs(args);
    }

}
