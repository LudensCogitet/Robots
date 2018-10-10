package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by john on 9/20/18.
 */

public class Playfield implements Drawable {
    private Main parent;

    public static final int logicalScreenWidth = 270;
    public static final int logicalScreenHeight = 480;
    public static final int spaceSize = 16;
    public static final int width = 16;
    public static final int height = 28;
    public static final int numberOfSquares = Playfield.width * Playfield.height;

    public static final int shortAxisOffset = 7;
    public static final int longAxisOffset = 7;

    public static final int playerStart = Utils.indexFromCoords(
            Playfield.width / 2,
            Playfield.height / 2,
            Playfield.width
    );

    private Array<Entity> board = new Array<Entity>(Playfield.numberOfSquares);
    private Array<Hatch> robotSpawnPoints;


    private int spawnPointIndex = 0;
    private RandomXS128 placementGenerator;

    Playfield(Main parent, long seed, float wallFrequency, int numberOfSpawnPoints) {
        this.parent = parent;

        for(int i = 0; i < Playfield.numberOfSquares; i++) {
            this.board.add(null);
        }

        this.generateBoard(seed, wallFrequency, numberOfSpawnPoints);
    }

    Playfield(Main parent, String boardFile) {
        this.parent = parent;

        this.loadBoard(boardFile);
    }

    Playfield(Main parent) {
        this.parent = parent;
    }

    public void draw(float delta, SpriteBatch batch) {
        for(int i = 0; i < this.board.size; i++) {
            Entity entity = this.board.get(i);
            if(entity != null) {
                entity.draw(delta, batch);
            }
        }
    }

    public Array<Robot> spawnRobots(int count) {
        if(count < 1) {
            return null;
        }

        Hatch spawnPoint;
        Array<Robot> robots = new Array<Robot>(false, 8);

        int currentIndex = this.spawnPointIndex;
        do {
            spawnPoint = this.robotSpawnPoints.get(currentIndex);

            if (!spawnPoint.isInactive() &&
                this.parent.entityAt(spawnPoint.getPositionIndex()) == null) {
                spawnPoint.open();
                spawnPoint.deactivate();

                Robot robot = this.parent.getRobot(spawnPoint.getPositionIndex());

                robots.add(robot);
            }

            if (currentIndex >= this.robotSpawnPoints.size -1) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }

            if(robots.size == count) { break; }
        } while(currentIndex != this.spawnPointIndex);

        this.spawnPointIndex = currentIndex;

        for(int i = 0; i < this.robotSpawnPoints.size; i++) {
            this.robotSpawnPoints.get(i).activate();
        }

        return robots.size > 0 ? robots : null;
    }

    public void closeHatches() {
        for(int i = 0; i < this.robotSpawnPoints.size; i++) {
            this.robotSpawnPoints.get(i).close();
        }
    }

    public void generateBoard(long seed, float wallFrequency, int numberOfSpawnPoints) {
        this.placementGenerator = new RandomXS128();

        this.placementGenerator.setSeed(seed);

        for(int i = 0; i < Playfield.numberOfSquares; i++) {
            this.board.set(i, null);
        }

        for(int i = 0; i < Playfield.numberOfSquares; i++) {
            if(this.placementGenerator.nextFloat() < wallFrequency) {
                this.board.set(i, new Wall(i, this.parent));
            }
        }

        this.robotSpawnPoints  = new Array<Hatch>(numberOfSpawnPoints);

        int spawnPointsLeft = numberOfSpawnPoints;
        while(spawnPointsLeft > 0) {
            int spawnPosition = this.placementGenerator.nextInt(Playfield.numberOfSquares);
            Entity boardPosition = this.board.get(spawnPosition);
            while(boardPosition != null) {
                spawnPosition++;
                if(spawnPosition > Playfield.numberOfSquares - 1) {
                    spawnPosition = 0;
                }

                boardPosition = this.board.get(spawnPosition);
            }

            Hatch hatch = new Hatch(spawnPosition, this.parent);
            this.board.set(spawnPosition, hatch);
            this.robotSpawnPoints.add(hatch);
            spawnPointsLeft--;
        }
    }

    public Player loadBoard(String boardFile) {
        JsonReader json = new JsonReader();
        int[] board = json.parse(Gdx.files.internal(boardFile)).asIntArray();

        this.robotSpawnPoints  = new Array<Hatch>();

        Player player = null;

        for(int i = 0; i < board.length; i++) {
            if(board[i] == 1) {
                this.board.add(new Wall(i, this.parent));
            } else if(board[i] == 2) {
                Hatch hatch = new Hatch(i, this.parent);
                this.robotSpawnPoints.add(hatch);
                this.board.add(hatch);
            } else if(board[i] == 3) {
                player = new Player(i, this.parent);
            } else {
                this.board.add(null);
            }
        }

        return player;
    }

    public boolean spaceBlocked(int position) {
        if(position < 0 || position > Playfield.numberOfSquares - 1) {
            return true;
        }

        Entity entity = this.board.get(position);

        return entity != null && entity.solid;
    }

    public boolean spaceBlocked(Vector2 position) {
        if(position.x < 0 ||
           position.x >= Playfield.width ||
           position.y < 0 ||
           position.y >= Playfield.height) {
            return true;
        }

        Entity entity = this.board.get(Utils.indexFromCoords(position, Playfield.width));

        return entity != null && entity.solid;
    }

    public boolean spaceBlocked(int x, int y) {
        if(x < 0 ||
           x >= Playfield.width ||
           y < 0 ||
           y >= Playfield.height) {
           return true;
        }

        Entity entity = this.board.get(Utils.indexFromCoords(x, y, Playfield.width));

        return entity != null && entity.solid;
    }

    public Entity entityAtPosition(int position) {
        if(position < 0 || position > Playfield.numberOfSquares - 1) {
            return null;
        }

        return this.board.get(position);
    }

    public Entity entityAtPosition(Vector2 position) {
        if(position.x < 0 ||
                position.x >= Playfield.width ||
                position.y < 0 ||
                position.y >= Playfield.height) {
            return null;
        }

        return this.board.get(Utils.indexFromCoords(position, Playfield.width));
    }

    public Entity entityAtPosition(int x, int y) {
        if(x < 0 ||
                x >= Playfield.width ||
                y < 0 ||
                y >= Playfield.height) {
            return null;
        }

        return this.board.get(Utils.indexFromCoords(x, y, Playfield.width));
    }

    public static Vector2 getScreenCoordinates(int x, int y) {
        x = (x * Playfield.spaceSize) + Playfield.shortAxisOffset;
        y = (y * Playfield.spaceSize) + Playfield.longAxisOffset;

        return new Vector2(x, y);
    }

    public static Vector2 getScreenCoordinates(int position) {
        Vector2 vector = Utils.coordsFromIndex(position, Playfield.width);

        vector.x = ((int)vector.x * Playfield.spaceSize) + Playfield.shortAxisOffset;
        vector.y = ((int)vector.y * Playfield.spaceSize) + Playfield.longAxisOffset;

        return vector;
    }

    public static int getScreenX(int x) {
        return (x * Playfield.spaceSize) + Playfield.shortAxisOffset;
    }

    public static int getScreenY(int y) {
        return (y * Playfield.spaceSize) + Playfield.longAxisOffset;
    }
}
