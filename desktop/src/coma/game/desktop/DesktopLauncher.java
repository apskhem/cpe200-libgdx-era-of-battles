package coma.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import coma.game.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		// congfigs
		config.title = "Era Of Battles (LibGDX Engine)";
		config.width = 960;
		config.height = 600;
		config.resizable = false;

		new LwjglApplication(new MainGame(), config);
	}
}
