package lab1;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class beFuddledStats {

    public static final String GAME_START = "GameStart";
    public static final String GAME_END = "GameEnd";
    public static final String SPECIAL = "SpecialMove";
    public static final String MOVE = "Move";

    private class GameStats {
        private int numStarted;
        private int wins;
        private int losses;

        public GameStats() {
            numStarted = 0;
            wins = 0;
            losses = 0;
        }

        public int getNumStarted() {
            return numStarted;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public void addStart() {
            numStarted++;
        }

        public void addWin() {
            wins++;
        }

        public void addLoss() {
            losses++;
        }

        public int getCompleted() {
            return wins + losses;
        }
    }

    private class CompletedStats {
        private boolean won;
        private int points;

        public CompletedStats(final boolean won, final int points) {
            this.won = won;
            this.points = points;
        }

        public boolean wonGame() {
            return won;
        }

        public int getPoints() {
            return points;
        }
    }

    public void init(String[] args) throws IOException, JSONException {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Improper arguments. Use the following structure:");
            System.err.println("... beFuddledStats <inputFile> (opt:<outputFile>)");
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

        //Maps to fill while parsing
        HashMap<String, GameStats> userGames = new HashMap<String, GameStats>();
        ArrayList<CompletedStats> completedStatsArrayList = new ArrayList<CompletedStats>();
        HashMap<String, Integer> moves = new HashMap<String, Integer>();
        HashMap<String, Integer> specialMoves = new HashMap<String, Integer>();

        //Counters for variables
        int completedGames = 0;
        int started = 0;
        int wins = 0;
        int losses = 0;

        int winsCompletedPoints = 0;
        int lossesCompletedPoints = 0;
        // Total, Wins, Losses
        ArrayList<Double> completedPointsMean = new ArrayList<>(3);

        int numUsersStartedGame = 0;

        int numUsersCompletedGame = 0;

        int maxGamesStarted = 0;
        ArrayList<String> maxGamesStartedUsers = new ArrayList<String>();

        int maxGamesCompleted = 0;
        ArrayList<String> maxGamesCompletedUsers = new ArrayList<String>();

        int maxWins = 0;
        ArrayList<String> maxWinsUsers = new ArrayList<String>();

        int maxLosses = 0;
        ArrayList<String> maxLossesUsers = new ArrayList<String>();

        while (token != JsonToken.END_ARRAY) {
            token = parser.nextToken();
            if (token == JsonToken.START_OBJECT) {
                JsonNode node = parser.readValueAsTree();
                String user = null;
                try {
                    user = node.get("user").asText();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    improperFormatOutput();
                }
                // Create game statistics if they don't exist for that user
                if (userGames.get(user) == null) {
                    userGames.put(user, new GameStats());
                }
                // Get the game statistics for the current user
                GameStats gameStats = userGames.get(user);
                JsonNode action = node.get("action");
                try {
                    switch (action.get("actionType").asText()) {
                        case MOVE:
                            break;
                        case GAME_START:
                            started++;
                            gameStats.addStart();

                            // Calculate the max games started and its users
                            if (gameStats.getNumStarted() == maxGamesStarted) {
                                maxGamesStartedUsers.add(user);
                            } else if (gameStats.getNumStarted() > maxGamesStarted) {
                                maxGamesStartedUsers.clear();
                                maxGamesStartedUsers.add(user);
                                maxGamesStarted = gameStats.getNumStarted();
                            }
                            break;
                        case GAME_END:
                            int points = action.get("points").asInt();
                            if (action.get("gameStatus").asText().equals("Win")) {
                                gameStats.addWin();
                                wins++;
                                completedStatsArrayList.add(new CompletedStats(true, points));
                                winsCompletedPoints += points;
                            } else {
                                gameStats.addLoss();
                                losses++;
                                completedStatsArrayList.add(new CompletedStats(false, points));
                                lossesCompletedPoints += points;
                            }

                            // Calculate the max wins and its users
                            if (gameStats.getWins() == maxWins) {
                                maxWinsUsers.add(user);
                            } else if (gameStats.getWins() > maxWins) {
                                maxWinsUsers.clear();
                                maxWinsUsers.add(user);
                                maxWins = gameStats.getWins();
                            }

                            // Calculate the max losses and its users
                            if (gameStats.getLosses() == maxLosses) {
                                maxLossesUsers.add(user);
                            } else if (gameStats.getLosses() > maxLosses) {
                                maxLossesUsers.clear();
                                maxLossesUsers.add(user);
                                maxLosses = gameStats.getLosses();
                            }

                            // Calculate the max games completed and its users
                            if (gameStats.getCompleted() == maxGamesCompleted) {
                                maxGamesCompletedUsers.add(user);
                            } else if (gameStats.getCompleted() > maxGamesCompleted) {
                                maxGamesCompletedUsers.clear();
                                maxGamesCompletedUsers.add(user);
                                maxGamesCompleted = gameStats.getCompleted();
                            }

                            if (gameStats.getCompleted() == 1) {
                                completedGames++;
                            }
                            break;
                        case SPECIAL:
                            break;
                        default:
                            System.err.println("Action type does not exist.");
                            System.exit(1);
                    }
                } catch (NullPointerException e) {
                    improperFormatOutput();
                }
            }
        }

        completedPointsMean.add(0, (double)(winsCompletedPoints + lossesCompletedPoints) / (wins + losses));
        completedPointsMean.add(1, (double)(winsCompletedPoints) / wins);
        completedPointsMean.add(2, (double)(lossesCompletedPoints) / losses);

        ArrayList<Double> stddevs = getCompletedStandardDeviations(completedStatsArrayList, completedPointsMean,
                new ArrayList<Integer>(Arrays.asList(wins+losses, wins, losses)));

        System.out.println("=============== GAME ================");
        System.out.println(" - Total games seen: " + started);
        System.out.println(" - Total games completed: " + wins + losses);
        System.out.println(" - Total games won: " + wins);
        System.out.println(" - Total games lost: " + losses);


        System.out.println("=============== USERS ===============");
        System.out.println(" - Total users who started at least one game: " + userGames.size());
        System.out.println(" - Total users who completed at least one game: " + completedGames);
        System.out.println(" - Largest number of games user started: " + maxGamesStarted);
        System.out.println("       Corresponding users: " + maxGamesStartedUsers);
        System.out.println(" - Largest number of games a user completed: " + maxGamesCompleted);
        System.out.println("       Corresponding users: " + maxGamesCompletedUsers);
        System.out.println(" - Max wins a single user had: " + maxWins);
        System.out.println("       Corresponding users: " + maxWinsUsers);
        System.out.println(" - Max losses a single user had: " + maxLosses);
        System.out.println("       Corresponding users: " + maxLossesUsers);

        // write out to the specified file
        if (args.length > 1) {
            String outputFile = args[1];
            FileWriter writer = new FileWriter(outputFile);
        }
        // print a bunch of stuff
    }

    private void improperFormatOutput() {
        System.err.println("Improper format of beFuddled game.");
        System.exit(1);
    }

    private ArrayList<Double> getCompletedStandardDeviations(ArrayList<CompletedStats> completedStatsArrayList,
                                                             ArrayList<Double> means,
                                                             ArrayList<Integer> totals) {
        // All, wins, losses
        ArrayList<Double> stddevs = new ArrayList<Double>();
        double totalSum = 0;
        double winSum = 0;
        double lossSum = 0;

        for (int i = 0; i < completedStatsArrayList.size(); i++) {
            CompletedStats stats = completedStatsArrayList.get(i);
            if (stats.wonGame()) {
                winSum += Math.pow(stats.getPoints() - means.get(1), 2);
            } else {
                lossSum += Math.pow(stats.getPoints() - means.get(2), 2);
            }
            totalSum += Math.pow(stats.getPoints() - means.get(0), 2);
        }
        totalSum = totalSum / totals.get(0);
        winSum = winSum / totals.get(1);
        lossSum = lossSum / totals.get(2);

        stddevs.add(0, Math.pow(totalSum, 0.5));
        stddevs.add(1, Math.pow(winSum, 0.5));
        stddevs.add(2, Math.pow(lossSum, 0.5));
        return stddevs;
    }


    public static void main(String[] args) throws IOException, JSONException {
        beFuddledStats main = new beFuddledStats();
        main.init(args);
    }

}
