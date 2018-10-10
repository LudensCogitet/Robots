package com.wholebean.robots;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by john on 9/23/18.
 */

public class Robot extends Entity {
    Main parent;

    Robot(int position, Main gameScreen) {
        super(TYPE.ROBOT, position, 0.3f, gameScreen.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.ROBOT_IDLE.INDEX), false);
        this.parent = gameScreen;
    }

    @Override
    public void react(Entity other, VERB verb) {
        if(this.inactive) { return; }

        switch(other.type) {
            case ROBOT:
                this.parent.killRobot(this);
                this.parent.killRobot(other);
                this.parent.addJunk(this.getPositionIndex());
                break;
            case JUNK:
                this.parent.killRobot(this);
                other.react(this, Entity.VERB.REFRESH);
        }
    }

    @Override
    public void act() {
        if(this.inactive) { return; }

        Vector2 playerPosition = this.parent.player.getPosition();
        Vector2 newPos = new Vector2(this.position);

        if(playerPosition.x < this.position.x) {
            newPos.x--;
        } else if(playerPosition.x > this.position.x) {
            newPos.x++;
        }

        if(playerPosition.y < this.position.y) {
            newPos.y--;
        } else if(playerPosition.y > this.position.y) {
            newPos.y++;
        }

        if(!this.parent.playfield.spaceBlocked(newPos)) {
            this.position.set(newPos);
        }
    }
}
