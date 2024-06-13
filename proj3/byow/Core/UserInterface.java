package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import edu.princeton.cs.algs4.StdDraw;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.util.Random;

public class UserInterface {
    public boolean inMenu;
    private String seed = "";
    public long finalSeed;
    public boolean seedEntry;
    public StringBuilder inputTracker = new StringBuilder();
    public boolean loadingWorld = false;
    public boolean closeOnLoad = false;
    public boolean saveAllInputs = false;
    public boolean loadFromConsole = false;
    private final int WIDTH;
    private final int HEIGHT;
    private final int TITLEI;
    private final int TITLEJ;
    private Sound menuMusic;
    private final String DIRECTORY = "SavedWorlds.txt";

    public World world;

    UserInterface(int width, int height) {
        inMenu = false;
        WIDTH = width;
        HEIGHT = height;
        TITLEI = WIDTH / 2;
        TITLEJ = (3 * HEIGHT) / 4;
        menuMusic = new Sound("inmenu01.wav");

        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.picture(35, 20, "mainmenu.jpg", 70, 40);
    }

    public void openMenu() {
        inMenu = true;
        menuMusic.loop();
        StdDraw.clear(Color.BLACK);
        StdDraw.picture(35, 20, "mainmenu.jpg", 70, 40);
        Font fontBig = new Font("Arial", Font.BOLD, 62);
        StdDraw.setFont(fontBig);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.textLeft(4, TITLEJ + 1 , "KNOWLEDGE CRUSADE I");
        fontBig = new Font("Arial", Font.BOLD, 60);
        Font fontSmall = new Font("Arial", Font.BOLD, 40);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(fontBig);
        StdDraw.textLeft(4, TITLEJ , "KNOWLEDGE CRUSADE I");
        StdDraw.setFont(fontSmall);
        StdDraw.textLeft(6, TITLEJ - 7, "NEW GAME (N)");
        StdDraw.textLeft(6 , TITLEJ - 14, "LOAD GAME (L)");
        StdDraw.textLeft(6 , TITLEJ - 21, "QUIT (Q)");
        StdDraw.show();
    }

    public void closeMenu() {
        inMenu = false;
        seedEntry = false;
        menuMusic.stop();
        StdDraw.clear();
    }

    public void inputScanner(char input) {
        if ((input == 'N' || input == 'n') && inMenu) {
            seed = "";
            seedEntry = true;
        }
        if (!seedEntry) {
            if ((input == 'Q' || input == 'q') && inMenu && !loadingWorld) {
                System.exit(0);
            }
        }
        if (seedEntry) {
            StdDraw.clear(Color.BLACK);
            StdDraw.picture(35, 20, "mainmenu.jpg", 70, 40);
            Font fontBig = new Font("Arial", Font.BOLD, 60);
            StdDraw.setFont(fontBig);
            StdDraw.text(TITLEI, TITLEJ, "Please enter valid seed: ");
            StdDraw.show();
            promptSeed(input);
        }
    }

    public void saveGame(StringBuilder inputTracker) {
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("N");
        System.out.println(finalSeed);
        dataBuilder.append(Long.toString(finalSeed));
        dataBuilder.append("S");
        if (saveAllInputs) {
            dataBuilder.append(inputTracker.substring(0, inputTracker.length()));
            saveAllInputs = false;
        } else {
            dataBuilder.append(inputTracker.substring(0, inputTracker.length() - 2));
        }
        String dataString = dataBuilder.toString();
        saveWorldFile(dataString);
    }

