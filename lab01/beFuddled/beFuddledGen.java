import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONException;

public class beFuddledGen {
    private static int MAX_PLAYERS = 10000;

    /**
     * Randomly generate a game id that is either currently playing
     * or does not exist.
     *
     * @param currentGameIds A list of the games that can be selected
     * @param numGames       The number of games that have been created
     * @return The game's id
     **/
    private int getGameId(ArrayList<Integer> currentGameIds, int numGames) {
        int gameId;
        Random rand = new Random();

        gameId = currentGameIds.get(rand.nextInt(currentGameIds.size()));
        if (gameId == numGames) {
            currentGameIds.add(numGames + 1);
        }

        return gameId;
    }

    /**
     * Create a new game and assign a player to it.
     *
     * @param gameId         The current gameId
     * @param currentPlayers An ArrayList of the current players
     * @return The newly created game
     **/
    private Game generateGame(int gameId, ArrayList<String> currentPlayers) {
        String player;
        Random rand = new Random();

        do {
            player = "u" + rand.nextInt(MAX_PLAYERS) + 1;
        } while (currentPlayers.contains(player));
        currentPlayers.add(player);

        return new Game(gameId, player);
    }

    /**
     * Pick a random value between 1 and 20 using a normal curve
     * with a mean of 10 and standard deviation of 5.
     *
     * @return An integer between 1 and 20
     **/
    private int pickLocation() {
        int value, min = 1, max = 20;
        Random rand = new Random();

        value = (int) Math.max(min, Math.min(max, Math.round(rand.nextGaussian() * 5 + 10)));

        return value;
    }

    /**
     * Generate an action for a game and update its state.
     * The possible moves are: GameStart, GameEnd, Move, SpecialMove.
     *
     * @param game           Game for which to generate an action and update.
     * @param currentGameIds List of all the games that are in action.
     * @param currentPlayers List of all the players currently playing a game.
     * @return The action as a JSONObject
     **/
    private JSONObject generateAction(Game game, ArrayList<Integer> currentGameIds,
                                      ArrayList<String> currentPlayers) throws JSONException {
        JSONObject action = new JSONObject();
        Random rand = new Random();

        action.put("actionNumber", game.currentAction);
        if (game.currentAction == 1) {
            // Start new game
            action.put("actionType", "GameStart");
            game.incrementCurrentAction();
        } else if (game.currentAction == game.maxActions) {
            // End game
            action.put("actionType", "GameEnd");
            action.put("points", game.playerPoints);
            String gameStatus = rand.nextInt(2) == 0 ? "Win" : "Loss";
            action.put("gameStatus", gameStatus);
            game.setFinished();
            // Make sure to remove the gameId and the current player
            currentGameIds.remove(new Integer(game.id));
            currentPlayers.remove(game.player);
        } else {
            // 5% chance to perform a special move
            int i = rand.nextInt(100) + 1;
            if (i <= 5 && !game.allSpecialsUsed()) {
                // special move
                action.put("actionType", "SpecialMove");
                action.put("move", game.pickSpecialMove());

                int points = rand.nextInt(41) - 20; // -20 to 20 points
                action.put("pointsAdded", points);
                game.addToPoints(points);
                action.put("points", game.playerPoints);

            } else {
                // regular move
                action.put("actionType", "Move");

                JSONObject location = new JSONObject();
                int locationX = pickLocation();
                int locationY = pickLocation();
                location.put("x", locationX);
                location.put("y", locationY);
                action.put("location", location);

                int points = rand.nextInt(41) - 20; // -20 to 20 points
                action.put("pointsAdded", points);
                game.addToPoints(points);
                action.put("points", game.playerPoints);

            }
            game.incrementCurrentAction();
        }

        return action;
    }

    /**
     * Main function that logs game actions to a file.
     *
     * @param args command line arguments
     **/
    private void startLoggingGames(String[] args) throws JSONException, IOException {
        String fileName = args[0];
        FileWriter writer = new FileWriter(fileName);
        int jsonObjCount = Integer.parseInt(args[1]),
                numGames = 1;
        Map<Integer, Game> games = new HashMap<Integer, Game>();
        ArrayList<Integer> currentGameIds = new ArrayList<Integer>();
        ArrayList<String> currentPlayers = new ArrayList<>();
        currentGameIds.add(1);
        writer.write("[");

        for (int logCnt = 1; logCnt <= jsonObjCount; logCnt++) {
            int gameId = getGameId(currentGameIds, numGames);
            Game game = games.get(gameId);
            if (game == null) {
                // Create a new game
                game = generateGame(gameId, currentPlayers);
                numGames++;
            }
            if (game.currentAction > game.maxActions) {
                throw new RuntimeException("WHAT IS GOING ON");
            }

            // Generate random data and add it to the log record
            JSONObject logRecord = new JSONObject();
            logRecord.put("user", game.player);
            logRecord.put("game", gameId);
            logRecord.put("action", generateAction(game, currentGameIds, currentPlayers));

            logRecord.write(writer);   // add log record to output file
            if (logCnt != jsonObjCount) {
                writer.write(",");
            }
            games.put(gameId, game);  // update games map
        }

        writer.write("]");
        writer.close();
    }

    public static void main(String[] args) throws JSONException, IOException {
        beFuddledGen main = new beFuddledGen();
        main.startLoggingGames(args);
    }
}
