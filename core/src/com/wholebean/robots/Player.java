package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.wholebean.robots.Entity.TYPE.JUNK;
import static com.wholebean.robots.Entity.TYPE.ROBOT;

/**
 * Created by john on 9/22/18.
 */

public class Player extends Entity implements InputProcessor {
    private Main parent;

    private TextureRegion phantom;
    private Vector2 phantomPosition;
    private boolean drawPhantom = false;

    private static final float deadZone = 50f;
    private Vector2 touchPoint;

    private int teleportPosition = 0;
    private int teleportOffset = 0;

    private boolean ignoreInput = false;

    Player(int position, Main gameScreen) {
        super(TYPE.PLAYER, position, 0.2f, gameScreen.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.PLAYER_WALKING.INDEX), false);
        this.parent = gameScreen;
        this.phantom = this.parent.robotsGameRef.graphics.get(RobotsGame.SPRITE_INFO.PLAYER_STANDING.INDEX).get(0);
        this.phantomPosition = new Vector2(this.position);
    }

    private void checkCollision(Entity other) {
        switch(other.type) {
            case ROBOT:
                this.parent.killRobot(other);
                this.parent.addJunk(other.getPositionIndex());
            case JUNK:
                this.parent.lose();
            case CHIP:
                this.parent.collectChip(other);
        }
    }

    public void reset(int position) {
        this.setPosition(position);
        this.setIgnoreInput(false);
        this.phantomPosition.set(this.position);
        this.touchPoint = null;
    }

    public void setIgnoreInput(boolean value) {
        this.ignoreInput = value;
    }

    @Override
    public void react(Entity other, VERB verb) {
        if(verb == VERB.SAME_SPACE) {
            this.checkCollision(other);
        }
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        super.draw(delta, batch);
        if(this.drawPhantom) {
            batch.setColor(1f,1f,1f,0.3f);
            batch.draw(
                    this.phantom,
                    Playfield.getScreenX((int)this.phantomPosition.x),
                    Playfield.getScreenY((int)this.phantomPosition.y)
            );
            batch.setColor(1f,1f,1f,1f);
        }

        this.teleportPosition += 11;
        if(this.teleportPosition >= Playfield.numberOfSquares) {
            this.teleportPosition -= Playfield.numberOfSquares;
        }
    }

    private void teleport() {
        int teleportTo = this.teleportPosition;
        while(this.parent.playfield.spaceBlocked(teleportTo)) {
            teleportTo++;

            if(teleportTo == Playfield.numberOfSquares) {
                teleportTo = 0;
            }
        }

        this.position = Utils.coordsFromIndex(teleportTo, Playfield.width);
        this.phantomPosition.set(this.position);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(this.ignoreInput) { return false; }

        if(pointer == 0) {
            this.touchPoint = new Vector2(screenX, screenY);
        } else {
            this.teleport();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(this.ignoreInput || pointer > 0) { return false; }

        this.drawPhantom = false;

        if(!this.parent.playfield.spaceBlocked(this.phantomPosition)) {
            this.position.set(this.phantomPosition);

            Array<Entity> entities = this.parent.entitiesAt(this.getPositionIndex());

            if(entities != null) {
                for(int i = 0; i < entities.size; i++) {
                    Entity entity = entities.get(i);
                    if(entity.type == JUNK || entity.type == ROBOT) {
                        this.checkCollision(entity);
                        break;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(this.ignoreInput || this.touchPoint == null) { return false; }

        if(pointer > 0) {
            return false;
        }

        Vector2 dragPoint = new Vector2(screenX, screenY);

        if(this.touchPoint.dst(dragPoint) > Player.deadZone) {
            this.drawPhantom = true;

            int distance = 1;
            if(this.touchPoint.dst(dragPoint) > Player.deadZone * 5f) {
                distance = 2;
            }

            Vector2 diff = dragPoint.sub(this.touchPoint);
            this.phantomPosition.set(this.position);
            float angle = diff.angle();

            if(angle > 337.5f || angle < 22.5f) {
                this.phantomPosition.x += distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                       this.parent.playfield.spaceBlocked((int) this.phantomPosition.x - 1, (int) this.phantomPosition.y)) {
                        this.phantomPosition.x--;
                    }
                }
            } else if(angle > 22.5f && angle < 67.5f) {
                this.phantomPosition.x += distance;
                this.phantomPosition.y -= distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                       this.parent.playfield.spaceBlocked((int) this.phantomPosition.x - 1, (int) this.phantomPosition.y + 1)) {
                        this.phantomPosition.x--;
                        this.phantomPosition.y++;
                    }
                }
            } else if(angle > 67.5f && angle < 112.5f) {
                this.phantomPosition.y -= distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                       this.parent.playfield.spaceBlocked((int) this.phantomPosition.x, (int) this.phantomPosition.y + 1)) {
                        this.phantomPosition.y++;
                    }
                }
            } else if(angle > 112.5f && angle < 157.5) {
                this.phantomPosition.x -= distance;
                this.phantomPosition.y -= distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                       this.parent.playfield.spaceBlocked((int) this.phantomPosition.x + 1, (int) this.phantomPosition.y + 1)) {
                        this.phantomPosition.x++;
                        this.phantomPosition.y++;
                    }
                }
            } else if(angle > 157.5f && angle < 202.5) {
                this.phantomPosition.x -= distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                            this.parent.playfield.spaceBlocked((int) this.phantomPosition.x + 1, (int) this.phantomPosition.y)) {
                        this.phantomPosition.x++;
                    }
                }
            } else if(angle > 202.5f && angle < 247.5) {
                this.phantomPosition.x -= distance;
                this.phantomPosition.y += distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                            this.parent.playfield.spaceBlocked((int) this.phantomPosition.x + 1, (int) this.phantomPosition.y - 1)) {
                        this.phantomPosition.x++;
                        this.phantomPosition.y--;
                    }
                }
            } else if(angle > 247.5f && angle < 292.5) {
                this.phantomPosition.y += distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                            this.parent.playfield.spaceBlocked((int) this.phantomPosition.x, (int) this.phantomPosition.y - 1)) {
                        this.phantomPosition.y--;
                    }
                }
            } else if(angle > 292.5f && angle < 337.5) {
                this.phantomPosition.x += distance;
                this.phantomPosition.y += distance;
                if(distance > 1) {
                    if(this.parent.playfield.spaceBlocked(this.phantomPosition) ||
                            this.parent.playfield.spaceBlocked((int) this.phantomPosition.x - 1, (int) this.phantomPosition.y - 1)) {
                        this.phantomPosition.x--;
                        this.phantomPosition.y--;
                    }
                }
            }

            if(this.parent.playfield.spaceBlocked(this.phantomPosition)) {
                this.phantomPosition.set(this.position);
                this.drawPhantom = false;
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(this.ignoreInput) { return false; }

        if(keycode == Input.Keys.SPACE) {
            this.teleport();
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
