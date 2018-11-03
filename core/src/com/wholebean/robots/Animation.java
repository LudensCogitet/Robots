package com.wholebean.robots;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by john on 11/1/18.
 */

public class Animation {
    public enum ANIMATION_TYPE {
        FORWARD,
        BACKWARD,
        PINGPONG
    }

    protected Array<TextureRegion> frames;
    protected int currentFrame = 0;

    protected float accumulator = 0f;

    protected boolean looping;
    protected boolean playing = false;

    protected float playbackSpeed;
    protected int playbackDirection;

    protected ANIMATION_TYPE animationType;

    Animation(Array<TextureRegion> frames, float playbackSpeed, ANIMATION_TYPE animationType, boolean looping) {
        this.frames = new Array<TextureRegion>(frames);
        this.animationType = animationType;
        this.looping = looping;
        this.playbackSpeed = playbackSpeed;

        switch(this.animationType) {
            case PINGPONG:
            case FORWARD:
                this.playbackDirection = 1;
                break;
            case BACKWARD:
                this.playbackDirection = -1;
                break;
        }
    }

    public TextureRegion getFrame() {
        return this.frames.get(this.currentFrame);
    }

    public TextureRegion getFrame(int frame) {
        return this.frames.get(frame);
    }

    public void update(float delta) {
        if(!this.playing) { return; }

        this.accumulator += delta;

        if(this.accumulator >= this.playbackSpeed) {
            this.accumulator -= this.playbackSpeed;

            this.advanceFrame();
        }
    }

    public void start() {
        this.playing = true;
        switch(this.animationType) {
            case FORWARD:
                this.currentFrame = 0;
                break;
            case BACKWARD:
                this.currentFrame = this.frames.size -1;
                break;
        }
    }

    public void play() {
        this.playing = true;
    }

    public void pause() {
        this.playing = false;
    }

    public void setPlaybackSpeed(float speed) {
        this.playbackSpeed = speed;
    }

    protected void advanceFrame() {
        this.currentFrame += this.playbackDirection;
        switch(this.animationType) {
            case FORWARD:
                if(this.looping && this.currentFrame > this.frames.size -1) {
                    this.currentFrame = 0;
                } else {
                    this.playing = false;
                }
                break;
            case BACKWARD:
                if(this.looping && this.currentFrame < 0) {
                    this.currentFrame = this.frames.size -1;
                } else {
                    this.playing = false;
                }
                break;
            case PINGPONG:
                if(this.currentFrame > this.frames.size -1) {
                    this.playbackDirection = -1;
                    this.currentFrame = this.frames.size -2;
                } else if(this.currentFrame < 0) {
                    this.playbackDirection = 1;
                    this.currentFrame = 1;
                }
                break;
        }
    }
}
