package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.awt.Robot;

/**
 * Created by john on 9/20/18.
 */

public class RobotsPlayfield implements RobotsDrawable {
    private RobotsGame game;

    public static final int logicalScreenWidth = 270;
    public static final int logicalScreenHeight = 480;
    public static final int spaceSize = 16;
    public static final int width = 16;
    public static final int height = 28;
    public static final int numberOfSquares = RobotsPlayfield.width * RobotsPlayfield.height;

    private static final int shortAxisOffset = 7;
    private static final int longAxisOffset = 9;

    public static final int playerStart = RobotsUtils.indexFromCoords(
            RobotsPlayfield.width / 2,
            RobotsPlayfield.height / 2,
            RobotsPlayfield.width
    );

    private TextureRegion wallGraphic;
    private Array<RobotsEntity.TYPE> board = new Array<RobotsEntity.TYPE>(RobotsPlayfield.numberOfSquares);

    private RandomXS128 placementGenerator;
    private RandomXS128 teleportGenerator;

    RobotsPlayfield(RobotsGame game, long seed, float wallFrequency) {
        this.game = game;
        this.wallGraphic = this.game.graphics.get(RobotsGame.SPRITE_INFO.WALL_STATIC.INDEX).get(0);

        for(int i = 0; i < RobotsPlayfield.numberOfSquares; i++) {
            this.board.add(RobotsEntity.TYPE.NOTHING);
        }

        this.generateBoard(seed, wallFrequency);
    }

    public void draw(float delta, SpriteBatch batch) {
        for(int i = 0; i < this.board.size; i++) {
            if(this.board.get(i) == RobotsEntity.TYPE.WALL) {
                Vector2 worldPos = RobotsUtils.coordsFromIndex(i, RobotsPlayfield.width);
                Vector2 screenPos = RobotsPlayfield.getScreenCoordinates((int)worldPos.x, (int)worldPos.y);
                batch.draw(this.wallGraphic, screenPos.x, screenPos.y);
            }
        }
    }

    public void generateBoard(long seed, float wallFrequency) {
        this.placementGenerator = new RandomXS128();
        this.teleportGenerator = new RandomXS128();

        this.placementGenerator.setSeed(seed);
        this.teleportGenerator.setSeed(seed);

        for(int i = 0; i < RobotsPlayfield.numberOfSquares; i++) {
            if(this.placementGenerator.nextFloat() < wallFrequency) {
                this.board.set(i, RobotsEntity.TYPE.WALL);
            } else {
                this.board.set(i, RobotsEntity.TYPE.NOTHING);
            }
        }
    }

    public boolean spaceClear(int position) {
        if(position < 0 || position > RobotsPlayfield.numberOfSquares - 1) {
            return false;
        }

        return this.board.get(position) != RobotsEntity.TYPE.WALL;
    }

    public boolean spaceClear(Vector2 position) {
        if(position.x < 0 ||
           position.x >= RobotsPlayfield.width ||
           position.y < 0 ||
           position.y >= RobotsPlayfield.height) {
            return false;
        }
        return this.board.get(RobotsUtils.indexFromCoords(position, RobotsPlayfield.width)) != RobotsEntity.TYPE.WALL;
    }

    public boolean spaceClear(int x, int y) {
        if(x < 0 ||
           x >= RobotsPlayfield.width ||
           y < 0 ||
           y >= RobotsPlayfield.height) {
           return false;
        }
        return this.board.get(RobotsUtils.indexFromCoords(x, y, RobotsPlayfield.width)) != RobotsEntity.TYPE.WALL;
    }

    public static Vector2 getScreenCoordinates(int x, int y) {
        x = (x * RobotsPlayfield.spaceSize) + RobotsPlayfield.shortAxisOffset;
        y = (y * RobotsPlayfield.spaceSize) + RobotsPlayfield.longAxisOffset;

        return new Vector2(x, y);
    }

    public static int getScreenX(int x) {
        return (x * RobotsPlayfield.spaceSize) + RobotsPlayfield.shortAxisOffset;
    }

    public static int getScreenY(int y) {
        return (y * RobotsPlayfield.spaceSize) + RobotsPlayfield.longAxisOffset;
    }
}
