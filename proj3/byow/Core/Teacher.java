package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.List;

public class Teacher {

    protected int teacherI, teacherJ;
    protected TETile[][] worldArray;
    protected Room classroom;
    protected String firstName;
    protected String lastName;
    protected String riddle, option1, option2, option3, option4;
    int answer;
    protected List<String> info;
    private final int WIDTH;
    private final int HEIGHT;
    protected boolean eventCompleted, eventInProgress;


    public Teacher(Room classroom, TETile[][] worldArray, List<String> info) {
        this.teacherI = classroom.eventPosI;
        this.teacherJ = classroom.eventPosJ;
        this.worldArray = worldArray;
        this.classroom = classroom;
        this.info = info;
        this.firstName = info.get(0);
        this.lastName = fixRiddle(info.get(1));
        this.riddle = fixRiddle(info.get(2));
        this.option1 = fixRiddle(info.get(3));
        this.option2 = fixRiddle(info.get(4));
        this.option3 = fixRiddle(info.get(5));
        this.option4 = fixRiddle(info.get(6));
        this.answer = Integer.valueOf(info.get(7));
        //this.riddle = fixRiddle(riddle);
        this.HEIGHT = worldArray[0].length;
        this.WIDTH = worldArray.length;
        this.eventCompleted = false;
        this.eventInProgress = false;
    }

    public void event() {
        if (eventCompleted) {
            System.out.println("You've already passed my class, get outta here!");
        } else {
            eventInProgress = true;
        }
    }

    private String fixRiddle(String riddle) {
        String fixedRiddle = riddle.replace("_", " ");
        return fixedRiddle;
    }

    private boolean correctAnswer(char input) {
        int intInput;
        if (input == '1') {
            intInput = 1;
        } else if (input == '2') {
            intInput = 2;
        } else if (input == '3') {
            intInput = 3;
        } else if (input == '4') {
            intInput = 4;
        } else  {
            intInput = 0;
        }
        return intInput == answer;
    }

    protected String eventAction(char input, Player player) {
        String returnString = "";
        if (correctAnswer(input)) {
            eventCompleted = true;
            eventInProgress = false;
            returnString = "Correct!";
        } else if (input == '5') {
            eventInProgress = false;
        } else {
            player.gpa = player.gpa - 0.5;
            returnString = "Wrong answer! Your GPA feel by 0.5 points!";
            //eventInProgress = false;
        }
        return returnString;
    }

    protected TETile[][] closeEvent() {
        worldArray[teacherI][teacherJ] = Tileset.TEACHER_PASSED;
        return worldArray;
    }
}