    public void saveWorldFile(String dataString)  {
        System.out.println("Starting to save world.");
        try {
            FileWriter myWriter = new FileWriter("SavedWorlds.txt");
            myWriter.write(dataString);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println("HERE");
    }

    public void promptSeed(char input) {
        if (input == 'S' || input == 's') {
            seedEntry = false;
            StdDraw.clear(StdDraw.BLACK);
            //load world
            generateWorld();
        } else if (input != 'N') {
            int titleI =  WIDTH / 2;
            int titleJ = (3 * HEIGHT) / 4;
            seed = seed + input;
            Font fontSmall = new Font("Arial", Font.BOLD, 40);
            StdDraw.setFont(fontSmall);
            StdDraw.text(titleI, titleJ - 14 , seed);
            StdDraw.show();
        }
    }

    private void generateWorld() {
        if (!seed.matches("\\d+")) {
            StdDraw.clear(Color.BLACK);
            Font fontSmall = new Font("Arial", Font.BOLD, 40);
            StdDraw.setFont(fontSmall);
            seed = "";
            StdDraw.text(WIDTH / 2, HEIGHT / 2 , "Invalid Seed! Seed must contain only digits. Try again!");
            StdDraw.show();
            StdDraw.pause(2000);
            StdDraw.clear(Color.BLACK);
            seedEntry = true;
        } else {
            finalSeed = Long.parseLong(seed);
            world = new World(HEIGHT, WIDTH, finalSeed);
            closeMenu();
        }
    }

    public World loadWorld() {
        loadingWorld = true;
        String data;
        String fileName = "SavedWorlds.txt";
        File file = new File(fileName);
        FileReader fr = null;
        // Read saved data, create string of saved data.
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            data = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder dataBuilder = new StringBuilder(data);
        StringBuilder seedBuilder = new StringBuilder();

        char ltr;
        boolean inSeed = false;
        boolean readingInputs = false;
        // First, we get the seed from the input. If there's anything left over, append to inputTracker.
        for (int i = 0; i < dataBuilder.length(); i++) {
            ltr = dataBuilder.charAt(i);
            if (i == 0) {
                inSeed = true;
                continue;
            }
            if (inSeed) {
                if (ltr == 'S' || ltr == 's') {
                    inSeed = false;
                    readingInputs = true;
                    continue;
                }
                seedBuilder.append(ltr);
            }
            if (readingInputs) {
                inputTracker.append(ltr);
            }
        }

        Long seed = Long.parseLong(seedBuilder.toString());
        finalSeed = seed;
        if (world == null) {
            world = new World(HEIGHT, WIDTH, seed);
        }
        inMenu = false;
        emulate();
        menuMusic.stop();
        loadingWorld = false;
        return world;
    }

    public World loadWorld(String data) {
        StringBuilder dataBuilder = new StringBuilder(data);
        StringBuilder seedBuilder = new StringBuilder();

        char ltr;
        boolean inSeed = false;
        boolean readingInputs = false;
        loadingWorld = true;
        // First, we get the seed from the input. If there's anything left over, append to inputTracker.
        for (int i = 0; i < dataBuilder.length(); i++) {
            ltr = dataBuilder.charAt(i);
            if (i == 0) {
                if (ltr == 'L' || ltr == 'l') {
                    // load previous saved world, append to input tracker, then continue.
                    loadWorld();
                    loadingWorld = true;
                    readingInputs = true;
                    continue;
                } else {
                    // Handles case where input is just asking to load some seed followed by movements.
                    inSeed = true;
                    continue;
                }
            }
            if (inSeed) {
                if (ltr == 'S' || ltr == 's') {
                    inSeed = false;
                    readingInputs = true;
                    continue;
                }
                seedBuilder.append(ltr);
            }
            if (readingInputs) {
                inputTracker.append(ltr);
            }
        }
        if (!seedBuilder.isEmpty()) {
            Long seed = Long.parseLong(seedBuilder.toString());
            finalSeed = seed;
        }
        if (world == null) {
            world = new World(HEIGHT, WIDTH, finalSeed);
        }
        inMenu = false;
        menuMusic.stop();
        emulate();
        loadingWorld = false;
        return world;
    }

    private void emulate() {
        boolean saveOnNext = false;
//        ter.initialize(WIDTH, HEIGHT, world.eventUI);
        char ltr;
        for (int i = 0; i < inputTracker.length(); i++) {
            ltr = inputTracker.charAt(i);
            world.inputScanner(ltr);
            inputScanner(ltr);
            // Handle quit and save requests.
            if (ltr == ':' || saveOnNext) {
                if (saveOnNext) {
                    if (ltr == 'Q' || ltr == 'q') {
                        // Sends all inputs to UI save handler, excluding last colon.
                        String saveString = inputTracker.substring(0, inputTracker.length());
                        StringBuilder saveBuilder = new StringBuilder(saveString);
                        saveGame(saveBuilder);
                        if (loadFromConsole) {
                            closeOnLoad = true;
                        }
                    }
                    saveOnNext = false;
                } else {
                    saveOnNext = true;
                }
            }
        }
    }

    public World getWorld() {
        return world;
    }
}
