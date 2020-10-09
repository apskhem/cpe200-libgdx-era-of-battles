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
	private Image base;
	private Image opBase;
	private OrthographicCamera cam;

	private Music themeMusic;
	
	@Override
	public void create() {
		r = new Renderer();
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bg = new Image("game-bg.png");
		base = new Image("base-era-1.png");
		opBase = new Image("base-era-1.png");

		// theme music
		themeMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/theme.mp3"));
		themeMusic.play();
		themeMusic.setLooping(true);
		themeMusic.setVolume(0.7f);

		base.src.setPosition(-80, 0);
		opBase.src.flip(true, false);
		opBase.src.setPosition(1760, 0);

		// set camera
		cam.translate(cam.viewportWidth/2, cam.viewportHeight/2);

		r.AddComponents(bg, base, opBase);
		r.AddCamera(cam);
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			if (cam.position.x > cam.viewportWidth / 2) {
				cam.translate(-10f , 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			if (cam.position.x < 2080 - cam.viewportWidth / 2) {
				cam.translate(10f, 0);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			cam.position.x = cam.viewportWidth/2;
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
