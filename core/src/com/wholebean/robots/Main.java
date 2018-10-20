package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by john on 8/28/18.
 */

public class Main implements Screen, InputProcessor {
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
    //private Label seedDisplay;
    private Label timeElapsedLabel;
    private Label levelLabel;
    private Label robotsLeftLabel;

    private Label titleLabel;
    private Label subtitleLabel;

    private Table notifications;

    private float timeElapsed = 0;

    private int robotsOnField = 0;
    private int robotsKilled = 0;
    private int currentLevel = 0;
    private final int numLevels = 1;

    private static final int PLAYING = 0;
    private static final int WIN = 1;
    private static final int LOSE = 2;
    private static final int PAUSE = 3;

    private static int gameState = PLAYING;
    private boolean suddenDeath = false;

    private Timer.Task actionLoop = new Timer.Task() {
        @Override
        public void run() {
            act();
            resolve();
            cleanUpEntities();
            if(robotsOnField + robotsKilled < playfield.robotsToKill) {
                spawn();
            }
            robotsLeftLabel.setText((robotsKilled < 10 ? "0" + Integer.toString(robotsKilled) : Integer.toString(robotsKilled)) + "/" + Integer.toString(playfield.robotsToKill));
            robotsLeftLabel.pack();

            if(Main.gameState == PLAYING && robotsKilled == playfield.robotsToKill) {
                showWinScreen();
            }

            if(!suddenDeath) {
                checkSuddenDeath();
            }
        }
    };

    Main(RobotsGame robotsGameReference) {
        this.robotsGameRef = robotsGameReference;

        this.viewport = new StretchViewport(Playfield.logicalScreenWidth, Playfield.logicalScreenHeight);

        this.ui = new Stage(this.viewport);

        Label.LabelStyle indicatorStyle = new Label.LabelStyle();

        indicatorStyle.font = this.robotsGameRef.font;
        indicatorStyle.fontColor = Color.WHITE;

        int indicatorLabelY = Playfield.spaceSize * 29;

        this.timeElapsedLabel = new Label("00:00", indicatorStyle);
        this.timeElapsedLabel.setPosition((Playfield.spaceSize + Playfield.spaceSize / 2) - 4, indicatorLabelY);

        this.levelLabel = new Label(Integer.toString(this.currentLevel + 1), indicatorStyle);
        this.levelLabel.setPosition((Playfield.logicalScreenWidth / 2) - 4, indicatorLabelY);

        this.robotsLeftLabel = new Label("00/00", indicatorStyle);
        this.robotsLeftLabel.setPosition(Playfield.logicalScreenWidth - Playfield.spaceSize * 4, indicatorLabelY);

        Label.LabelStyle pauseStyle = new Label.LabelStyle();

        pauseStyle.font = this.robotsGameRef.largeFont;
        pauseStyle.fontColor = Color.WHITE;

        this.notifications = new Table();
        this.notifications.setFillParent(true);

        this.titleLabel = new Label("You Win!", pauseStyle);
        this.subtitleLabel = new Label("Tap to continue", indicatorStyle);

        this.notifications.add(this.titleLabel).padBottom(Playfield.spaceSize).row();
        this.notifications.add(this.subtitleLabel).row();

        this.notifications.setVisible(false);

        this.ui.addActor(new Image(this.robotsGameRef.overlay));
        this.ui.addActor(this.timeElapsedLabel);
        this.ui.addActor(this.levelLabel);
        this.ui.addActor(this.robotsLeftLabel);
        this.ui.addActor(this.notifications);

        this.playfield = new Playfield(this, new String[]{"level1"});
        this.player = new Player(this.playfield.loadBoard(0), this);
        Gdx.input.setInputProcessor(new InputMultiplexer(this.player, this));

        this.entities.add(this.player);

        this.playfield.loadBoard(this.currentLevel);

        Timer.schedule(this.actionLoop, this.playfield.step, this.playfield.step);
    }

    public void act() {
        for(int i = 0; i < entities.size; i++) {
            entities.get(i).act();
        }
    }

