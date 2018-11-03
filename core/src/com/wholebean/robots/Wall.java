package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by john on 10/4/18.
 */

public class Wall extends Entity {
    Wall(int position, Main main) {
        super(TYPE.WALL, position, 1f, main.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.WALL_STATIC.INDEX), true);
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        batch.draw(
                this.solid ? this.animation.getFrame(0) : this.animation.getFrame(1),
                Playfield.getScreenX((int) this.position.x),
                Playfield.getScreenY((int) this.position.y)
        );
    }

    @Override
    public void update(float delta) {
    }

    public void smash() {
        this.solid = false;
    }

    public void repair() {
        this.solid = true;
    }
}
