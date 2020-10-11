package coma.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.text.DecimalFormat;

public class MainGame extends ApplicationAdapter {

	// all game source will be here
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
	private Canvas xpIcon;
	private Canvas healthBar;
	private Canvas healthBarL;
	private Canvas healthBarR;

	private BitmapFont bitmapFont;
	private TextBox cashText;
	private TextBox xpText;

	private Player user;
	private Player foe;

	private Music themeMusic;
	private Sound startSound;
	private Sound menuClickSound;

	// config
	private final float CAMERA_SPEED = 10f;
	private final float NORMAL_MUSIC_VOLUME = 0.7f;
	
	@Override
	public void create() {
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
		xpIcon = new Canvas("xp-icon.png");
		healthBar = new Canvas("health-bar.png");
		healthBarL = new Canvas("health-bar-inner.png");
		healthBarR = new Canvas(healthBarL);
		bitmapFont = new BitmapFont(Gdx.files.internal("fonts/kefa.fnt"), false);
		cashText = new TextBox(bitmapFont);
		xpText = new TextBox(bitmapFont);
		ui = new UIController(camera);
		user = new Player(false);
		foe = new Player(true);

		// set ui position and group module
		playBtn.SetPosition("center", camera.viewportHeight/2);
		creditBtn.SetPosition("center", camera.viewportHeight/2 - 120);
		musicBtn.SetPosition(886, 14);
		mode1.SetPosition(80, "center");
		mode2.SetPosition("center", "center");
		mode3.SetPosition(640, "center");
		modeBanner.SetPosition("center", 440);
		startBtn.SetPosition("center", 80);
		startBtn.SetScale(0.8f);
		unit1.SetPosition(566, 524);
		unit2.SetPosition(646, 524);
		unit3.SetPosition(726, 524);
		unit4.SetPosition(806, 524);
		unit5.SetPosition(886, 524);
		unitUl.SetPosition(770, 430);
		cashIcon.SetPosition(-28, 504);
		cashIcon.SetScale(0.25f);
		xpIcon.SetPosition(-28, 466);
		xpIcon.SetScale(0.25f);
		healthBar.SetPosition("center", 14);
		healthBarL.SetPosition(318, 18);
		healthBarR.SetPosition(485, 18);
		healthBarR.src.rotate(180);

		cashText.SetPosition(64, 580);
		cashText.textContent = "0";
		xpText.SetPosition(64, 540);
		xpText.textContent = "0";

		ui.AddBoxModule("start-menu", playBtn, creditBtn);
		ui.AddBoxModule("mode-selection-menu", mode1, mode2, mode3, modeBanner, startBtn);
		ui.AddBoxModule("in-game-menu", unit1, unit2, unit3, unit4, unit5, unitUl,
				cashIcon, xpIcon, cashText, xpText, healthBar, healthBarL, healthBarR);
		ui.GetBoxModule("in-game-menu").SetVisibility(false);
		ui.GetBoxModule("mode-selection-menu").SetVisibility(false);

		// set sounds and music
		themeMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/theme.mp3"));
		themeMusic.play();
		themeMusic.setLooping(true);
		themeMusic.setVolume(NORMAL_MUSIC_VOLUME);

		startSound = Gdx.audio.newSound(Gdx.files.internal("audio/start-game.mp3"));
		menuClickSound = Gdx.audio.newSound(Gdx.files.internal("audio/menu-click.mp3"));
		Unit.meleeHit1 = Gdx.audio.newSound(Gdx.files.internal("audio/melee-hit-1.mp3"));
		Unit.rangedHit1 = Gdx.audio.newSound(Gdx.files.internal("audio/ranged-hit-1.mp3"));
		Unit.meleeDie1 = Gdx.audio.newSound(Gdx.files.internal("audio/melee-die-1.mp3"));
		Unit.unitCall = Gdx.audio.newSound(Gdx.files.internal("audio/call-unit.mp3"));

		Image.meleeUnitEra1A = new Image("melee-unit-era-1-1.png");
		Image.meleeUnitEra1B = new Image("melee-unit-era-1-2.png");
		Image.meleeUnitEra1C = new Image("melee-unit-era-1-3.png");
		Image.meleeUnitEra1D = new Image("melee-unit-era-1-4.png");
		Image.meleeUnitEra1E = new Image("melee-unit-era-1-5.png");
		Image.meleeUnitEra1F = new Image("melee-unit-era-1-6.png");
		Image.meleeUnitEra1G = new Image("melee-unit-era-1-7.png");
		Image.rangedUnitEra1A = new Image("ranged-unit-era-1-1.png");
		Image.rangedUnitEra1B = new Image("ranged-unit-era-1-2.png");
		Image.rangedUnitEra1C = new Image("ranged-unit-era-1-3.png");
		Image.rangedUnitEra1D = new Image("ranged-unit-era-1-4.png");
		Image.rangedUnitEra1E = new Image("ranged-unit-era-1-5.png");
		Image.rangedUnitEra1F = new Image("ranged-unit-era-1-6.png");
		Image.rangedUnitEra1G = new Image("ranged-unit-era-1-7.png");
		Image.unitHealthBar = new Image("unit-health-bar.png");
		Image.unitHealthBarInner = new Image("unit-health-bar-inner.png");

		// set camera
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);

