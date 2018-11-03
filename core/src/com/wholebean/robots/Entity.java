package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by john on 9/17/18.
 */

public class Entity implements Drawable {
    public enum TYPE {
        NOTHING,
        PLAYER,
        ROBOT,
        WALL,
        JUNK,
        HATCH,
        CHIP
    }

    public enum VERB {
        NONE,
        STUN,
        SAME_SPACE,
        REFRESH
    }

    protected Vector2 position;
    protected Animation animation;
    protected TYPE type;
    protected boolean solid;
    protected boolean inactive = false;

    Entity(TYPE type, int position, float animationSpeed, Array<TextureRegion> sprite, boolean solid) {
        this.type = type;
        this.solid = solid;
        this.position = Utils.coordsFromIndex(position, Playfield.width);
        this.animation = new Animation(sprite, animationSpeed, Animation.ANIMATION_TYPE.PINGPONG, true);
        this.animation.start();
    }

    public void act() {}
    public void react() {}
    public void react(Entity other, VERB verb) {}
    public void update(float delta) { this.animation.update(delta); }
    public void draw(float delta, SpriteBatch batch) {
        batch.draw(
                this.animation.getFrame(),
                Playfield.getScreenX((int) this.position.x),
                Playfield.getScreenY((int) this.position.y)
        );
    }
    public void deactivate() {
        this.inactive = true;
    }
    public void activate() { this.inactive = false; }

    public boolean isInactive() {
        return this.inactive;
    }

    public int getPositionIndex() {
        return Utils.indexFromCoords(this.position, Playfield.width);
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = Utils.coordsFromIndex(position, Playfield.width);
    }

    public void reset(int position) {
        this.position = Utils.coordsFromIndex(position, Playfield.width);
        this.animation.start();
        this.activate();
    }
}
