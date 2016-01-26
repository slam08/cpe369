import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class thghtShreStats {
    private static final String PUBLIC = "public";
    private static final String PRIVATE = "private";
    private static final String PROTECTED = "protected";
    private static final String ALL = "all";
    private static final String SELF = "self";
    private static final String SUBSCRIBERS = "subscribers";
    private static final String USER = "user";

    private static final int PUBLIC_INDEX = 0;
    private static final int PROTECTED_INDEX = 1;
    private static final int PRIVATE_INDEX = 2;

    private static final int NOT_IN_RESPONSE_INDEX = 0;
    private static final int IN_RESPONSE_INDEX = 1;



    public void init(String[] args) throws IOException, JSONException {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Improper arguments. Use the following structure:");
            System.err.println("... thghtShreStats <inputFile> (opt:<outputFile>)");
            System.exit(1);
        }
        String inputFile = args[0];
        JsonFactory factory = new MappingJsonFactory();
        FileReader file = null;

        try {
            file = new FileReader(inputFile);
        } catch (IOException e) {
            System.err.println("Input file does not exist.");
            e.printStackTrace();
            System.exit(1);
        }

        JsonParser parser = factory.createParser(file);
        JsonToken token = parser.nextToken(); // Get the start array

        int totalMessages = 0;
        int publicMessages = 0;
        int privateMessages = 0;
        int protectedMessages = 0;

        int allRecepients = 0;
        int selfRecepients = 0;
        int subscriberRecepients = 0;
        int userRecepients = 0;

        int inResponseMessages = 0;
        int notInResponseMessages = 0;

        double averageWordLength = 0.0;
        double averageWordLengthPublic = 0.0;
        double averageWordLengthPrivate = 0.0;
        double averageWordLengthProtected = 0.0;

        double averageCharLength = 0.0;
        double averagePublicCharLength = 0.0;
        double averagePrivateCharLength = 0.0;
        double averageProtectedCharLength = 0.0;

        double averageWordLengthAll = 0.0;
        double averageWordLengthSelf = 0.0;
        double averageWordLengthSubscriber = 0.0;
        double averageWordLengthUser = 0.0;

        double averageCharLengthAll = 0.0;
        double averageCharLengthSelf = 0.0;
        double averageCharLengthSubscriber = 0.0;
        double averageCharLengthUser = 0.0;

        double averageWordLengthInResponse = 0.0;
        double averageWordLengthNotInResponse = 0.0;

        double averageCharLengthInResponse = 0.0;
        double averageCharLengthNotInResponse = 0.0;

        ArrayList<String> uniqueUsers = new ArrayList<String>();

        ArrayList<Integer> messageLengthWords = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsPublic = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsPrivate = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsProtected = new ArrayList<Integer>();

        ArrayList<Integer> messageLengthChars = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsPublic = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsPrivate = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsProtected = new ArrayList<Integer>();

        ArrayList<Integer> messageLengthWordsAll = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsSelf = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsSubscriber = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsUser = new ArrayList<Integer>();

        ArrayList<Integer> messageLengthCharsAll = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsSelf = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsSubscriber = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsUser = new ArrayList<Integer>();

        ArrayList<Integer> messageLengthWordsInResponse = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthWordsNotInResponse = new ArrayList<Integer>();

        ArrayList<Integer> messageLengthCharsInResponse = new ArrayList<Integer>();
        ArrayList<Integer> messageLengthCharsNotInResponse = new ArrayList<Integer>();

        Map<String, Integer> frequencyHistogramPublic = new HashMap<String, Integer>();
        Map<String, Integer> frequencyHistogramPrivate = new HashMap<String, Integer>();
        Map<String, Integer> frequencyHistogramProtected = new HashMap<String, Integer>();

        ArrayList<Integer> frequencyHistogramReponsePublic = new ArrayList<Integer>(Arrays.asList(0,0));
        ArrayList<Integer> frequencyHistogramReponsePrivate = new ArrayList<Integer>(Arrays.asList(0,0));
        ArrayList<Integer> frequencyHistogramReponseProtected = new ArrayList<Integer>(Arrays.asList(0,0));

        ArrayList<Integer> statusHistogram = new ArrayList<Integer>(Arrays.asList(0,0,0));
        ArrayList<Integer> inResponseHistogram = new ArrayList<Integer>(Arrays.asList(0,0));
        Map<Integer, Integer> messageLengthHistogram = new HashMap<Integer, Integer>();
        Map<String, Integer> recipientHistogram = new HashMap<String, Integer>();

        // Initialize map counters
        recipientHistogram.put(ALL, 0);
        recipientHistogram.put(SELF, 0);
        recipientHistogram.put(SUBSCRIBERS, 0);
        recipientHistogram.put(USER, 0);

        frequencyHistogramPublic.put(ALL, 0);
        frequencyHistogramPublic.put(SELF, 0);
        frequencyHistogramPublic.put(SUBSCRIBERS, 0);
        frequencyHistogramPublic.put(USER, 0);

        frequencyHistogramPrivate.put(ALL, 0);
        frequencyHistogramPrivate.put(SELF, 0);
        frequencyHistogramPrivate.put(SUBSCRIBERS, 0);
        frequencyHistogramPrivate.put(USER, 0);

        frequencyHistogramProtected.put(ALL, 0);
        frequencyHistogramProtected.put(SELF, 0);
        frequencyHistogramProtected.put(SUBSCRIBERS, 0);
        frequencyHistogramProtected.put(USER, 0);

        for (int i = 2; i <= 20; i++) {
            messageLengthHistogram.put(i, 0);
        }

        while (token != JsonToken.END_ARRAY) {
            token = parser.nextToken();
            if (token == JsonToken.START_OBJECT) {
                JsonNode node = parser.readValueAsTree();
                totalMessages++;

                String user = node.get("user").toString();
                if (!uniqueUsers.contains(user)) {
                    uniqueUsers.add(user);
                }

                String message = node.get("text").asText();
                int curCharLength = message.length();
                messageLengthChars.add(curCharLength);
                int curStrLength = message.split(" ").length;
                messageLengthWords.add(curStrLength);

                /** TR 3-3 */
                averageWordLength = runningAverage(averageWordLength, curStrLength, totalMessages);
                averageCharLength = runningAverage(averageCharLength, curCharLength, totalMessages);

                /**  Message Type Histogram */
                String messageStatus = node.get("status").asText();
                String messageRecepient = node.get("recepient").asText();
                JsonNode messageInResponse = node.get("in-response");

                /** TR 4-1 */
                switch (messageStatus) {
                    case PUBLIC:
                        publicMessages++;
                        statusHistogram.set(PUBLIC_INDEX, statusHistogram.get(PUBLIC_INDEX) + 1);
                        /** TR 5-1 */
                        messageLengthCharsPublic.add(curCharLength);
                        messageLengthWordsPublic.add(curStrLength);
                        averagePublicCharLength = runningAverage(averagePublicCharLength, curCharLength, publicMessages);
                        averageWordLengthPublic = runningAverage(averageWordLengthPublic, curStrLength, publicMessages);
                        /** TR 6-1 */
                        switch (messageRecepient) {
                            case ALL:
                                frequencyHistogramPublic.put(ALL, frequencyHistogramPublic.get(ALL) + 1);
                                break;
                            case SELF:
                                frequencyHistogramPublic.put(SELF, frequencyHistogramPublic.get(SELF) + 1);
                                break;
                            case SUBSCRIBERS:
                                frequencyHistogramPublic.put(SUBSCRIBERS, frequencyHistogramPublic.get(SUBSCRIBERS) + 1);
                                break;
                            default:
                                frequencyHistogramPublic.put(USER, frequencyHistogramPublic.get(USER) + 1);
                                break;
                        }
                        break;
                    case PRIVATE:
                        privateMessages++;
                        statusHistogram.set(PRIVATE_INDEX, statusHistogram.get(PRIVATE_INDEX) + 1);
                        /** TR 5-1 */
                        messageLengthCharsPrivate.add(curCharLength);
                        messageLengthWordsPrivate.add(curStrLength);
                        averagePrivateCharLength = runningAverage(averagePrivateCharLength, curCharLength, privateMessages);
                        averageWordLengthPrivate = runningAverage(averageWordLengthPrivate, curStrLength, privateMessages);
                        /** TR 6-1 */
                        switch (messageRecepient) {
                            case ALL:
                                frequencyHistogramPrivate.put(ALL, frequencyHistogramPrivate.get(ALL) + 1);
                                break;
                            case SELF:
                                frequencyHistogramPrivate.put(SELF, frequencyHistogramPrivate.get(SELF) + 1);
                                break;
                            case SUBSCRIBERS:
                                frequencyHistogramPrivate.put(SUBSCRIBERS, frequencyHistogramPrivate.get(SUBSCRIBERS) + 1);
                                break;
                            default:
                                frequencyHistogramPrivate.put(USER, frequencyHistogramPrivate.get(USER) + 1);
                                break;
                        }
                        break;
                    case PROTECTED:
                        protectedMessages++;
                        statusHistogram.set(PROTECTED_INDEX, statusHistogram.get(PROTECTED_INDEX) + 1);
                        /** TR 5-1 */
                        messageLengthCharsProtected.add(curCharLength);
                        messageLengthWordsProtected.add(curStrLength);
                        averageProtectedCharLength = runningAverage(averageProtectedCharLength, curCharLength, protectedMessages);
                        averageWordLengthProtected = runningAverage(averageWordLengthProtected, curStrLength, protectedMessages);
                        /** TR 6-1 */
                        switch (messageRecepient) {
                            case ALL:
                                frequencyHistogramProtected.put(ALL, frequencyHistogramProtected.get(ALL) + 1);
                                break;
                            case SELF:
                                frequencyHistogramProtected.put(SELF, frequencyHistogramProtected.get(SELF) + 1);
                                break;
                            case SUBSCRIBERS:
                                frequencyHistogramProtected.put(SUBSCRIBERS, frequencyHistogramProtected.get(SUBSCRIBERS) + 1);
                                break;
                            default:
                                frequencyHistogramProtected.put(USER, frequencyHistogramProtected.get(USER) + 1);
                                break;
                        }
                        break;
                    default:
                        printErrorMessage("Status not defined.");
                }

                /** TR 4-2 */
                switch (messageRecepient) {
                    case ALL:
                        allRecepients++;
                        recipientHistogram.put(ALL, recipientHistogram.get(ALL) + 1);
                        /** TR 5-2 */
                        messageLengthCharsAll.add(curCharLength);
                        messageLengthWordsAll.add(curStrLength);
                        averageCharLengthAll = runningAverage(averageCharLengthAll, curCharLength, allRecepients);
                        averageWordLengthAll = runningAverage(averageWordLengthAll, curStrLength, allRecepients);
                        break;
                    case SELF:
                        selfRecepients++;
                        recipientHistogram.put(SELF, recipientHistogram.get(SELF) + 1);
                        /** TR 5-2 */
                        messageLengthCharsSelf.add(curCharLength);
                        messageLengthWordsSelf.add(curStrLength);
                        averageCharLengthSelf = runningAverage(averageCharLengthSelf, curCharLength, selfRecepients);
                        averageWordLengthSelf = runningAverage(averageWordLengthSelf, curStrLength, selfRecepients);
                        break;
                    case SUBSCRIBERS:
                        subscriberRecepients++;
                        recipientHistogram.put(SUBSCRIBERS, recipientHistogram.get(SUBSCRIBERS) + 1);
                        /** TR 5-2 */
                        messageLengthCharsSubscriber.add(curCharLength);
                        messageLengthWordsSubscriber.add(curStrLength);
                        averageCharLengthSubscriber = runningAverage(averageCharLengthSubscriber, curCharLength, subscriberRecepients);
                        averageWordLengthSubscriber = runningAverage(averageWordLengthSubscriber, curStrLength, subscriberRecepients);
                        break;
                    default:
                        userRecepients++;
                        recipientHistogram.put(USER, recipientHistogram.get(USER) + 1);
                        /** TR 5-2 */
                        messageLengthCharsUser.add(curCharLength);
                        messageLengthWordsUser.add(curStrLength);
                        averageCharLengthUser = runningAverage(averageCharLengthUser, curCharLength, userRecepients);
                        averageWordLengthUser = runningAverage(averageWordLengthUser, curStrLength, userRecepients);
                        break;
                }

                /** TR 4-3 */
                if (messageInResponse != null) {
                    inResponseMessages++;
                    inResponseHistogram.set(IN_RESPONSE_INDEX, inResponseHistogram.get(IN_RESPONSE_INDEX) + 1);
                    /** TR 5-3 */
                    messageLengthCharsInResponse.add(curCharLength);
                    messageLengthWordsInResponse.add(curStrLength);
                    averageCharLengthInResponse = runningAverage(averageCharLengthInResponse, curCharLength, inResponseMessages);
                    averageWordLengthInResponse = runningAverage(averageWordLengthInResponse, curStrLength, inResponseMessages);
                } else {
                    notInResponseMessages++;
                    inResponseHistogram.set(NOT_IN_RESPONSE_INDEX, inResponseHistogram.get(NOT_IN_RESPONSE_INDEX) + 1);
                    /** TR 5-3 */
                    messageLengthCharsNotInResponse.add(curCharLength);
                    messageLengthWordsNotInResponse.add(curStrLength);
                    averageCharLengthNotInResponse = runningAverage(averageCharLengthNotInResponse, curCharLength, notInResponseMessages);
                    averageWordLengthNotInResponse = runningAverage(averageWordLengthNotInResponse, curStrLength, notInResponseMessages);
                }

                /** TR 4-4 */
                messageLengthHistogram.put(curStrLength, messageLengthHistogram.get(curStrLength) + 1);
            }
        }

        JSONObject json = new JSONObject();

        /** TR 3 **/
        JSONObject jsonBasicInfo = new JSONObject();
        System.out.println("========== Basic Stats ==========");
        /** TR 3-1 **/
        System.out.println(" - Total number of messages reported: " + totalMessages);
        jsonBasicInfo.put("totalMessages", totalMessages);
        /** TR 3-2 **/
        System.out.println(" - Total number of unique users who authored messages: " + uniqueUsers.size());
        jsonBasicInfo.put("uniqueAuthors", uniqueUsers.size());
        /** TR 3-3 **/
        double wordStdDev = getStdDev(messageLengthWords, averageWordLength);
        System.out.println(" - Average length of message (words): " + averageWordLength);
        System.out.println(" - Std dev for length of message (words): " + wordStdDev);
        jsonBasicInfo.put("wordAverage", averageWordLength);
        jsonBasicInfo.put("wordStdDev", wordStdDev);

        double charStdDev = getStdDev(messageLengthChars, averageCharLength);
        System.out.println(" - Average length of message (characters): " + averageCharLength);
        System.out.println(" - Std dev for length of message (words): " + charStdDev);
        jsonBasicInfo.put("characterAverage", averageCharLength);
        jsonBasicInfo.put("characterStdDev", charStdDev);

        json.put("basicInfo", jsonBasicInfo);

        /** TR 4 **/
        JSONObject jsonMessageTypeHistograms = new JSONObject();
        System.out.println("========== Message Type Histograms ==========");

        /** TR 4-1 **/
        JSONObject jsonStatusHistograms = new JSONObject();
        System.out.println(" - Distrubtion of number of messages by status:");
        System.out.println("\t\tValue\t\t\tFrequency");
        System.out.println("\t\t" + PUBLIC + "\t\t\t" + statusHistogram.get(PUBLIC_INDEX));
        System.out.println("\t\t" + PRIVATE + "\t\t\t" + statusHistogram.get(PRIVATE_INDEX));
        System.out.println("\t\t" + PROTECTED + "\t\t\t" + statusHistogram.get(PROTECTED_INDEX));

        jsonStatusHistograms.put("public", statusHistogram.get(PUBLIC_INDEX));
        jsonStatusHistograms.put("private", statusHistogram.get(PRIVATE_INDEX));
        jsonStatusHistograms.put("protected", statusHistogram.get(PROTECTED_INDEX));
        jsonMessageTypeHistograms.put("statusDistribution", jsonStatusHistograms);

        /** TR 4-2 **/
        JSONObject jsonRecipientHistograms = new JSONObject();
        System.out.println(" - Distrubtion of number of messages by recipient:");
        System.out.println("\t\tValue\t\t\tFrequency");
        System.out.println("\t\t" + ALL + "\t\t\t" + recipientHistogram.get(ALL));
        System.out.println("\t\t" + SELF + "\t\t\t" + recipientHistogram.get(SELF));
        System.out.println("\t\t" + SUBSCRIBERS + "\t\t\t" + recipientHistogram.get(SUBSCRIBERS));
        System.out.println("\t\t" + USER + "\t\t\t" + recipientHistogram.get(USER));

        jsonRecipientHistograms.put("all", recipientHistogram.get(ALL));
        jsonRecipientHistograms.put("self", recipientHistogram.get(SELF));
        jsonRecipientHistograms.put("subscribers", recipientHistogram.get(SUBSCRIBERS));
        jsonRecipientHistograms.put("users", recipientHistogram.get(USER));
        jsonMessageTypeHistograms.put("messageDistribution", jsonRecipientHistograms);

        /** TR 4-3 **/
        JSONObject jsonResponseHistograms = new JSONObject();
        System.out.println(" - Distrubtion of number of messages by response type:");
        System.out.println("\t\tValue\t\t\tFrequency");
        System.out.println("\t\t'in-response'\t\t\t" + inResponseHistogram.get(IN_RESPONSE_INDEX));
        System.out.println("\t\tNot 'in-response'\t\t\t" + inResponseHistogram.get(NOT_IN_RESPONSE_INDEX));

        jsonResponseHistograms.put("inResponse", inResponseHistogram.get(IN_RESPONSE_INDEX));
        jsonResponseHistograms.put("notInResponse", inResponseHistogram.get(NOT_IN_RESPONSE_INDEX));
        jsonMessageTypeHistograms.put("responseDistribution", jsonResponseHistograms);

        /** TR 4-4 **/
        JSONObject jsonMessageLengthHistograms = new JSONObject();
        System.out.println(" - Distrubtion of number of words in a message:");
        System.out.println("\t\tValue\t\t\tFrequency");
        for (Map.Entry<Integer, Integer> entry : messageLengthHistogram.entrySet()) {
            System.out.println("\t\t" + entry.getKey() + "\t\t\t\t" + entry.getValue());
            jsonMessageLengthHistograms.put(entry.getKey().toString(), entry.getValue());
        }
        jsonMessageTypeHistograms.put("messageLengthDistribution", jsonMessageLengthHistograms);

        json.put("messageTypeHistograms", jsonMessageTypeHistograms);


        /** TR 5 **/
        JSONObject jsonMessageSubsets = new JSONObject();
        System.out.println("========== Message Subsets ==========");

        /** TR 5-1 **/
        JSONObject jsonMessageStatusStats = new JSONObject();
        System.out.println(" - Status statistics");

        /** TR 5-1-1 **/
        JSONObject jsonMessagePublicStatusStats = new JSONObject();
        System.out.println("\t - " + PUBLIC);

        wordStdDev = getStdDev(messageLengthWordsPublic, averageWordLengthPublic);
        System.out.println("\t\t\t Average (words): " + averageWordLengthPublic);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessagePublicStatusStats.put("wordAverage", averageWordLengthPublic);
        jsonMessagePublicStatusStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthWordsPublic, averagePublicCharLength);
        System.out.println("\t\t\t Average (characters): " + averagePublicCharLength);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessagePublicStatusStats.put("characterAverage", averagePublicCharLength);
        jsonMessagePublicStatusStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("public", jsonMessagePublicStatusStats);

        /** TR 5-1-2 **/
        JSONObject jsonMessagePrivateStatusStats = new JSONObject();
        System.out.println("\t - " + PRIVATE);

        wordStdDev = getStdDev(messageLengthWordsPrivate, averageWordLengthPrivate);
        System.out.println("\t\t\t Average (words): " + averageWordLengthPrivate);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessagePrivateStatusStats.put("wordAverage", averageWordLengthPrivate);
        jsonMessagePrivateStatusStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsPrivate, averagePrivateCharLength);
        System.out.println("\t\t\t Average (characters): " + averagePrivateCharLength);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessagePrivateStatusStats.put("characterAverage", averagePrivateCharLength);
        jsonMessagePrivateStatusStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("private", jsonMessagePrivateStatusStats);

        /** TR 5-1-3 **/
        JSONObject jsonMessageProtectedStatusStats = new JSONObject();
        System.out.println("\t - " + PROTECTED);

        wordStdDev = getStdDev(messageLengthWordsProtected, averageWordLengthProtected);
        System.out.println("\t\t\t Average (words): " + averageWordLengthProtected);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessageProtectedStatusStats.put("wordAverage", averageWordLengthProtected);
        jsonMessageProtectedStatusStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthWordsProtected, averageProtectedCharLength);
        System.out.println("\t\t\t Average (characters): " + averageProtectedCharLength);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessageProtectedStatusStats.put("characterAverage", averageProtectedCharLength);
        jsonMessageProtectedStatusStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("protected", jsonMessageProtectedStatusStats);
        jsonMessageSubsets.put("statusStats", jsonMessageStatusStats);


        /** TR 5-2**/
        JSONObject jsonMessageRecipientStats = new JSONObject();
        System.out.println(" - Recipient statistics");

        /** TR 5-2-1 **/
        JSONObject jsonMessageAllRecipientStats = new JSONObject();
        System.out.println("\t - " + ALL);

        wordStdDev = getStdDev(messageLengthWordsAll, averageWordLengthAll);
        System.out.println("\t\t\t Average (words): " + averageWordLengthAll);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessageAllRecipientStats.put("wordAverage", averageWordLengthAll);
        jsonMessageAllRecipientStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsAll, averageCharLengthAll);
        System.out.println("\t\t\t Average (characters): " + averageCharLengthAll);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessageAllRecipientStats.put("characterAverage", averageCharLengthAll);
        jsonMessageAllRecipientStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("all", jsonMessageAllRecipientStats);

        /** TR 5-2-2 **/
        JSONObject jsonMessageSelfRecipientStats = new JSONObject();
        System.out.println("\t - " + SELF);

        wordStdDev = getStdDev(messageLengthWordsSelf, averageWordLengthSelf);
        System.out.println("\t\t\t Average (words): " + averageWordLengthSelf);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessageSelfRecipientStats.put("wordAverage", averageWordLengthSelf);
        jsonMessageSelfRecipientStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsSelf, averageCharLengthSelf);
        System.out.println("\t\t\t Average (characters): " + averageCharLengthSelf);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessageSelfRecipientStats.put("characterAverage", averageCharLengthSelf);
        jsonMessageSelfRecipientStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("self", jsonMessageSelfRecipientStats);

        /** TR 5-2-3 **/
        JSONObject jsonMessageSubscribersRecipientStats = new JSONObject();
        System.out.println("\t - " + SUBSCRIBERS);

        wordStdDev = getStdDev(messageLengthWordsSubscriber, averageWordLengthSubscriber);
        System.out.println("\t\t\t Average (words): " + averageWordLengthSubscriber);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessageSubscribersRecipientStats.put("wordAverage", averageWordLengthSubscriber);
        jsonMessageSubscribersRecipientStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsSubscriber, averageCharLengthSubscriber);
        System.out.println("\t\t\t Average (characters): " + averageCharLengthSubscriber);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessageSubscribersRecipientStats.put("characterAverage", averageCharLengthSubscriber);
        jsonMessageSubscribersRecipientStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("subscribers", jsonMessageSubscribersRecipientStats);

        /** TR 5-2-4 **/
        JSONObject jsonMessageUserRecipientStats = new JSONObject();
        System.out.println("\t - " + USER);

        wordStdDev = getStdDev(messageLengthWordsUser, averageWordLengthUser);
        System.out.println("\t\t\t Average (words): " + averageWordLengthUser);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessageUserRecipientStats.put("wordAverage", averageWordLengthUser);
        jsonMessageUserRecipientStats.put("wordStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsUser, averageCharLengthUser);
        System.out.println("\t\t\t Average (characters): " + averageCharLengthUser);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessageUserRecipientStats.put("characterAverage", averageCharLengthUser);
        jsonMessageUserRecipientStats.put("characterStdDev", charStdDev);

        jsonMessageStatusStats.put("users", jsonMessageUserRecipientStats);
        jsonMessageSubsets.put("recipientStats", jsonMessageRecipientStats);

        /** TR 5-3 **/
        JSONObject jsonMessageResponseStats = new JSONObject();
        System.out.println(" - Response statistics");

        /** TR 5-3-1 **/
        JSONObject jsonMessagesInResponseStats = new JSONObject();
        System.out.println("\t - in-response");

        wordStdDev = getStdDev(messageLengthWordsInResponse, averageWordLengthInResponse);
        System.out.println("\t\t\t Average (words): " + averageWordLengthInResponse);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessagesInResponseStats.put("characterAverage", averageWordLengthInResponse);
        jsonMessagesInResponseStats.put("characterStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsInResponse, averageCharLengthInResponse);
        System.out.println("\t\t\t Average (characters): " + averageCharLengthInResponse);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessagesInResponseStats.put("characterAverage", averageCharLengthInResponse);
        jsonMessagesInResponseStats.put("characterStdDev", charStdDev);

        jsonMessageResponseStats.put("inResponse", jsonMessagesInResponseStats);

        /** TR 5-3-2 **/
        JSONObject jsonMessagesNotResponseStats = new JSONObject();
        System.out.println("\t - Not in-response");

        wordStdDev = getStdDev(messageLengthWordsNotInResponse, averageWordLengthNotInResponse);
        System.out.println("\t\t\t Average (words): " + averageWordLengthNotInResponse);
        System.out.println("\t\t\t Standard Deviation (words): " + wordStdDev);
        jsonMessagesNotResponseStats.put("characterAverage", averageWordLengthNotInResponse);
        jsonMessagesNotResponseStats.put("characterStdDev", wordStdDev);

        charStdDev = getStdDev(messageLengthCharsNotInResponse, averageCharLengthNotInResponse);
        System.out.println("\t\t\t Average (characters): " + averageCharLengthNotInResponse);
        System.out.println("\t\t\t Standard Deviation (characters): " + charStdDev);
        jsonMessagesNotResponseStats.put("characterAverage", averageCharLengthNotInResponse);
        jsonMessagesNotResponseStats.put("characterStdDev", charStdDev);

        jsonMessageResponseStats.put("notInResponse", jsonMessagesNotResponseStats);
        jsonMessageSubsets.put("responseStats", jsonMessageResponseStats);

        json.put("messageSubsets", jsonMessageSubsets);


    }

    private double getStdDev(ArrayList<Integer> data, double mean) {
        double sum = 0;

        for (int i = 0; i < data.size(); i++) {
            sum += Math.pow(data.get(i) - mean, 2);
        }
        sum = data.size() != 0 ? sum / data.size() : 0;

        return Math.pow(sum, 0.5);
    }

    public double runningAverage(double curAvg, int curCount, int size) {
        curAvg -= curAvg / size;
        curAvg += (double)curCount / size;

        return curAvg;
    }

    private void printErrorMessage(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static void main(String[] args) throws IOException, JSONException {
        thghtShreStats main = new thghtShreStats();
        main.init(args);
    }
}
