package com.wholebean.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

public class RobotsGame extends com.badlogic.gdx.Game {
	private Screen currentScreen;
	private Texture spriteSheet;
	public Texture overlay;

	public static SCREEN_ORIENTATION orientation;

	public BitmapFont font;
	public BitmapFont largeFont;

	public SpriteBatch spriteBatch;
	public Array<Array<TextureRegion>> graphics;

	public enum SCREEN_ORIENTATION {
		PORTRAIT,
		LANDSCAPE
	}

	private static final int SPRITE_COUNT = 7;
	public enum SPRITE_INFO {
		PLAYER_STANDING(0, 1),
		PLAYER_WALKING(1, 3),
		WALL_STATIC(2, 2),
		JUNK_IDLE(3, 2),
		ROBOT_IDLE(4, 3),
		HATCH(5, 2),
		CHIP(6, 1);

		SPRITE_INFO(int index, int frameCount) {
			this.INDEX 		 = index;
			this.FRAME_COUNT = frameCount;
		}

		public final int INDEX;
		public final int FRAME_COUNT;
	}

	public RobotsGame(SCREEN_ORIENTATION orientation) {
		RobotsGame.orientation = orientation;
	}

	@Override
	public void create () {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("PressStart2P.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

		param.color = Color.WHITE;
		param.size = 8;

		this.font = gen.generateFont(param);

		param.size = 16;
		this.largeFont = gen.generateFont(param);

		this.spriteBatch = new SpriteBatch();

		this.spriteSheet = new Texture(Gdx.files.internal("spriteSheet.png"));
		this.overlay = new Texture(Gdx.files.internal("overlay.png"));
		this.generateGraphics();

		this.currentScreen = new Menu(this);
		this.setScreen(this.currentScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		this.font.dispose();
		this.spriteBatch.dispose();
		this.spriteSheet.dispose();
		this.overlay.dispose();
		this.currentScreen.dispose();

	}

	public void startGame() {
		this.currentScreen.dispose();
		this.currentScreen = new Main(this);
		this.setScreen(this.currentScreen);
	}

	private void generateGraphics() {
		TextureRegion[][] tiles = Utils.fixBleeding(TextureRegion.split(this.spriteSheet, 16, 16));
		if(RobotsGame.SPRITE_COUNT != tiles.length) {
			throw new java.lang.Error("Sprite sheet contains more rows than number of sprites");
		}

		this.graphics = new Array<Array<TextureRegion>>(tiles.length);

		Array<TextureRegion> temp;

		for(SPRITE_INFO info : SPRITE_INFO.values()) {
			temp = new Array<TextureRegion>(info.FRAME_COUNT);
			for (int x = 0; x < info.FRAME_COUNT; x++) {
				temp.add(tiles[info.INDEX][x]);
			}
			this.graphics.add(temp);
		}
	}
}
