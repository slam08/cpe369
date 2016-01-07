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
  /** Nested class to save game state. */
  private class Game {
    public boolean finished;
    public String player;
    public Integer playerPoints;
    public Integer currentAction, maxActions;
    private Random rand;
    public boolean usedShuffle;
    public boolean usedClear;
    public boolean usedInvert;
    public boolean usedRotate;

    /** Initialize fields */
    public Game() {
      finished = false;
      playerPoints = 0;
      currentAction = 1;
      rand = new Random();
      maxActions = generateMaxActions();
      usedShuffle = false;
      usedClear = false;
      usedInvert = false;
      usedRotate = false;
    } 

    /**
     * Randomly pick a special move with the following percentages:
     *   - Shuffle: 0.33
     *   - Clear: 0.23
     *   - Invert: 0.22
     *   - Rotate: 0.22
     *
     *  @return
     *    The name of the special move used.
     **/
    public String pickSpecialMove() {
      boolean reroll;
      String move = new String();

      do {
        int value = this.rand.nextInt(100);
        reroll = false;
        if (!usedShuffle && value < 33) {
          move = "Shuffle";
          usedShuffle = true;
        } else if (!usedClear && value >= 33 && value < 56) {
          move = "Clear";
          usedClear = true;
        } else if (!usedInvert && value >= 56 && value < 78) {
          move = "Invert";
          usedClear = true;
        } else if (!usedRotate && value >= 78 && value < 100) {
          move = "Rotate";
          usedRotate = true;
        } else {
          reroll = true;
        }
      } while (reroll);
      
      return move;
    } 

    /**
     * Generate a random value using a normal curve 
     * with a mean of 45 and standard deviation of 15.
     *
     * @return
     *   An integer between 9 and 100
     **/
    private int generateMaxActions() {
      int i;

      do {
        i = (int) Math.round(this.rand.nextGaussian() * 15 + 45);
      } while (i < 9 || i > 100);

      return i;
    }

    public void incrementCurrentAction() {
      this.currentAction++;
    }

    public void addToPoints(int amount) {
      this.playerPoints += amount;
    }

    public void setPlayer(String player) {
      this.player = player;
    }

    public void setFinished() {
      this.finished = true;
    }

    /** Print the current fields of the game for debugging purposes. */
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
   * Create a new game and assign a player to it.
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
    game.setPlayer(player);

    return game;
  }

  /**
   * Pick a random value between 1 and 20 using a normal curve  
   * with a mean of 10 and standard deviation of 5.
   *
   * @return
   *   An integer between 1 and 20
   **/
  private int pickLocation() {
    int value;
    Random rand = new Random();

    do {
      value = (int) Math.round(rand.nextGaussian() * 5 + 10);  
    } while (value < 1 || value > 20);

    return value;
  }

  /**
   * Generate an action for a game and update its state.
   * The possible moves are: GameStart, GameEnd, Move, SpecialMove.
   *
   * @param game
   *   Game for which to generate an action and update.
   * @return 
   *   The action as a JSONObject
   **/
  private JSONObject generateAction(Game game) throws JSONException {
    JSONObject action = new JSONObject();
    Random rand = new Random();

    action.put("actionNumber", game.currentAction);
    if (game.currentAction == 1) {
      // Start new game
      action.put("actionType", "GameStart");
      game.incrementCurrentAction();
    } else if (game.currentAction >= game.maxActions) {
      // End game
      action.put("actionType", "GameEnd");
      action.put("points", game.playerPoints);
      String gameStatus = rand.nextInt(2) == 0 ? "Win" : "Loss";
      action.put("gameStatus", gameStatus);
      game.setFinished();
    } else {
      // 5% chance to perform a special move
      int i = rand.nextInt(100) + 1;
      if (i <= 5) {
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
      Game game = games.get(gameId);
      if (game == null) {
        // Create a new game
        game = generateGame(games);
        numGames++;
      }

      // Generate random data and add it to the log record
      JSONObject logRecord = new JSONObject();
      logRecord.put("user", game.player);
      logRecord.put("game", gameId);
      logRecord.put("action", generateAction(game));

      jsonArr.put(logRecord);   // add log record to json array
      games.put(gameId, game);  // update games map
      // game.printContents();   // debug
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