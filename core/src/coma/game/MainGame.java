package coma.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends ApplicationAdapter {

	// all game source will be here
	Renderer r;
	SpriteBatch batch;
	BackgroundObject bg;
	TextureObject base;
	TextureObject opBase;

	Music themeMusic;
	
	@Override
	public void create() {
		batch = new SpriteBatch();

		r = new Renderer();
		bg = new BackgroundObject("game-bg.png");
		base = new TextureObject("base-era-1.png");
		base.SetPosition(-80, 10);
		opBase = new TextureObject("op-base-era-1.png");
		opBase.SetPosition(1740, 10);

		// theme music
		themeMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/theme.mp3"));
		themeMusic.play();
		themeMusic.setLooping(true);
		themeMusic.setVolume(0.7f);

		r.AddRenderableObjects(bg, base, opBase);
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bg.SetTranslateX(bg.translateX - 10);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bg.SetTranslateX(bg.translateX + 10);
		}
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			System.out.println("clicked");
		}

		// set textures translation
		base.Translate(bg.translateX, 0);
		opBase.Translate(bg.translateX, 0);

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
