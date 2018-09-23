package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by john on 8/28/18.
 */

public class RobotsGameScreen implements Screen {
    private RobotsGame gameRef;

    private Stage ui;
    private StretchViewport viewport;
    private RobotsPlayfield playfield;
    private long seed = 1;
    private float wallFrequency = 0.1f;
    private Label seedDisplay;
    private RobotsPlayer player;

    RobotsGameScreen(RobotsGame gameReference) {
        this.gameRef = gameReference;

        this.viewport = new StretchViewport(RobotsPlayfield.logicalScreenWidth, RobotsPlayfield.logicalScreenHeight);

        this.ui = new Stage(this.viewport);

        Label.LabelStyle labelStyle = new Label.LabelStyle();

        labelStyle.font = this.gameRef.font;
        labelStyle.fontColor = Color.WHITE;

        this.seedDisplay = new Label(Long.toString(this.seed), labelStyle);
        this.seedDisplay.setPosition(8, 8);

        this.ui.addActor(this.seedDisplay);

        this.playfield = new RobotsPlayfield(this.gameRef, this.seed, this.wallFrequency);
        this.player = new RobotsPlayer(RobotsPlayfield.playerStart, this.gameRef, this.playfield);
        Gdx.input.setInputProcessor(this.player);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.gameRef.spriteBatch.begin();
        this.gameRef.spriteBatch.setProjectionMatrix(this.viewport.getCamera().combined);

        this.playfield.draw(delta, this.gameRef.spriteBatch);
        this.player.draw(delta, this.gameRef.spriteBatch);
        this.gameRef.spriteBatch.end();

        this.ui.act(delta);
        this.ui.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            this.seed++;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            this.seed--;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            this.seed += 10;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            this.seed -= 10;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency);
        }
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.ui.dispose();
    }
}
