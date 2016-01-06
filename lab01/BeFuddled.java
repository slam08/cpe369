import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;

public class BeFuddled {
  private class Game {
    public boolean finished;
    public String player;
    public Integer playerPoints;
    public Integer currentAction, maxActions;
    private Random rand;

    public Game() {
      finished = false;
      playerPoints = 0;
      currentAction = 1;
      rand = new Random();
      maxActions = generateMaxActions();
    } 

    // Predetermined last action
    private Integer generateMaxActions() {
      Integer i = this.rand.nextInt(10) + 1;

      if (i == 1) {
        // 10% chance to return a value between 9 and 40
        return this.rand.nextInt(31) + 9;
      } else if (i == 10) {
        // 10% chance to return a value between 70 and 100
        return this.rand.nextInt(30) + 70;
      } else {
        // 80% chance to return a value between 40 and 60
        return this.rand.nextInt(20) + 40;
      }
    }

    public void printContents() {
      System.out.println("--------------------");
      System.out.println("player: " + this.player);
      System.out.println("playerPoints: " + this.playerPoints);
      System.out.println("currentAction: " + this.currentAction);
      System.out.println("maxActions: " + this.maxActions);
      System.out.println("--------------------");
    }
  }

  private static int MAX_PLAYERS = 10000;

  /**
   * Randomly generate a game id that is either currently playing
   * or does not exist.
   *
   * @param existingGames
   *   A map of games that have already been created
   * @param numGames
   *   The number of games that have been created
   * @return
   *   The game's id
   **/
  private Integer getGameId(Map<Integer, Game> existingGames, 
                            Integer numGames) {
    Integer gameId;
    Random rand = new Random();

    do {
      gameId = rand.nextInt(numGames + 1) + 1;
    } while (existingGames.get(gameId) != null && 
             existingGames.get(gameId).finished);
    assert(existingGames.get(gameId) == null || 
           !existingGames.get(gameId).finished);
  
    return gameId;
  }
  
  /**
   * Check if a player is currently playing a game.
   *
   * @param existingGames
   *   A map of games that have already been created
   * @param player
   *   The player id to check
   * @return 
   *   A boolean value
   *   True if the player is currently playing a game
   *   False if the player is not currently playing a game
   **/
  private boolean alreadyPlayingGame(Map<Integer, Game> existingGames, 
                                     String player) {
    for (Map.Entry<Integer, Game> gameEntry : existingGames.entrySet()) {
      Game game = gameEntry.getValue();

      if (game.player == player && !game.finished) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create a new game and assign it two randomly generated player ids.
   *
   * @param existingGames
   *   A map of games that have already been created
   * @return 
   *   The newly created game
   **/
  private Game generateGame(Map<Integer, Game> existingGames) {
    String player;
    Integer playerId;
    Random rand = new Random();
    Game game = new Game();

    do {
      playerId = rand.nextInt(MAX_PLAYERS) + 1;
      player = "u" + playerId.toString();
    } while (alreadyPlayingGame(existingGames, player));
    game.player = player;

    return game;
  }

  /**
   * Main function that logs game actions to a file.
   *
   * @param args
   *   command line arguments
   **/
  private void startLoggingGames(String[] args) throws 
      JSONException, IOException {
    String fileName = args[0];
    Integer jsonObjCount = Integer.parseInt(args[1]),
            numGames = 0,
            numLogs = 0;
    Random rand = new Random();
    Map<Integer, Game> games = new HashMap<Integer, Game>();
    JSONArray jsonArr = new JSONArray();
   
    for (int logCnt = 1; logCnt <= jsonObjCount; logCnt++) {
      Integer gameId = getGameId(games, numGames);

      // Create a new game and add to map
      Game game = games.get(gameId);
      if (game == null) {
        game = generateGame(games);
      }

      JSONObject logRecord = new JSONObject();
      logRecord.put("user", game.player);
      logRecord.put("game", gameId);

      JSONObject action = new JSONObject();
      if (game.currentAction == 1) {
        // Start new game
        action.put("actionNumber", game.currentAction);
        action.put("actionType", "GameStart");
      } else if (game.currentAction >= game.maxActions) {
        // End game
        action.put("actionNumber", game.currentAction);
        action.put("actionType", "GameEnd");
        action.put("points", game.playerPoints);
        String gameStatus = rand.nextInt(2) == 0 ? "Win" : "Loss";
        action.put("gameStatus", gameStatus);
      } else {
        // Perform regular or special move
      }
      logRecord.put("action", action);
      jsonArr.put(logRecord);
      game.currentAction++;
      game.printContents();
      games.put(gameId, game);
      numGames++;
    }

    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write(jsonArr.toString(2));
    }
  }

  public static void main(String[] args) throws JSONException, IOException {
    BeFuddled main = new BeFuddled();
    main.startLoggingGames(args);
  }

  
}
