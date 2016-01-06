import java.util.Map;
import java.util.HashMap;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class BeFuddled {
  private class Game {
    public boolean finished;
    public String player1, player2;
    public Integer player1Points, player2Points;
    public Integer currentAction;
    private Random rand;

    public Game() {
      finished = false;
      player1Points = 0;
      player2Points = 0;
      currentAction = 1;
      rand = new Random();
    } 

    public String pickPlayer() {
      return this.rand.nextInt(2) == 0 ? this.player1 : this.player2;
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
             existingGames.get(gameId).finished == true);
    assert(existingGames.get(gameId) == null || 
           existingGames.get(gameId).finished == false);
    System.out.println("gameId: " + gameId);
  
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

      if (game.finished == false && 
          (game.player1 == player || game.player2 == player)) {
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
    String player1, player2;
    Integer playerId;
    Random rand = new Random();
    Game game = new Game();

    do {
      playerId = rand.nextInt(MAX_PLAYERS) + 1;
      player1 = "u" + playerId.toString();
      playerId = rand.nextInt(MAX_PLAYERS) + 1;
      player2 = "u" + playerId.toString();
    } while (player1 == player2 ||
             alreadyPlayingGame(existingGames, player1) ||
             alreadyPlayingGame(existingGames, player2));
    game.player1 = player1;
    game.player2 = player2;

    return game;
  }

  /**
   * Main function that logs game actions to a file.
   *
   * @param args
   *   command line arguments
   **/
  public void startLoggingGames(String[] args) throws JSONException {
    String fileName = args[0];
    Integer jsonObjCount = Integer.parseInt(args[1]),
            numGames = 0,
            numLogs = 0;
    Map<Integer, Game> games = new HashMap<Integer, Game>();
    Random rand = new Random();
   
    for (int logCnt = 1; logCnt <= jsonObjCount; logCnt++) {
      Integer gameId = getGameId(games, numGames);
      Game game;

      // Create a new game and add to map
      if ((game = games.get(gameId)) == null) {
        game = generateGame(games);
        games.put(gameId, game);
      }

      JSONObject logRecord = new JSONObject();
      logRecord.put("user", game.pickPlayer());
      logRecord.put("game", gameId);
      numGames++;
    }
  }

  public static void main(String[] args) throws JSONException {
    BeFuddled main = new BeFuddled();
    main.startLoggingGames(args);
  }

  
}
