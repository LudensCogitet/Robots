package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by john on 9/17/18.
 */

public class RobotsEntity implements RobotsDrawable {
    public enum TYPE {
        NOTHING,
        PLAYER,
        ROBOT,
        WALL,
        SCRAP
    }

    protected Vector2 position;
    protected Animation<TextureRegion> sprite;
    protected float accumulator = 0;
    protected TYPE type;

    RobotsEntity(TYPE type, int position, float animationSpeed,  Array<TextureRegion> sprite) {
        this.type = type;
        this.position = RobotsUtils.coordsFromIndex(position, RobotsPlayfield.width);
        this.sprite = new Animation<TextureRegion>(animationSpeed, sprite);
        this.sprite.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    public void act() {}
    public void react(RobotsEntity other) {}
    public void draw(float delta, SpriteBatch batch) {
        this.accumulator += delta;
        batch.draw(
                this.sprite.getKeyFrame(this.accumulator),
                RobotsPlayfield.getScreenX((int) this.position.x),
                RobotsPlayfield.getScreenY((int) this.position.y)
        );
    }
}
