package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by john on 9/20/18.
 */

public class Playfield implements Drawable {
    private RobotsGame robotsGame;

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

    private TextureRegion wallGraphic;
    private Array<Entity.TYPE> board = new Array<Entity.TYPE>(Playfield.numberOfSquares);
    private Array<Hatch> robotSpawnPoints;


    private RandomXS128 placementGenerator;

    Playfield(RobotsGame robotsGame, long seed, float wallFrequency, int numberOfSpawnPoints) {
        this.robotsGame = robotsGame;
        this.wallGraphic = this.robotsGame.graphics.get(RobotsGame.SPRITE_INFO.WALL_STATIC.INDEX).get(0);

        for(int i = 0; i < Playfield.numberOfSquares; i++) {
            this.board.add(Entity.TYPE.NOTHING);
        }

        this.generateBoard(seed, wallFrequency, numberOfSpawnPoints);
    }

    public void draw(float delta, SpriteBatch batch) {
        for(int i = 0; i < this.board.size; i++) {
            if(this.board.get(i) == Entity.TYPE.WALL) {
                Vector2 worldPos = Utils.coordsFromIndex(i, Playfield.width);
                Vector2 screenPos = Playfield.getScreenCoordinates((int)worldPos.x, (int)worldPos.y);
                batch.draw(this.wallGraphic, screenPos.x, screenPos.y);
            }
        }

        for(int i = 0; i < this.robotSpawnPoints.size; i++) {
            this.robotSpawnPoints.get(i).draw(delta, batch);
        }
    }

    public void generateBoard(long seed, float wallFrequency, int numberOfSpawnPoints) {
        this.placementGenerator = new RandomXS128();

        this.placementGenerator.setSeed(seed);

        for(int i = 0; i < Playfield.numberOfSquares; i++) {
            if(this.placementGenerator.nextFloat() < wallFrequency) {
                this.board.set(i, Entity.TYPE.WALL);
            } else {
                this.board.set(i, Entity.TYPE.NOTHING);
            }
        }

        this.robotSpawnPoints  = new Array<Hatch>(numberOfSpawnPoints);

        int spawnPointsLeft = numberOfSpawnPoints;
        while(spawnPointsLeft > 0) {
            int spawnPosition = this.placementGenerator.nextInt(Playfield.numberOfSquares);
            Entity.TYPE boardPosition = this.board.get(spawnPosition);
            while(boardPosition == Entity.TYPE.WALL || boardPosition == Entity.TYPE.HATCH) {
                spawnPosition++;
                if(spawnPosition > Playfield.numberOfSquares - 1) {
                    spawnPosition = 0;
                }

                boardPosition = this.board.get(spawnPosition);
            }

            this.board.set(spawnPosition, Entity.TYPE.WALL);
            this.robotSpawnPoints.add(new Hatch(spawnPosition, this.robotsGame));
            spawnPointsLeft--;
        }
    }

    public boolean spaceClear(int position) {
        if(position < 0 || position > Playfield.numberOfSquares - 1) {
            return false;
        }

        return this.board.get(position) != Entity.TYPE.WALL;
    }

    public boolean spaceClear(Vector2 position) {
        if(position.x < 0 ||
           position.x >= Playfield.width ||
           position.y < 0 ||
           position.y >= Playfield.height) {
            return false;
        }
        return this.board.get(Utils.indexFromCoords(position, Playfield.width)) != Entity.TYPE.WALL;
    }

    public boolean spaceClear(int x, int y) {
        if(x < 0 ||
           x >= Playfield.width ||
           y < 0 ||
           y >= Playfield.height) {
           return false;
        }
        return this.board.get(Utils.indexFromCoords(x, y, Playfield.width)) != Entity.TYPE.WALL;
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
