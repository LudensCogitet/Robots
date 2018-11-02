package com.wholebean.robots;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by john on 9/23/18.
 */

public class Robot extends Entity {
    Main parent;

    private boolean overdrive = false;
    private int selfDestruct = 0;

    Robot(int position, Main gameScreen) {
        super(TYPE.ROBOT, position, 0.3f, gameScreen.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.ROBOT_IDLE.INDEX), false);
        this.parent = gameScreen;
    }

    public void enterOverdrive(int selfDestruct) {
        this.overdrive = true;
        this.selfDestruct = selfDestruct;
    }

    @Override
    public void react(Entity other, VERB verb) {
        if(this.inactive || other.isInactive()) { return; }

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

        if(this.overdrive) {
            this.selfDestruct--;
            if(this.selfDestruct <= 0) {
                this.parent.killRobot(this);
                this.parent.addJunk(Utils.indexFromCoords(this.position, Playfield.width));
                return;
            }
        }

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
        } else if(this.overdrive) {
            Entity entity = this.parent.playfield.entityAtPosition(newPos);
            if(entity.type == TYPE.WALL) {
                ((Wall) entity).smash();
                this.position.set(newPos);
            }
        }
    }

    @Override
    public void reset(int position) {
        super.reset(position);
        this.overdrive = false;
        this.selfDestruct = 0;
    }
}
