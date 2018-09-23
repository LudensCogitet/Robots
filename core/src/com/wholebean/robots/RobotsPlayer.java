package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by john on 9/22/18.
 */

public class RobotsPlayer extends RobotsEntity implements InputProcessor {
    private RobotsGame game;
    private RobotsPlayfield playfield;
    private TextureRegion phantom;

    private Vector2 phantomPosition;
    private boolean drawPhantom = false;

    private static final float deadZone = 50f;
    private Vector2 touchPoint;

    RobotsPlayer(int position, RobotsGame game, RobotsPlayfield playfield) {
        super(TYPE.PLAYER, position, 0.4f, game.graphics.get(RobotsGame.SPRITE_INFO.PLAYER_WALKING.INDEX));
        this.game = game;
        this.playfield = playfield;
        this.phantom = this.game.graphics.get(RobotsGame.SPRITE_INFO.PLAYER_STANDING.INDEX).get(0);
        this.phantomPosition = new Vector2(this.position);
        this.phantomPosition.y++;
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        super.draw(delta, batch);
        if(this.drawPhantom) {
            batch.setColor(1f,1f,1f,0.3f);
            batch.draw(
                    this.phantom,
                    RobotsPlayfield.getScreenX((int)this.phantomPosition.x),
                    RobotsPlayfield.getScreenY((int)this.phantomPosition.y)
            );
            batch.setColor(1f,1f,1f,1f);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(pointer == 0) {
            this.touchPoint = new Vector2(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.drawPhantom = false;

        if(this.playfield.spaceClear(this.phantomPosition)) {
            this.position.set(this.phantomPosition);
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(pointer > 0) {
            return false;
        }

        Vector2 dragPoint = new Vector2(screenX, screenY);

        if(this.touchPoint.dst(dragPoint) > RobotsPlayer.deadZone) {
            this.drawPhantom = true;

            int distance = 1;
            if(this.touchPoint.dst(dragPoint) > RobotsPlayer.deadZone * 3f) {
                distance = 2;
            }

            Vector2 diff = dragPoint.sub(this.touchPoint);
            this.phantomPosition.set(this.position);
            float angle = diff.angle();

            if(angle > 337.5f || angle < 22.5f) {
                this.phantomPosition.x += distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                       !this.playfield.spaceClear((int) this.phantomPosition.x - 1, (int) this.phantomPosition.y)) {
                        this.phantomPosition.x--;
                    }
                }
            } else if(angle > 22.5f && angle < 67.5f) {
                this.phantomPosition.x += distance;
                this.phantomPosition.y -= distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                       !this.playfield.spaceClear((int) this.phantomPosition.x - 1, (int) this.phantomPosition.y + 1)) {
                        this.phantomPosition.x--;
                        this.phantomPosition.y++;
                    }
                }
            } else if(angle > 67.5f && angle < 112.5f) {
                this.phantomPosition.y -= distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                            !this.playfield.spaceClear((int) this.phantomPosition.x, (int) this.phantomPosition.y + 1)) {
                        this.phantomPosition.y++;
                    }
                }
            } else if(angle > 112.5f && angle < 157.5) {
                this.phantomPosition.x -= distance;
                this.phantomPosition.y -= distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                            !this.playfield.spaceClear((int) this.phantomPosition.x + 1, (int) this.phantomPosition.y + 1)) {
                        this.phantomPosition.x++;
                        this.phantomPosition.y++;
                    }
                }
            } else if(angle > 157.5f && angle < 202.5) {
                this.phantomPosition.x -= distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                            !this.playfield.spaceClear((int) this.phantomPosition.x + 1, (int) this.phantomPosition.y)) {
                        this.phantomPosition.x++;
                    }
                }
            } else if(angle > 202.5f && angle < 247.5) {
                this.phantomPosition.x -= distance;
                this.phantomPosition.y += distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                            !this.playfield.spaceClear((int) this.phantomPosition.x + 1, (int) this.phantomPosition.y - 1)) {
                        this.phantomPosition.x++;
                        this.phantomPosition.y--;
                    }
                }
            } else if(angle > 247.5f && angle < 292.5) {
                this.phantomPosition.y += distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                            !this.playfield.spaceClear((int) this.phantomPosition.x, (int) this.phantomPosition.y - 1)) {
                        this.phantomPosition.y--;
                    }
                }
            } else if(angle > 292.5f && angle < 337.5) {
                this.phantomPosition.x += distance;
                this.phantomPosition.y += distance;
                if(distance > 1) {
                    if(!this.playfield.spaceClear(this.phantomPosition) ||
                            !this.playfield.spaceClear((int) this.phantomPosition.x - 1, (int) this.phantomPosition.y - 1)) {
                        this.phantomPosition.x--;
                        this.phantomPosition.y--;
                    }
                }
            }

            if(!this.playfield.spaceClear(this.phantomPosition)) {
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
