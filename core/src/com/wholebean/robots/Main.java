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
    public Array<Robot> robotPool = new Array<Robot>(false, 8);
    public Array<Junk> junkPool = new Array<Junk>(false, 8);

    private Stage ui;
    private StretchViewport viewport;
    private long seed = 1;
    private float wallFrequency = 0.1f;
    private int numberOfSpawnPoints = 8;
    private Label seedDisplay;

    private float robotDensity = 0.01f;
    private int robotsToKill = 25;

    private int robotsOnField = 0;
    private int robotsKilled = 0;

    private float step = 0.85f;

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
        this.playfield = new Playfield(this);
        this.player = this.playfield.loadBoard("testBoard");//new Player(Playfield.playerStart, this);
        Gdx.input.setInputProcessor(this.player);

        this.entities.add(this.player);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                act();
                resolve();
                cleanUpEntities();
                if(robotsKilled < robotsToKill) {
                    spawn();
                }
            }
        }, this.step, this.step);
    }

    public void act() {
        for(int i = 0; i < entities.size; i++) {
            entities.get(i).act();
        }
    }

    private void spawn() {
        this.playfield.closeHatches();

        Array<Robot> newRobots = this.playfield.spawnRobots(((int)(Playfield.numberOfSquares * this.robotDensity) - this.robotsOnField));

        if(newRobots != null) {
            this.robotsOnField += newRobots.size;
            this.entities.addAll(newRobots);
        }
    }

    public Robot getRobot(int position) {
        Robot robot;
        if(this.robotPool.size > 0) {
            robot = this.robotPool.pop();
            robot.reset(position);
        } else {
            robot = new Robot(position, this);
        }

        return robot;
    }

    public Junk getJunk(int position) {
        Junk junk;
        if(this.junkPool.size > 0) {
            junk = this.junkPool.pop();
            junk.reset(position);
        } else {
            junk = new Junk(position, this);
        }

        return junk;
    }

    public void addJunk(int position) {
        this.entities.add(this.getJunk(position));
    }

    public void killRobot(Entity robot) {
        if(robot.type != Entity.TYPE.ROBOT) { return; }
        this.robotsKilled++;
        this.robotsOnField--;
        robot.deactivate();
    }

    public Entity entityAt(int position) {
        Entity entity;
        for(int i = 0; i < this.entities.size; i++) {
            entity = this.entities.get(i);
            if(entity.getPositionIndex() == position) {
                return entity;
            }
        }

        return null;
    }

    private void resolve() {
        for(int i = 0; i < this.entities.size; i++) {
            Entity entity = this.entities.get(i);
            if(entity.isInactive()) { continue; }
            for(int j = 0; j < this.entities.size; j++) {
                Entity other = this.entities.get(j);
                if(other.isInactive() || entity == other) { continue; }
                if(entity.getPositionIndex() == other.getPositionIndex()) {
                    entity.react(other, Entity.VERB.SAME_SPACE);
                }
            }
        }
    }

    private void cleanUpEntities() {
        Array<Entity> toDelete = new Array<Entity>();

        for(int i = 0; i < this.entities.size; i++) {
            Entity entity = this.entities.get(i);
            if(entity.isInactive()) {
                toDelete.add(entity);
                switch(entity.type) {
                    case ROBOT:
                        this.robotPool.add((Robot) entity);
                        break;
                    case JUNK:
                        this.junkPool.add((Junk) entity);
                }
            }
        }

        this.entities.removeAll(toDelete, true);
    }

    @Override
    public void show() {}

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

        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            act();
            resolve();
            spawn();
            cleanUpEntities();
            Gdx.app.log("Number of Entities:", Integer.toString(this.entities.size));
            Gdx.app.log("Robots killed:", Integer.toString(this.robotsKilled));

            for(int i = 0; i < this.entities.size; i++) {
                String type = "NONE";
                switch(this.entities.get(i).type) {
                    case ROBOT:
                        type = "ROBOT";
                        break;
                    case PLAYER:
                        type = "PLAYER";
                        break;
                    case JUNK:
                        type = "JUNK";
                        break;
                }
                Gdx.app.log("Entity type: ", type);
            }
        }

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
