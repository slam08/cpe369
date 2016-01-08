/**
 * Class to save game state
 **/
import java.util.Random;

public class Game {
  public int id;
  public boolean finished;
  public String player;
  public Integer playerPoints;
  public Integer currentAction, maxActions;
  public boolean usedShuffle;
  public boolean usedClear;
  public boolean usedInvert;
  public boolean usedRotate;
  private Random rand;

  /** Initialize fields */
  public Game(int gameId) {
    id = gameId;
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
    int min = 9, max = 100;

    return (int) Math.max(min, Math.min(max, Math.round(this.rand.nextGaussian() * 15 + 45)));
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



