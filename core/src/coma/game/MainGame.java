package coma.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends ApplicationAdapter {

	// all game source will be here
	private Renderer r;
	private Image bg;
	private OrthographicCamera camera;

	private Player user;
	private Player foe;

	private Music themeMusic;
	
	@Override
	public void create() {
		r = new Renderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bg = new Image("game-bg.png");
		user = new Player(false);
		foe = new Player(true);

		// theme music
		themeMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/theme.mp3"));
		themeMusic.play();
		themeMusic.setLooping(true);
		themeMusic.setVolume(0.7f);

		// set camera
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);

		r.AddComponents(camera, bg, user.base, foe.base);
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (camera.position.x > camera.viewportWidth / 2) {
				camera.translate(-10f , 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (camera.position.x < 2080 - camera.viewportWidth / 2) {
				camera.translate(10f, 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			camera.position.x = camera.viewportWidth/2;
		}
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			System.out.println("clicked");
		}

		// float mouseYPercentage = Gdx.input.getY() / 600f;
		// float mappedMouseY = 600 * (1 - mouseYPercentage);
		// System.out.println(Gdx.input.getX() + " " + (int) mappedMouseY);

		r.Update();
	}
	
	@Override
	public void dispose() {
		r.Close();

		themeMusic.dispose();
	}
}
