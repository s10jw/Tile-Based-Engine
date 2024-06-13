package byow.Core;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class EventUI {
    private final int HEIGHT;
    private final int WIDTH;
    private Player player;
    private List<Teacher> teachers;
    private boolean oneIncorrect = false;
    public boolean inGame = false;
    private boolean twoIncorrect = false;
    private boolean threeIncorrect = false;
    private boolean fourIncorrect = false;
    private char lastInput;
    private TETile[][] worldArray;
    private boolean inEvent = false;
    EventUI(Player player, List<Teacher> teachers) {
        this.player = player;
        this.teachers = teachers;
        HEIGHT = this.player.worldArray[0].length;
        WIDTH = this.player.worldArray.length;
        worldArray = this.player.worldArray;
    }
    public void updatePlayer(Player player) {
        this.player = player;
        worldArray = this.player.worldArray;
    }
    public void inputScanner(char input) {
        input = Character.toUpperCase(input);
        if (input == '1' || input == '2' || input == '3' || input == '4' || input == '5') {
            eventAction(input);
        }
    }
    public void eventAction(char input) {
        lastInput = input;
        if (player.hasRoom && !player.currentPlayerRoom.roomTeacher.eventCompleted) {
            player.currentPlayerRoom.roomTeacher.eventAction(input, player);
            if (input == '5') {
                inEvent = false;
                player.currentPlayerRoom.roomTeacher.eventCompleted = false;
            }
            if (player.currentPlayerRoom.roomTeacher.eventCompleted) {
                //System.out.println("Event done!");
                if (!checkRiddleCompletion()) {
                    renderWinScreen();
                }
                inEvent = false;
                resetTriggers();
                worldArray = player.currentPlayerRoom.roomTeacher.closeEvent();
            } else {
                determineIncorrect(lastInput);
            }
        }
    }

    private boolean checkRiddleCompletion() {
        for (Teacher teacher : teachers) {
            if (!teacher.eventCompleted) {
                return false;
            }
        }
        return true;
    }

    private void determineIncorrect(char input) {
        if (input == '1') {
            oneIncorrect = true;
        } else if (input == '2') {
            twoIncorrect = true;
        } else if (input == '3') {
            threeIncorrect = true;
        } else if (input == '4') {
            fourIncorrect = true;
        }
    }

    private void resetTriggers() {
        oneIncorrect = twoIncorrect = threeIncorrect = fourIncorrect = false;
    }

    public void render() {
        Teacher teacher  = player.currentPlayerRoom.roomTeacher;
        Font font = new Font("Monaco", Font.BOLD, 40);
        String firstName = teacher.firstName;
        String lastName = teacher.lastName;
        String question = teacher.riddle;
        String option1 = teacher.option1;
        String option2 = teacher.option2;
        String option3 = teacher.option3;
        String option4 = teacher.option4;
        // TODO: We seem to not need this line, teacher does the checking and it has knowledge of
        //  the correct answer number.
        // int answer = teacher.answer;
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledRectangle((double) WIDTH / 2, (double) HEIGHT / 2, 22, 13);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle((double) WIDTH / 2, (double) HEIGHT / 2, 21, 12);
        StdDraw.setPenColor(StdDraw.WHITE);
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, 29, "Hi, I am professor " + firstName + " " + lastName);
        StdDraw.text(WIDTH / 2, 26, "Can you solve my riddle?:");
        StdDraw.text(WIDTH / 2, 23, question);
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (oneIncorrect) {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
        }
        StdDraw.textLeft(20, 20, "1: " + option1);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (twoIncorrect) {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
        }
        StdDraw.textLeft(20, 18, "2: " + option2);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (threeIncorrect) {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
        }
        StdDraw.textLeft(20, 16, "3: " + option3);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (fourIncorrect) {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
        }
        StdDraw.textLeft(20, 14, "4: " + option4);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(20, 12, "5: " + "Emergency Drop (Exit)");
    }

    public void renderWinScreen() {
        if (inGame) {
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.setXscale(0, WIDTH);
            StdDraw.setYscale(0, HEIGHT);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledRectangle((double) WIDTH / 2, (double) HEIGHT / 2, 22, 13);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledRectangle((double) WIDTH / 2, (double) HEIGHT / 2, 21, 12);
            StdDraw.setPenColor(StdDraw.GREEN);
            font = new Font("Monaco", Font.BOLD, 40);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Good Job! Now get out of my class...");
            StdDraw.setFont(font);
            StdDraw.show();
            StdDraw.pause(1850);
        }
    }

    public void renderEndScreen(boolean gameWon) {
        Font font = new Font("Monaco", Font.BOLD, 38);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        if (gameWon) {
            int xPlacement = WIDTH / 2;
            int yPlacement = HEIGHT / 2;
            int buffer = 3;
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(xPlacement, yPlacement + buffer, "Congratulations!");
            StdDraw.text(xPlacement, yPlacement, "You have graduated from UC Berkeley!");
            StdDraw.text(xPlacement, yPlacement - buffer, "Go Bears!");
            StdDraw.text(xPlacement - (4 * buffer), yPlacement - (2 * buffer), "(M) Menu");
            StdDraw.text(xPlacement + (4 * buffer), yPlacement - (2 * buffer), "(Q) Exit");
        } else {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "You've been placed on academic probation...");
            font = new Font("Monaco", Font.BOLD, 70);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "GAME OVER");
            font = new Font("Monaco", Font.BOLD, 38);
            StdDraw.setFont(font);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Try again next semester.");
            StdDraw.text(WIDTH / 2 - 10, HEIGHT / 2 - 10, "(M) Menu");
            StdDraw.text(WIDTH / 2 + 10, HEIGHT / 2 - 10, "(Q) Exit");
        }
        StdDraw.show();
    }

    public void playEvent() {
        inEvent = true;
        player.currentPlayerRoom.roomTeacher.event();
    }
    public boolean getEventState() {
        return inEvent;
    }
}
