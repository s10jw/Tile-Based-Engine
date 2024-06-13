package byow.Core;
import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    protected UserInterface UI;
    protected StringBuilder inputTracker = new StringBuilder();
    private static final int WIDTH = 70;
    private static final int HEIGHT = 40;
    protected boolean saveOnNext = false;
    protected boolean gameOver = false;
    protected boolean gameWon = false;

    protected boolean loadedWorld;
    protected World world;
    protected TETile[][] worldArray;
    private Mouse mouse;
    Sound gameMusic = new Sound("ingame01.wav");
    Sound eventMusic = new Sound("inevent.wav");
    Sound winMusic = new Sound("victory.wav");
    Sound loseMusic = new Sound("gameover.wav");

    public Engine() {
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        if (UI == null) {
            UI = new UserInterface(WIDTH, HEIGHT);
        }
        InputSource inputSource = new KeyboardInputSource();
        String data = "";
        UI.openMenu();
        while (UI.inMenu) {
            char input = 0;
            if (inputSource.possibleNextInput()) {
                input = inputSource.getNextKey();
                if (input == 'N' || input == 'n') {
                    inputTracker = new StringBuilder();
                    gameMusic.setFramePos(0);
                }
                UI.inputScanner(input);
            }
            // Handle load world request from menu.
            if ((input == 'L' || input == 'l') && !UI.seedEntry) {
                UI.loadWorld();
                inputTracker = UI.inputTracker;
            }
        }
        UI.closeMenu();
        world = UI.getWorld();
        worldArray = world.worldArray;
        startGame();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        UI = new UserInterface(WIDTH, HEIGHT);
        UI.loadFromConsole = true;
        UI.loadWorld(input);
        inputTracker = UI.inputTracker;
        world = UI.getWorld();
        worldArray = world.worldArray;
        return worldArray;
    }

    public void startGame() {
        if (UI.closeOnLoad) {
            System.out.println("CLOSED ON LOAD");
            System.exit(0);
        }
        mouse = new Mouse();
        world.eventUI.inGame = true;
        ter.initialize(WIDTH, HEIGHT, world.eventUI);
        ter.setHud(WIDTH, HEIGHT, world.player, mouse, worldArray);

        ter.renderFrame(worldArray, world.eventTriggered, gameOver);
        gameStatus();
        while (!gameOver) {
            char input = 0;
            if (StdDraw.hasNextKeyTyped()) {
                input = StdDraw.nextKeyTyped();
                // Track input.
                inputTracker.append(input);
                // Pass inputs to scanners.
                world.inputScanner(input);
                UI.inputScanner(input);
                if (input == ':' || saveOnNext) {
                    if (saveOnNext) {
                        System.out.println("HERE");
                        if (input == 'Q' || input == 'q') {
                            // Sends all inputs to UI save handler, excluding last colon.
                            UI.saveGame(inputTracker);
                            System.exit(0);
                        }
                        saveOnNext = false;
                    } else {
                        saveOnNext = true;
                    }
                }
            }
            // Mouse update for render?
            ter.hud.getMousePosition();
            // Render world.
            ter.renderFrame(worldArray, world.eventTriggered, gameOver);
            gameStatus();
        }
        // Handling end game mechanics:
        world.eventUI.renderEndScreen(gameWon);
        endGameResponse();
    }

    public void gameStatus() {
        Player player = world.player;
        boolean allRiddlesPassed = checkRiddleCompletion();
        // Check game stats.
        if (player.getPlayerGpa() <= 0) { //game lost
            gameOver = true;
            System.out.println("You died.");
        } else if (allRiddlesPassed) { // all riddles passed, game won
            gameOver = true;
            gameWon = true;
            System.out.println("You won.");
        } else {
            gameOver = false;
        }
        musicStatus();
    }

    private void musicStatus() {
        if (!gameOver) {
            // turn off end screen music
            if (winMusic.isRunning() || loseMusic.isRunning()) {
                winMusic.stop();
                loseMusic.stop();
            }
        }
        if (world.eventTriggered) {
            if (!gameOver) {
                if (gameMusic.isRunning()) {
                    gameMusic.pause();
                }
                if (!eventMusic.isRunning()) {
                    eventMusic.play();
                }
            }
        }
        if (!world.eventTriggered) {
            if (!gameOver) {
                if (eventMusic.isRunning()) {
                    eventMusic.stop();
                }
                if (gameMusic.framePos == 0 && !gameMusic.isRunning()) {
                    gameMusic.loop();
                } else if (!gameMusic.isRunning()) {
                    gameMusic.resume();
                }
            }
        }
        if (gameOver) {
            eventMusic.stop();
            gameMusic.stop();
            if (gameWon) {
                if (!winMusic.isRunning()) {
                    winMusic.play();
                }
            } else {
                if (!loseMusic.isRunning()) {
                    loseMusic.play();
                }
            }
        }
    }

    private void endGameResponse() {
        boolean inEndGame = true;
        while (inEndGame) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                input = Character.toUpperCase(input);
                if (input == ':' || saveOnNext) {
                    if (saveOnNext) {
                        System.out.println("HERE");
                        if (input == 'Q' || input == 'q') {
                            // Sends all inputs to UI save handler, excluding last colon.
                            UI.saveAllInputs = true;
                            UI.saveGame(inputTracker);
                            System.exit(0);
                        }
                        saveOnNext = false;
                    } else {
                        saveOnNext = true;
                    }
                }
                if (input == 'M') {
                    gameWon = false;
                    gameOver = false;
                    eventMusic.stop();
                    gameMusic.pause();
                    winMusic.stop();
                    loseMusic.stop();
                    inEndGame = false;
                    UI.saveAllInputs = true;
                    UI.saveGame(inputTracker);
                    interactWithKeyboard();
                } else if (input == 'Q') {
                    gameWon = false;
                    gameOver = false;
                    inEndGame = false;
                    System.exit(0);
                }
            }
        }
    }

    private boolean checkRiddleCompletion() {
        List<Teacher> teachers = world.teacherObjects;
        for (Teacher teacher : teachers) {
            if (!teacher.eventCompleted) {
                return false;
            }
        }
        return true;
    }
}
