package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by john on 9/24/18.
 */

public class Hatch extends Entity {
    private boolean open = false;

    Hatch(int position, Main main) {
        super(TYPE.HATCH, position, 1f, main.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.HATCH.INDEX), true);
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        batch.draw(
                this.open ? this.animation.getFrame(1) : this.animation.getFrame(0),
                Playfield.getScreenX((int) this.position.x),
                Playfield.getScreenY((int) this.position.y)
        );
    }

    @Override
    public void update(float delta) {

    }

    public void open() {
        this.open = true;
    }

    public void close() {
        this.open = false;
    }

    public void toggle() {
        this.open = !this.open;
    }
}
