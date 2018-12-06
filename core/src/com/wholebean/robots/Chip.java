package com.wholebean.robots;

/**
 * Created by john on 10/24/18.
 */

public class Chip extends Entity {
    Chip(int position, Main main) {
        super(TYPE.CHIP, position, 0.1f, main.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.CHIP.INDEX), false);
    }
}
