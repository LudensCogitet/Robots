package com.wholebean.robots;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by john on 9/23/18.
 */

public class Robot extends Entity {
    Main parent;

    Robot(int position, Main gameScreen) {
        super(TYPE.ROBOT, position, 0.3f, gameScreen.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.ROBOT_IDLE.INDEX));
        this.parent = gameScreen;
    }

    @Override
    public void act() {
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

        if(this.parent.playfield.spaceClear(newPos)) {
            this.position.set(newPos);
        }
    }
}