		// set components
		ui.AddComponents(playBtn, creditBtn, musicBtn, mode1, mode2, mode3, modeBanner, startBtn,
				unit1, unit2, unit3, unit4, unit5, unitUl, cashIcon, xpIcon, healthBar, healthBarL, healthBarR);
		ui.AddComponents(cashText, xpText);
		Renderer.AddComponents(camera, bg, user.stronghold.image, foe.stronghold.image, ui.GetComponents());
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
					camera.translate(-CAMERA_SPEED , 0);
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				if (camera.position.x < 2080 - camera.viewportWidth / 2) {
					camera.translate(CAMERA_SPEED, 0);
				}
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				camera.position.x = camera.viewportWidth/2;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
				user.SpawnMeleeUnit();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
				user.SpawnRangedUnit();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
				user.SpawnCavalryUnit();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
				user.BuildTurret();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
				user.UpgradeStronghold();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
				user.UseUltimate();
			}

			// debugging
			if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
				foe.SpawnMeleeUnit();
			}

			// update ui
			DecimalFormat df = new DecimalFormat("###,###,###");

			cashText.textContent = df.format(user.cash);
			xpText.textContent = df.format(user.xp);

			healthBarL.src.setBounds(0,0, user.stronghold.GetPercentageHealth(healthBarL.naturalWidth), healthBarL.naturalHeight);
			healthBarR.src.setBounds(0,0, foe.stronghold.GetPercentageHealth(healthBarR.naturalWidth), healthBarR.naturalHeight);

			Unit.UpdateDeadUnits();

			foe.cash += 1;
			foe.Awake();
		}

		// click event
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			final int clientX = Gdx.input.getX();
			final int clientY = (int)(600 * (1 - Gdx.input.getY() / 600f));

			this.onclick(clientX, clientY);
		}

		// update user and foe
		Player.Update(user, foe);

		// update components
		ui.Update();
		Renderer.Update();
	}
	
	@Override
	public void dispose() {
		Renderer.Close();

		themeMusic.dispose();
		startSound.dispose();
		menuClickSound.dispose();
		Unit.meleeHit1.dispose();
		Unit.rangedHit1.dispose();
		Unit.meleeDie1.dispose();
		Unit.unitCall.dispose();

		bitmapFont.dispose();
	}

	public void onkeypress(Input.Keys keys) {

	}

	public void onclick(int clientX, int clientY) {
		if (playBtn.IsInBound(clientX, clientY)) {
			ui.GetBoxModule("mode-selection-menu").SetVisibility(true);
			ui.GetBoxModule("start-menu").SetVisibility(false);

			mode1.SetActive(true);
			mode2.SetActive(false);
			mode3.SetActive(false);

			menuClickSound.play();
		}
		else if (creditBtn.IsInBound(clientX, clientY)) {
			menuClickSound.play();
		}
		else if (musicBtn.IsInBound(clientX, clientY)) {
			if (themeMusic.getVolume() == 0) {
				themeMusic.setVolume(NORMAL_MUSIC_VOLUME);
				musicBtn.SetActive(true);
			}
			else {
				themeMusic.setVolume(0);
				musicBtn.SetActive(false);
			}
		}
		else if (mode1.IsInBound(clientX, clientY)) {
			if (foe.difficulty == 1) return;

			mode1.SetActive(true);
			mode2.SetActive(false);
			mode3.SetActive(false);

			foe.difficulty = 1;
			menuClickSound.play();
		}
		else if (mode2.IsInBound(clientX, clientY)) {
			if (foe.difficulty == 2) return;

			mode1.SetActive(false);
			mode2.SetActive(true);
			mode3.SetActive(false);

			foe.difficulty = 2;
			menuClickSound.play();
		}
		else if (mode3.IsInBound(clientX, clientY)) {
			if (foe.difficulty == 3) return;

			mode1.SetActive(false);
			mode2.SetActive(false);
			mode3.SetActive(true);

			foe.difficulty = 3;
			menuClickSound.play();
		}
		else if (startBtn.IsInBound(clientX, clientY)) {
			ui.GetBoxModule("mode-selection-menu").SetVisibility(false);
			ui.GetBoxModule("in-game-menu").SetVisibility(true);

			user.cash = 1000;
			foe.cash = 200;

			startSound.play();
			GameStatus.isGameStarted = true;
		}
		else if (unit1.IsInBound(clientX, clientY)) {
			user.SpawnMeleeUnit();
		}
		else if (unit2.IsInBound(clientX, clientY)) {
			user.SpawnRangedUnit();
		}
		else if (unit3.IsInBound(clientX, clientY)) {
			user.SpawnCavalryUnit();
		}
		else if (unit4.IsInBound(clientX, clientY)) {
			user.BuildTurret();
		}
		else if (unit5.IsInBound(clientX, clientY)) {
			user.UpgradeStronghold();
		}
		else if (unitUl.IsInBound(clientX, clientY)) {
			user.UseUltimate();
		}
	}
}
