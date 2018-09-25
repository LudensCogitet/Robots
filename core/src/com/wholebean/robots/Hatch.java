package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by john on 9/24/18.
 */

public class Hatch extends Entity {
    private boolean open = false;

    Hatch(int position, RobotsGame robotsGameRef) {
        super(TYPE.HATCH, position, 1f, robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.HATCH.INDEX));
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        batch.draw(
                this.open ? this.sprite.getKeyFrame(1) : this.sprite.getKeyFrame(0),
                Playfield.getScreenX((int) this.position.x),
                Playfield.getScreenY((int) this.position.y)
        );
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