    private void spawn() {
        this.playfield.closeHatches();

        Array<Robot> newRobots = this.playfield.spawnRobots(
                MathUtils.clamp(
                        this.playfield.robotDensity - this.robotsOnField,
                        0,
                        this.playfield.robotsToKill - (robotsOnField + robotsKilled))
        );

        if(newRobots != null) {
            this.robotsOnField += newRobots.size;
            this.entities.addAll(newRobots);
        }
    }

    private void checkSuddenDeath() {
        if(this.robotsKilled == this.playfield.robotsToKill - 1) {
            for(int i = 0; i < this.entities.size; i++) {
                Entity entity = this.entities.get(i);

                if(entity.type == Entity.TYPE.ROBOT) {
                    ((Robot) entity).enterOverdrive(this.playfield.overdriveCountdown);
                    break;
                }
            }

            Timer.instance().clear();
            Timer.schedule(this.actionLoop, this.playfield.step / 2, this.playfield.step / 2);
            this.suddenDeath = true;
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

    private void setGameState(int state) {
        Main.gameState = state;

        if(Main.gameState == PLAYING) {
            Timer.schedule(this.actionLoop, this.playfield.step, this.playfield.step);
        } else {
            Timer.instance().clear();
        }
    }

    private void showWinScreen() {
        this.player.setIgnoreInput(true);
        this.setGameState(WIN);

        this.titleLabel.setText("You Win!");
        this.titleLabel.setVisible(true);
        this.titleLabel.pack();

        this.subtitleLabel.setText("Tap to continue");
        this.subtitleLabel.setVisible(true);
        this.subtitleLabel.pack();

        this.notifications.setVisible(true);
    }

    private void showLoseScreen() {

    }

    private void reset(int playerPos) {
        this.robotsKilled = 0;
        this.suddenDeath = false;
        this.player.reset(playerPos);
        this.entities.clear();
        this.entities.add(this.player);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        //int mb = 1024 * 1024;
        //Gdx.app.log("Memory usage", Long.toString((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/mb));
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.robotsGameRef.spriteBatch.begin();
        this.robotsGameRef.spriteBatch.setProjectionMatrix(this.viewport.getCamera().combined);

        for(int i = 0; i < this.entities.size; i++) {
            Entity entity = this.entities.get(i);
            if(Main.gameState == PLAYING) {
                entity.update(delta);
            }

            entity.draw(delta, this.robotsGameRef.spriteBatch);

        }
        this.playfield.draw(delta, this.robotsGameRef.spriteBatch);
        this.robotsGameRef.spriteBatch.end();

        this.levelLabel.setText(Integer.toString(this.currentLevel +1));
        this.levelLabel.pack();

        if(Main.gameState == PLAYING) {
            this.timeElapsed += delta;
            int minutes = (int) this.timeElapsed / 60;
            int seconds = (int) this.timeElapsed % 60;

            String timeElapsedString = minutes < 10 ? "0" + Integer.toString(minutes) :  Integer.toString(minutes);

            timeElapsedString += ":";

            timeElapsedString += seconds < 10 ? "0" +  Integer.toString(seconds) : Integer.toString(seconds);

            this.timeElapsedLabel.setText(timeElapsedString);
            this.timeElapsedLabel.pack();
        }

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

//        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
//            this.seed++;
//            this.seedDisplay.setText(Long.toString(this.seed));
//            this.seedDisplay.pack();
//            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
//        }
//
//        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
//            this.seed--;
//            this.seedDisplay.setText(Long.toString(this.seed));
//            this.seedDisplay.pack();
//            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
//        }
//
//        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
//            this.seed += 10;
//            this.seedDisplay.setText(Long.toString(this.seed));
//            this.seedDisplay.pack();
//            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
//        }
//
//        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
//            this.seed -= 10;
//            this.seedDisplay.setText(Long.toString(this.seed));
//            this.seedDisplay.pack();
//            this.playfield.generateBoard(this.seed, this.wallFrequency, this.numberOfSpawnPoints);
//        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            this.setGameState(PAUSE);
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(Main.gameState == WIN) {
            if(this.currentLevel == this.numLevels -1) {
                this.currentLevel = 0;
            }

            this.reset(this.playfield.loadBoard(this.currentLevel));
            this.setGameState(PLAYING);
            this.notifications.setVisible(false);
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) { return false; }

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
