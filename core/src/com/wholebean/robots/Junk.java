package com.wholebean.robots;

/**
 * Created by john on 10/4/18.
 */

public class Junk extends Entity {
    private static final int fullHealth = 4;

    private Main parent;
    private int health = Junk.fullHealth;

    Junk(int position, Main main) {
        super(TYPE.JUNK, position, 1f, main.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.JUNK_IDLE.INDEX), false);
        this.parent = main;
    }

    @Override
    public void act() {
        if(this.inactive) {
            return;
        }

        this.health--;
        if(this.health < 1) {
            this.deactivate();
        }
    }

    @Override
    public void react(Entity other, VERB verb) {
        if(this.inactive) {
            return;
        }

        switch(verb) {
            case REFRESH:
                this.health = Junk.fullHealth;
                break;
            case SAME_SPACE:
                if(other.type == TYPE.JUNK) {
                    other.deactivate();
                }
        }
    }

    @Override
    public void reset(int position) {
        super.reset(position);
        this.health = Junk.fullHealth;
    }
}
