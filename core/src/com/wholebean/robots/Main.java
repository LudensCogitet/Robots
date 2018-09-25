package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by john on 8/28/18.
 */

public class Main implements Screen {
    public RobotsGame robotsGameRef;
    public Playfield playfield;
    public Player player;
    public Array<Entity> entities = new Array<Entity>();

    private Stage ui;
    private StretchViewport viewport;
    private long seed = 1;
    private float wallFrequency = 0.05f;
    private int numberOfSpawnPoints = 8;
    private Label seedDisplay;

    private float step = 0.75f;

    Main(RobotsGame robotsGameReference) {
        this.robotsGameRef = robotsGameReference;

        this.viewport = new StretchViewport(Playfield.logicalScreenWidth, Playfield.logicalScreenHeight);

        this.ui = new Stage(this.viewport);

        Label.LabelStyle labelStyle = new Label.LabelStyle();

        labelStyle.font = this.robotsGameRef.font;
        labelStyle.fontColor = Color.WHITE;

        this.seedDisplay = new Label(Long.toString(this.seed), labelStyle);
        this.seedDisplay.setPosition(8, 8);

        this.ui.addActor(this.seedDisplay);
        this.ui.addActor(new Image(this.robotsGameRef.overlay));
        this.playfield = new Playfield(this.robotsGameRef, this.seed, this.wallFrequency, this.numberOfSpawnPoints);
        this.player = new Player(Playfield.playerStart, this);
        Gdx.input.setInputProcessor(this.player);

        this.entities.add(this.player);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                for(int i = 0; i < entities.size; i++) {
                    entities.get(i).act();
                }
            }
        }, this.step, this.step);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.robotsGameRef.spriteBatch.begin();
        this.robotsGameRef.spriteBatch.setProjectionMatrix(this.viewport.getCamera().combined);

        for(int i = 0; i < this.entities.size; i++) {
            this.entities.get(i).draw(delta, this.robotsGameRef.spriteBatch);

        }
        this.playfield.draw(delta, this.robotsGameRef.spriteBatch);
        this.robotsGameRef.spriteBatch.end();

        this.ui.act(delta);
        this.ui.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            this.seed++;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            this.seed--;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            this.seed += 10;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            this.seed -= 10;
            this.seedDisplay.setText(Long.toString(this.seed));
            this.seedDisplay.pack();
            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
        }

        Gdx.app.log("longAxisOffset", Float.toString(Playfield.longAxisOffset));
        Gdx.app.log("shortAxisOffset", Float.toString(Playfield.shortAxisOffset));
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
