/**
 * Class to save game state
 **/

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game {
    public static String SHUFFLE = "Shuffle";
    public static int SHUFFLE_PROB = 30;
    public static String CLEAR = "Clear";
    public static int CLEAR_PROB = 27;
    public static String INVERT = "Invert";
    public static int INVERT_PROB = 23;
    public static String ROTATE = "Rotate";
    public static int ROTATE_PROB = 20;

    public int id;
    public boolean finished;
    public String player;
    public int playerPoints;
    public int currentAction, maxActions;
    public HashMap<String, Integer> availableSpecials;
    public int probOfAvailable;
    private Random rand;

    /**
     * Initialize fields
     */
    public Game(int gameId, String newPlayer) {
        id = gameId;
        finished = false;
        playerPoints = 0;
        currentAction = 1;
        rand = new Random();
        maxActions = generateMaxActions();
        availableSpecials = new HashMap<String, Integer>();
        availableSpecials.put(SHUFFLE, SHUFFLE_PROB);
        availableSpecials.put(CLEAR, CLEAR_PROB);
        availableSpecials.put(INVERT, INVERT_PROB);
        availableSpecials.put(ROTATE, ROTATE_PROB);
        probOfAvailable = 100;
        player = newPlayer;
    }

    /**
     * Randomly pick a special move with the following percentages:
     * - Shuffle: 0.30
     * - Clear: 0.27
     * - Invert: 0.23
     * - Rotate: 0.20
     *
     * @return The name of the special move used.
     **/
    public String pickSpecialMove() {
        int value = this.rand.nextInt(this.probOfAvailable) + 1;
        int curVal = 0;
        HashMap<String, Integer> availableCopy = new HashMap<String, Integer>(this.availableSpecials);
        for (Map.Entry<String, Integer> entry : availableCopy.entrySet()) {
            curVal += entry.getValue();
            if (curVal >= value) {
                this.availableSpecials.remove(entry.getKey());
                this.probOfAvailable -= entry.getValue();
                return entry.getKey();
            }
        }

        throw new RuntimeException("Improper generation of special move.");
    }

    /**
     * Generate a random value using a normal curve
     * with a mean of 45 and standard deviation of 15.
     *
     * @return An integer between 9 and 100
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

    public void setFinished() {
        this.finished = true;
    }

    /**
     * Print the current fields of the game for debugging purposes.
     */
    public void printContents() {
        System.out.println("--------------------");
        System.out.println("player: " + this.player);
        System.out.println("playerPoints: " + this.playerPoints);
        System.out.println("currentAction: " + this.currentAction);
        System.out.println("maxActions: " + this.maxActions);
        System.out.println("--------------------");
    }

    public boolean allSpecialsUsed() {
        return availableSpecials.size() == 0;
    }
}


