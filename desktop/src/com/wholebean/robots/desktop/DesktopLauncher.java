package com.wholebean.robots.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.wholebean.robots.RobotsGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 540;  // 1080p / 2
		config.height = 960;
		//config.fullscreen = true;
		new LwjglApplication(new RobotsGame(RobotsGame.SCREEN_ORIENTATION.PORTRAIT), config);
	}
}
