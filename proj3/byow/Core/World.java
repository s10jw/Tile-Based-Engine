package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import ngordnet.ngrams.TimeSeries;
import org.checkerframework.checker.units.qual.A;


import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class World {
    private final String TEACHERS = ".data/Teachers.csv";
    public TETile[][] worldArray;
    public List<Room> totalRoomData;
    private TileMap worldData;
    private Long SEED;
    public final int HEIGHT;
    public final int WIDTH;
    private Builder builder;
    public Player player;
    protected List<List<String>> teachers;
    public List<Teacher> teacherObjects;
    public boolean eventTriggered = false;
    public EventUI eventUI;
    public List<Integer> eventPositions;
    public World(int height, int width, long seed) {
        /** BuildWorld is presented a height and width of the world, then generates a random world. The world is
         * accessible through a getWorld call.
         */
        this.teacherObjects = new ArrayList<>();
        this.worldData = new TileMap(height, width);
        this.builder = new Builder(worldData, seed);
        HEIGHT = height;
        WIDTH = width;
        this.teachers = new ArrayList<>();
        this.SEED = seed;
        worldArray = new TETile[width][height];
        generateWorldData();
        generateWorldArray(height, width);
        this.totalRoomData = worldData.getRoomData();
        player = generateAvatar();
        generateTeachers();
        eventUI = new EventUI(player,  teacherObjects);
        // Building list of teachers to pull from:
        System.out.println("World Generated!");
    }

    public void generateTeachers() {
        Random teachersPicker = new Random(SEED);
        List<Integer> chosenNumbers = new ArrayList<>(); // For ensuring we don't pick the same
        // teacher twice.
        teacherTxtReader();
        // Filling each room with a teacher:
        for (Room room : totalRoomData) {
            if (!room.hasPlayer) {
                int currentTeacherIndex = teachersPicker.nextInt(teachers.size());
                while (!room.hasTeacher) {
                    if (chosenNumbers.contains(currentTeacherIndex)) {
                        currentTeacherIndex = teachersPicker.nextInt(teachers.size());
                    } else {
                        room.hasTeacher = true;
                        chosenNumbers.add(currentTeacherIndex);
                        List<String> currentTeacherInfo = teachers.get(currentTeacherIndex);
                        Teacher teacher = new Teacher(room, worldArray, currentTeacherInfo);
                        room.roomTeacher = teacher;
                        teacherObjects.add(teacher);
                        worldArray[room.eventPosI][room.eventPosJ] = Tileset.TEACHER;
                    }
                } // Process for making sure we don't pick the same teacher twice.
            }
        }
    }

    public void generateWorldData() {
        worldData = builder.buildWorld();
    }
    public void generateWorldArray(int height, int width) {
        TETile tile;
        int key;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                key = j + (i * width);
                worldArray[i][j] = worldData.getOrDefault(key, Tileset.NOTHING);
            }
        }
    }

    public Player generateAvatar() {
        Random random = new Random(SEED);
        Player player;
        int index = RandomUtils.uniform(random, 0, totalRoomData.size());
        Room startRoom = totalRoomData.get(index);
        startRoom.hasPlayer(true);
        startRoom.isStartRoom = true;
        int startI = ((startRoom.n - startRoom.i) / 2) + startRoom.i;
        int startJ = ((startRoom.m - startRoom.j) / 2) + startRoom.j;
        player = new Player(startI, startJ, worldArray, totalRoomData);
        player.currentPlayerRoom = startRoom;
        worldArray[player.playerI][player.playerJ] = Tileset.AVATAR;
        return player;
    }



    public TETile[][] getWorldArray() {
        return worldArray;
    }

    public TileMap getWorldData() {
        return worldData;
    }

    public void inputScanner(char input) {
        input = Character.toUpperCase(input);
        boolean validEventPos = player.hasRoom && !player.currentPlayerRoom.isStartRoom;
        if (!eventTriggered) {
            if (input == 'W' || input == 'A' || input == 'S' || input == 'D') {
                updatePlayerPos(input);
            } else if (input == 'E' && validEventPos) {
                triggerEvent();
            }
        } else {
            // If an event has been triggered, divert inputs to UI's inputScanner, check to see if games ended in UI.
            eventUI.inputScanner(input);
            eventTriggered = eventUI.getEventState();
            if (!eventTriggered) {
                // If the UI says the event has ended, update world object.
                worldArray = player.worldArray;
            }
        }
    }

    public void updatePlayerPos(char input) {
        if (player.onEntrance()) {
            totalRoomData = player.setPlayerRoom(input);
        }
        worldArray = player.walk(input);
        player.worldArray = this.worldArray;
    }

    public void triggerEvent() {
        if (!player.currentPlayerRoom.roomTeacher.eventCompleted) {
            eventTriggered = true;
            eventUI.updatePlayer(player);
            eventUI.playEvent();
        }
    }

    private void teacherTxtReader() {
        In teachersIn = new In("data/Teachers.csv");
        while (teachersIn.hasNextLine()) {
            readTeacherLine(teachersIn);
        }
    }

    private void readTeacherLine(In in) {
        List<String> currentTeacher = new ArrayList<>();
        String firstName = in.readString();
        String lastName  = in.readString();
        String riddle = in.readString();
        String option1 = in.readString();
        String option2 = in.readString();
        String option3 = in.readString();
        String option4 = in.readString();
        String answer = in.readString();
        currentTeacher.add(firstName);
        currentTeacher.add(lastName);
        currentTeacher.add(riddle);
        currentTeacher.add(option1);
        currentTeacher.add(option2);
        currentTeacher.add(option3);
        currentTeacher.add(option4);
        currentTeacher.add(answer);
        teachers.add(currentTeacher);
    }
}
