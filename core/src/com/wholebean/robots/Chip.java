package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by john on 10/24/18.
 */

public class Chip extends Entity {
    Chip(int position, Main main) {
        super(TYPE.CHIP, position, 1f, main.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.CHIP.INDEX), false);
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        batch.draw(
                this.sprite.getFrame(0),
                Playfield.getScreenX((int) this.position.x),
                Playfield.getScreenY((int) this.position.y)
        );
    }
}
