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
	// all game sources will be here
	public static Image bg;
	public static OrthographicCamera camera;

	public static Canvas devLogo;
	public static Canvas gameLogo;
	public static Canvas creditBanner;
	public static Canvas playBtn;
	public static Canvas creditBtn;
	public static Canvas musicBtn;
	public static Canvas speedBtn;
	public static Canvas mode1;
	public static Canvas mode2;
	public static Canvas mode3;
	public static Canvas modeBanner;
	public static Canvas startBtn;
	public static Canvas menuBtn;
	public static Canvas restartBtn;
	public static Canvas unit1;
	public static Canvas unit2;
	public static Canvas unit3;
	public static Canvas unit4;
	public static Canvas unit5;
	public static Canvas unitUl;
	public static Canvas cashIcon;
	public static Canvas xpIcon;
	public static final Canvas[] unitQueueIcons = new Canvas[10];
	public static Canvas healthBar;
	public static Canvas healthBarL;
	public static Canvas healthBarR;
	public static Canvas queueBar;
	public static Canvas unitQueueBarInner;
	public static Canvas ultimateBarInner;
	public static Canvas victoryBanner;
	public static Canvas defeatBanner;

	public static BitmapFont bitmapFont;
	public static TextBox cashText;
	public static TextBox xpText;
	public static TextBox unitCapText;

	public static final Image[] strongholdImages = new Image[4];
	public static final Image[] turretImages = new Image[4];
	public static final Image[][] meleeUnitImages = new Image[4][7];
	public static final Image[][] rangedUnitImages = new Image[4][7];
	public static final Image[][] cavalryUnitImages = new Image[4][7];
	public static final Image[] unitQueueImages = new Image[3];
	public static final Image[] ultimateBannerImages = new Image[4];
	public static final Image[] ultimateImages = new Image[4];
	public static ImageRegion explosionImageRegion;
	public static Image ulPlane;
	public static Image unitHealthBar;
	public static Image unitHealthBarInner;

	public static Player user;
	public static GameBot foe;

	public static float deltaTime;
	public static byte gameSpeed = 1;

	public static Music themeMusic;
	public static Sound devLogoSound;
	public static Sound startSound;
	public static Sound menuClickSound;
	public static Sound newEraSound;
	public static Sound winSound;
	public static Sound loseSound;
	public static final Sound[] meleeHitSounds = new Sound[4];
	public static final Sound[] rangedHitSounds = new Sound[4];
	public static final Sound[] cavalryHitSounds = new Sound[4];
	public static final Sound[] explosionSounds = new Sound[1];
	public static Sound meleeDie1;
	public static Sound cavalryDie1;
	public static Sound unitCall;
	public static Sound ulPlaneSound;

	// config
	private static final float CAMERA_SPEED = 10f;
	private static final float MUSIC_VOLUME = 0.7f;

	// debug
	private static final boolean devMode = false;
	
	@Override
	public void create() {
		// load global images
		for (byte era = 0; era < 4; era++) {
			strongholdImages[era] = new Image("base-era-" + (era + 1) + ".png");
			turretImages[era] = new Image("turret-era-" + (era + 1) + ".png");
			ultimateBannerImages[era] = new Image("unit-ul-" + (era + 1) + ".png");
			ultimateImages[era] = new Image("ultimate-" + (era + 1) + ".png");
		}

		for (byte era = 0; era < 4; era++) {
			for (byte mov = 0; mov < 7; mov++) {
				meleeUnitImages[era][mov] = new Image("melee-unit-era-" + (era + 1) + "-" + (mov + 1) + ".png");
				rangedUnitImages[era][mov] = new Image("ranged-unit-era-" + (era + 1) + "-" + (mov + 1) + ".png");
				cavalryUnitImages[era][mov] = new Image("cavalry-unit-era-" + (era + 1) + "-" + (mov + 1) + ".png");
			}
		}

		for (byte t = 0; t < unitQueueImages.length; t++) {
			unitQueueImages[t] = new Image("unit-queue-" + (t + 1) + ".png");
		}

		explosionImageRegion = new ImageRegion("explosion-region.png", 128, 128, 4, 4);
		ulPlane = new Image("ul-plane.png");
		unitHealthBar = new Image("unit-health-bar.png");
		unitHealthBarInner = new Image("unit-health-bar-inner.png");

		// set fields
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bg = new Image("game-bg.png");
		devLogo = new Canvas("dev-logo.png");
		gameLogo = new Canvas("game-logo.png");
		creditBanner = new Canvas("credit.png");
		playBtn = new Canvas("play-btn.png");
		creditBtn = new Canvas("credit-btn.png");
		musicBtn = new Canvas("music-btn.png");
		speedBtn = new Canvas("speed-btn.png");
		mode1 = new Canvas("mode-1.png");
		mode2 = new Canvas("mode-2.png");
		mode3 = new Canvas("mode-3.png");
		modeBanner = new Canvas("mode-selection-banner.png");
		startBtn = new Canvas("start-btn.png");
		menuBtn = new Canvas("menu-btn.png");
		restartBtn = new Canvas("restart-btn.png");
		unit1 = new Canvas("unit-1.png");
		unit2 = new Canvas("unit-2.png");
		unit3 = new Canvas("unit-3.png");
		unit4 = new Canvas("unit-4.png");
		unit5 = new Canvas("unit-5.png");
		unitUl = new Canvas(ultimateBannerImages[0]);
		cashIcon = new Canvas("cash-icon.png");
		xpIcon = new Canvas("xp-icon.png");
		for (byte i = 0; i < unitQueueIcons.length; i++) {
			unitQueueIcons[i] = new Canvas(unitQueueImages[0].Clone());
		}
		healthBar = new Canvas("health-bar.png");
		healthBarL = new Canvas("health-bar-inner.png");
		healthBarR = new Canvas(healthBarL);
		queueBar = new Canvas("queue-bar.png");
		unitQueueBarInner = new Canvas("unit-queue-bar-inner.png");
		ultimateBarInner = new Canvas("ultimate-bar-inner.png");
		victoryBanner = new Canvas("victory.png");
		defeatBanner = new Canvas("defeat.png");
		bitmapFont = Asset.LoadBitmapFont("fonts/kefa.fnt", false);
		cashText = new TextBox(bitmapFont);
		xpText = new TextBox(bitmapFont);
		unitCapText = new TextBox(bitmapFont);
		user = new Player();
		foe = new GameBot();

		// set ui position and group module

		gameLogo.SetPosition("center", 400);
		playBtn.SetPosition("center", camera.viewportHeight/2);
		creditBtn.SetPosition("center", camera.viewportHeight/2 - 120);
		creditBanner.SetPosition("center", 5);
		creditBanner.isVisible = false;
		musicBtn.SetPosition(886, 14);
		musicBtn.isVisible = false;
		speedBtn.SetPosition(14, 14);
		speedBtn.SetActive(false);
		mode1.SetPosition(80, "center");
		mode2.SetPosition("center", "center");
		mode3.SetPosition(640, "center");
		modeBanner.SetPosition("center", 440);
		startBtn.SetPosition("center", 80);
		startBtn.x += -120;
		menuBtn.SetPosition("center", 80);
		menuBtn.x += 120;
		restartBtn.SetPosition("center", 80);
		restartBtn.x += -120;
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
		unitQueueIcons[0].SetPosition(566, 450);
		for (byte i = 0; i < unitQueueIcons.length; i++) {
			unitQueueIcons[i].SetPosition(566 + 20 * i, 462);
			unitQueueIcons[i].isVisible = false;
		}
		healthBar.SetPosition("center", 14);
		healthBarL.SetPosition(318, 18);
		healthBarR.SetPosition(485, 18);
		healthBarR.SetRotation(180);
		queueBar.SetPosition(566, 492);
		unitQueueBarInner.SetPosition(567, 500);
		ultimateBarInner.SetPosition(567,493);
		victoryBanner.SetPosition("center", 180);
		defeatBanner.SetPosition("center", 180);

		cashText.SetPosition(64, 580);
		cashText.textContent = "0";
		xpText.SetPosition(64, 540);
		xpText.textContent = "0";
		unitCapText.SetPosition(22, 500);
		unitCapText.textContent = "0/NaN";

		UIController.AddBoxModule("start-menu", gameLogo, playBtn, creditBtn);
		UIController.AddBoxModule("mode-selection-menu", mode1, mode2, mode3, modeBanner, startBtn, menuBtn);
		UIController.AddBoxModule("in-game-menu", speedBtn, unit1, unit2, unit3, unit4, unit5, unitUl,
				cashIcon, xpIcon, cashText, xpText, unitCapText, healthBar, healthBarL, healthBarR, queueBar,
				unitQueueBarInner, ultimateBarInner, unitQueueIcons[0], unitQueueIcons[1], unitQueueIcons[2],
				unitQueueIcons[3], unitQueueIcons[4], unitQueueIcons[5], unitQueueIcons[6], unitQueueIcons[7],
				unitQueueIcons[8], unitQueueIcons[9]);
		UIController.AddBoxModule("game-over-menu", restartBtn, menuBtn, victoryBanner, defeatBanner);
		UIController.GetBoxModule("start-menu").SetVisibility(false);
		UIController.GetBoxModule("game-over-menu").SetVisibility(false);
		UIController.GetBoxModule("in-game-menu").SetVisibility(false);
		UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);

		// set sounds and music
		themeMusic = Asset.LoadMusic("audio/theme.mp3");
		themeMusic.setLooping(true);
		themeMusic.setVolume(MUSIC_VOLUME);

		devLogoSound = Asset.LoadSound("audio/dev-logo.mp3");
		startSound = Asset.LoadSound("audio/start-game.mp3");
		menuClickSound = Asset.LoadSound("audio/menu-click.mp3");
		newEraSound = Asset.LoadSound("audio/new-era.mp3");
		winSound = Asset.LoadSound("audio/win.mp3");
		loseSound = Asset.LoadSound("audio/lose.mp3");
		ulPlaneSound = Asset.LoadSound("audio/ul-plane.mp3");

		for (byte era = 0; era < meleeHitSounds.length; era++) {
			meleeHitSounds[era] = Asset.LoadSound("audio/melee-hit-" + (era + 1) + ".mp3");
		}

		for (byte era = 0; era < rangedHitSounds.length; era++) {
			rangedHitSounds[era] = Asset.LoadSound("audio/ranged-hit-" + (era + 1) + ".mp3");
		}

		for (byte era = 0; era < cavalryHitSounds.length; era++) {
			cavalryHitSounds[era] = Asset.LoadSound("audio/cavalry-hit-" + (era + 1) + ".mp3");
		}

		explosionSounds[0] = Asset.LoadSound("audio/explosion-1.mp3");
		meleeDie1 = Asset.LoadSound("audio/melee-die-1.mp3");
		cavalryDie1 = Asset.LoadSound("audio/cavalry-die-1.mp3");
		unitCall = Asset.LoadSound("audio/call-unit.mp3");

		// set camera
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);

		// set components
		UIController.AddComponents(devLogo, gameLogo, playBtn, creditBtn, creditBanner, musicBtn, mode1, mode2, mode3, modeBanner, startBtn, restartBtn,
				menuBtn, speedBtn, unit1, unit2, unit3, unit4, unit5, unitUl, cashIcon, xpIcon, healthBar, healthBarL, healthBarR, queueBar,
				unitQueueBarInner, ultimateBarInner, victoryBanner, defeatBanner, unitQueueIcons[0], unitQueueIcons[1],
				unitQueueIcons[2], unitQueueIcons[3], unitQueueIcons[4], unitQueueIcons[5], unitQueueIcons[6],
				unitQueueIcons[7], unitQueueIcons[8], unitQueueIcons[9]);
		UIController.AddComponents(cashText, xpText, unitCapText);
		Renderer.AddComponents(bg, user.stronghold.image, foe.stronghold.image, UIController.GetComponents());
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set deltaTime
		deltaTime = (float) Gdx.graphics.getDeltaTime() * 60.606f * gameSpeed;

		Intro.Play();

		// keyboard event
		if (GameStatus.isGameStarted) {
			this.onkeypress();

			// update user ui
			DecimalFormat df = new DecimalFormat("###,###,###");

			// text
			cashText.textContent = df.format(user.cash);
			xpText.textContent = df.format(user.xp);
			unitCapText.textContent = user.units.size() + "/" + Player.MAX_UNIT;

			// unit icons
			unit1.SetActive(user.cash >= MeleeUnit.GetEra(user.era).cost);
			unit2.SetActive(user.cash >= RangedUnit.GetEra(user.era).cost);
			unit3.SetActive(user.cash >= CavalryUnit.GetEra(user.era).cost);
			unit4.SetActive(user.BuildTurret(null));
			unit5.SetActive(user.era < 4 && user.xp >= Stronghold.GetRequiredXp(user.era));
			unitUl.SetActive(user.ultimateDelay <= 0);

			// delay bars
			final float cd = user.deploymentQueue.size > 0 ? user.deploymentQueue.first().GetDeploymentDelay() : 100;
			final float b = user.deploymentDelay / cd < 0 ? 0 : user.deploymentDelay / cd;

			unitQueueBarInner.SetViewBox((1 - b) * unitQueueBarInner.naturalWidth, Float.NaN);
			ultimateBarInner.SetViewBox((1 - user.ultimateDelay / (float) Player.ULTIMATE_LOADING_DELAY) * 198, Float.NaN);
			healthBarL.SetViewBox(user.stronghold.GetPercentageHealth(healthBarL.naturalWidth), Float.NaN);
			healthBarR.SetViewBox(foe.stronghold.GetPercentageHealth(healthBarR.naturalWidth), Float.NaN);

			unitUl.SetTexture(ultimateBannerImages[user.era - 1]);

			// queue ui
			for (byte i = 0; i < unitQueueIcons.length; i++) {
				if (i < user.deploymentQueue.size) {
					final Unit qu = user.deploymentQueue.get(i);
					unitQueueIcons[i].isVisible = true;

					if (qu instanceof MeleeUnit) unitQueueIcons[i].SetTexture(unitQueueImages[0]);
					if (qu instanceof RangedUnit) unitQueueIcons[i].SetTexture(unitQueueImages[1]);
					if (qu instanceof CavalryUnit) unitQueueIcons[i].SetTexture(unitQueueImages[2]);
				}
				else {
					unitQueueIcons[i].isVisible = false;
				}
			}

			// update fading units
			Unit.UpdateDeadUnits();

			// update user and foe
			Player.Update(user, foe);

			// check game over
			GameStatus.CheckGameOver(user, foe);

			// update foe
			foe.Awake();
		}

		// click event
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			final int clientX = Gdx.input.getX();
			final int clientY = (int)(600 * (1 - Gdx.input.getY() / 600f));

			this.onclick(clientX, clientY);
		}

		// update components
		UIController.Update();
		Renderer.Update();
	}
	
	@Override
	public void dispose() {
		Asset.Unload();
	}

	public void onkeypress() {
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			final float dm = CAMERA_SPEED * deltaTime / gameSpeed;
			if (camera.position.x - dm > camera.viewportWidth / 2) {

				camera.translate(-dm, 0);
			}
			else {
				camera.position.x = camera.viewportWidth / 2;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			final float dm = CAMERA_SPEED * deltaTime / gameSpeed;
			if (camera.position.x + dm < 2080 - camera.viewportWidth / 2) {
				camera.translate(dm, 0);
			}
			else {
				camera.position.x = 2080 - camera.viewportWidth / 2;
			}
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			camera.position.x = camera.viewportWidth/2;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			user.DeployUnit(MeleeUnit.GetEra(user.era));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			user.DeployUnit(RangedUnit.GetEra(user.era));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
			user.DeployUnit(CavalryUnit.GetEra(user.era));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
			user.BuildTurret(Turret.GetEra(user.era));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
			user.UpgradeStronghold();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
			user.UseUltimate();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
			final boolean t = gameSpeed == 1;
			gameSpeed = (byte) (t ? 2 : 1);
			speedBtn.SetActive(t);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			final boolean t = themeMusic.getVolume() == 0;
			themeMusic.setVolume(t ? MUSIC_VOLUME : 0);
			musicBtn.SetActive(t);
		}

		// debugging
		if (devMode) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
				foe.BuildTurret(Turret.GetEra(user.era));
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
				user.cash += 1000;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
				foe.cash += 1000;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
				user.stronghold.image.SetTexture(strongholdImages[1]);
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
				user.ultimateDelay = 0;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
				foe.ultimateDelay = 0;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
				user.xp = Stronghold.GetRequiredXp(user.era < 4 ? this.user.era : 3);
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
				foe.xp = Stronghold.GetRequiredXp(foe.era < 4 ? this.foe.era : 3);
			}
		}
	}

	public void onclick(int clientX, int clientY) {
		if (playBtn.IsInBound(clientX, clientY)) {
			UIController.GetBoxModule("mode-selection-menu").SetVisibility(true);
			UIController.GetBoxModule("start-menu").SetVisibility(false);

			mode1.SetActive(foe.difficulty == 1);
			mode2.SetActive(foe.difficulty == 2);
			mode3.SetActive(foe.difficulty == 3);

			menuClickSound.play();
		}
		else if (creditBtn.IsInBound(clientX, clientY)) {
			UIController.GetBoxModule("start-menu").SetVisibility(false);
			creditBanner.isVisible = true;

			menuClickSound.play();
		}
		else if (creditBanner.IsInBound(clientX, clientY)) {
			UIController.GetBoxModule("start-menu").SetVisibility(true);
			creditBanner.isVisible = false;

			menuClickSound.play();
		}
		else if (musicBtn.IsInBound(clientX, clientY)) {
			final boolean t = themeMusic.getVolume() == 0;
			themeMusic.setVolume(t ? MUSIC_VOLUME : 0);
			musicBtn.SetActive(t);
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
			UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);
			UIController.GetBoxModule("in-game-menu").SetVisibility(true);

			user.Setup();
			foe.Setup();
			GameStatus.isGameStarted = true;

			startSound.play();
		}
		else if (menuBtn.IsInBound(clientX, clientY)) {
			UIController.GetBoxModule("start-menu").SetVisibility(true);
			UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);
			UIController.GetBoxModule("game-over-menu").SetVisibility(false);
			victoryBanner.isVisible = false;
			defeatBanner.isVisible = false;

			menuClickSound.play();
		}
		else if (restartBtn.IsInBound(clientX, clientY)) {
			UIController.GetBoxModule("in-game-menu").SetVisibility(true);
			UIController.GetBoxModule("game-over-menu").SetVisibility(false);
			victoryBanner.isVisible = false;
			defeatBanner.isVisible = false;

			user.Setup();
			foe.Setup();
			GameStatus.isGameStarted = true;

			startSound.play();
		}
		else if (unit1.IsInBound(clientX, clientY)) {
			user.DeployUnit(MeleeUnit.GetEra(user.era));
		}
		else if (unit2.IsInBound(clientX, clientY)) {
			user.DeployUnit(RangedUnit.GetEra(user.era));
		}
		else if (unit3.IsInBound(clientX, clientY)) {
			user.DeployUnit(CavalryUnit.GetEra(user.era));
		}
		else if (unit4.IsInBound(clientX, clientY)) {
			user.BuildTurret(Turret.GetEra(user.era));
		}
		else if (unit5.IsInBound(clientX, clientY)) {
			user.UpgradeStronghold();
		}
		else if (unitUl.IsInBound(clientX, clientY)) {
			user.UseUltimate();
		}
		else if (speedBtn.IsInBound(clientX, clientY)) {
			final boolean t = gameSpeed == 1;
			gameSpeed = (byte) (t ? 2 : 1);
			speedBtn.SetActive(t);
		}
	}
}