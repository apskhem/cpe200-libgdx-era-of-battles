package coma.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
	private UIController ui;
	private OrthographicCamera camera;

	private Canvas playBtn;
	private Canvas creditBtn;
	private Canvas musicBtn;
	private Canvas mode1;
	private Canvas mode2;
	private Canvas mode3;
	private Canvas modeBanner;
	private Canvas startBtn;
	private Canvas unit1;
	private Canvas unit2;
	private Canvas unit3;
	private Canvas unit4;
	private Canvas unit5;
	private Canvas unitUl;
	private Canvas cashIcon;

	private Player user;
	private Player foe;

	private Music themeMusic;
	private Sound startSound;
	private Sound menuClickSound;
	
	@Override
	public void create() {
		r = new Renderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bg = new Image("game-bg.png");
		playBtn = new Canvas("play-btn.png");
		creditBtn = new Canvas("credit-btn.png");
		musicBtn = new Canvas("music-btn.png");
		mode1 = new Canvas("mode-1.png");
		mode2 = new Canvas("mode-2.png");
		mode3 = new Canvas("mode-3.png");
		modeBanner = new Canvas("mode-selection-banner.png");
		startBtn = new Canvas("start-btn.png");
		unit1 = new Canvas("unit-1.png");
		unit2 = new Canvas("unit-2.png");
		unit3 = new Canvas("unit-3.png");
		unit4 = new Canvas("unit-4.png");
		unit5 = new Canvas("unit-5.png");
		unitUl = new Canvas("unit-ul.png");
		cashIcon = new Canvas("cash-icon.png");
		ui = new UIController(camera);
		user = new Player(false);
		foe = new Player(true);

		playBtn.SetPosition("center", camera.viewportHeight/2);
		creditBtn.SetPosition("center", camera.viewportHeight/2 - 120);
		musicBtn.SetPosition(886, 14);
		mode1.SetPosition(80, "center");
		mode2.SetPosition("center", "center");
		mode3.SetPosition(640, "center");
		modeBanner.SetPosition("center", 440);
		startBtn.SetPosition("center", 80);
		startBtn.src.scale(-0.2f);
		unit1.SetPosition(566, 524);
		unit2.SetPosition(646, 524);
		unit3.SetPosition(726, 524);
		unit4.SetPosition(806, 524);
		unit5.SetPosition(886, 524);
		unitUl.SetPosition(770, 430);
		cashIcon.SetPosition(-28, 504);
		cashIcon.src.scale(-0.75f);

		ui.AddBoxModule("start-menu", playBtn, creditBtn);
		ui.AddBoxModule("mode-selection-menu", mode1, mode2, mode3, modeBanner, startBtn);
		ui.AddBoxModule("in-game-menu", unit1, unit2, unit3, unit4, unit5, unitUl, cashIcon);
		ui.GetBoxModule("in-game-menu").SetVisibility(false);
		ui.GetBoxModule("mode-selection-menu").SetVisibility(false);

		// theme music
		themeMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/theme.mp3"));
		themeMusic.play();
		themeMusic.setLooping(true);
		themeMusic.setVolume(0.7f);

		startSound = Gdx.audio.newSound(Gdx.files.internal("audio/start-game.mp3"));
		menuClickSound = Gdx.audio.newSound(Gdx.files.internal("audio/menu-click.mp3"));

		// set camera
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);

		ui.AddComponents(playBtn, creditBtn, musicBtn, mode1, mode2, mode3, modeBanner, startBtn,
				unit1, unit2, unit3, unit4, unit5, unitUl, cashIcon);
		r.AddComponents(camera,bg, user.stronghold, foe.stronghold, playBtn, creditBtn, musicBtn,
				mode1, mode2, mode3, modeBanner, startBtn, unit1, unit2, unit3, unit4, unit5, unitUl, cashIcon);
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// keyboard event
		if (GameStatus.isGameStarted) {
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
		}

		// click event
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			final int clientX = Gdx.input.getX();
			final int clientY = (int)(600 * (1 - Gdx.input.getY() / 600f));

			this.onclick(clientX, clientY);
		}

		ui.Update();
		r.Update();
	}
	
	@Override
	public void dispose() {
		r.Close();

		themeMusic.dispose();
		startSound.dispose();
		menuClickSound.dispose();
	}

	public void onkeypress(Input.Keys keys) {

	}

	public void onclick(int clientX, int clientY) {
		if (playBtn.IsInBound(clientX, clientY)) {
			ui.GetBoxModule("mode-selection-menu").SetVisibility(true);
			ui.GetBoxModule("start-menu").SetVisibility(false);

			mode1.SetVisibility(true);
			mode2.src.setColor(0.5f, 0.5f, 0.5f, 1);
			mode3.src.setColor(0.5f, 0.5f, 0.5f, 1);

			menuClickSound.play();
		}
		else if (creditBtn.IsInBound(clientX, clientY)) {
			menuClickSound.play();
		}
		else if (musicBtn.IsInBound(clientX, clientY)) {
			if (themeMusic.getVolume() == 0) {
				themeMusic.setVolume(0.7f);
				musicBtn.SetVisibility(true);
			}
			else {
				themeMusic.setVolume(0);
				musicBtn.src.setColor(0.5f, 0.5f, 0.5f, 1);
			}
		}
		else if (mode1.IsInBound(clientX, clientY)) {
			if (foe.difficulty == 1) return;

			mode1.SetVisibility(true);
			mode2.src.setColor(0.5f, 0.5f, 0.5f, 1);
			mode3.src.setColor(0.5f, 0.5f, 0.5f, 1);

			foe.difficulty = 1;
			menuClickSound.play();
		}
		else if (mode2.IsInBound(clientX, clientY)) {
			if (foe.difficulty == 2) return;

			mode1.src.setColor(0.5f, 0.5f, 0.5f, 1);
			mode2.SetVisibility(true);
			mode3.src.setColor(0.5f, 0.5f, 0.5f, 1);

			foe.difficulty = 2;
			menuClickSound.play();
		}
		else if (mode3.IsInBound(clientX, clientY)) {
			if (foe.difficulty == 3) return;

			mode1.src.setColor(0.5f, 0.5f, 0.5f, 1);
			mode2.src.setColor(0.5f, 0.5f, 0.5f, 1);
			mode3.SetVisibility(true);

			foe.difficulty = 3;
			menuClickSound.play();
		}
		else if (startBtn.IsInBound(clientX, clientY)) {
			ui.GetBoxModule("mode-selection-menu").SetVisibility(false);
			ui.GetBoxModule("in-game-menu").SetVisibility(true);

			startSound.play();
			GameStatus.isGameStarted = true;
		}
	}
}
