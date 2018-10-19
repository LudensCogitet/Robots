package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by john on 8/27/18.
 */

public class Menu implements Screen {
    private RobotsGame robotsGame;

    private Animation<TextureRegion> animation;
    private float timeAccumulator = 0f;

    private Stage stage;

    Menu(RobotsGame robotsGameReference) {
        this.robotsGame = robotsGameReference;

        this.stage = new Stage(new StretchViewport(Playfield.logicalScreenWidth, Playfield.logicalScreenHeight));
        VerticalGroup buttonGroup = new VerticalGroup();
        buttonGroup.setFillParent(true);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.font = this.robotsGame.font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.LIGHT_GRAY;
        buttonStyle.downFontColor = Color.NAVY;

        Label.LabelStyle titleStyle = new Label.LabelStyle();

        titleStyle.font = this.robotsGame.font;
        titleStyle.fontColor = Color.WHITE;

        Label title = new Label("Robots!", titleStyle);
        title.setFontScale(2f);
        title.pack();
        TextButton startButton = new TextButton("Start", buttonStyle);
        startButton.addListener(new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
                robotsGame.startGame();
            }
        });

        TextButton highScoreButton = new TextButton("High Scores", buttonStyle);

        buttonGroup.addActor(title);
        buttonGroup.addActor(startButton.padBottom(Playfield.spaceSize).padTop(Playfield.spaceSize));
        buttonGroup.addActor(highScoreButton);

        this.stage.addActor(buttonGroup.padTop(Playfield.spaceSize * 4));

        Gdx.input.setInputProcessor(this.stage);

        this.animation = new Animation<TextureRegion>(0.3f, this.robotsGame.graphics.get(RobotsGame.SPRITE_INFO.ROBOT_IDLE.INDEX));
        this.animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.timeAccumulator += delta;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(delta);
        this.stage.draw();

        this.robotsGame.spriteBatch.setProjectionMatrix(this.stage.getViewport().getCamera().combined);
        this.robotsGame.spriteBatch.begin();

        TextureRegion frame = this.animation.getKeyFrame(this.timeAccumulator, true);

        this.robotsGame.spriteBatch.draw(
                frame,
                Playfield.spaceSize * (Playfield.width / 2) - 4,
                Playfield.spaceSize * 18
        );


        this.robotsGame.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
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
        this.stage.dispose();
    }
}
