package coma.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import coma.game.contents.*;
import coma.game.event.*;
import coma.game.utils.Asset;
import coma.game.utils.Renderer;
import coma.game.utils.UIController;

import java.text.DecimalFormat;

public class MainGame extends ApplicationAdapter {

	public static OrthographicCamera camera;

	public static float deltaTime;
	public static byte gameSpeed = 1;

	public static Player user;
	public static GameBot foe;

	// config
	private static final float CAMERA_SPEED = 10f;

	// debug
	private static final boolean devMode = true;
	
	@Override
	public void create() {
		// load global images
		Resources.Load();
		Resources.Setup();

		// set fields
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		user = new Player();
		foe = new GameBot();

		this.InitEventListener();

		// set camera
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);

		Renderer.AddComponents(Resources.bg, user.stronghold.image, foe.stronghold.image, UIController.GetComponents());
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set deltaTime
		deltaTime = Gdx.graphics.getDeltaTime() * 60.606f * gameSpeed;

		Intro.Play();

		// keyboard event
		if (GameStatus.isGameStarted) {
			// update user ui
			DecimalFormat df = new DecimalFormat("###,###,###");

			// text
			Resources.cashText.textContent = df.format(user.cash);
			Resources.xpText.textContent = df.format(user.xp);
			Resources.unitCapText.textContent = user.units.size() + "/" + Player.MAX_UNIT;

			// unit icons
			Resources.unit1.SetActive(user.cash >= new MeleeUnit(user.era, MeleeUnit.stats[user.era - 1]).cost);
			Resources.unit2.SetActive(user.cash >= new RangedUnit(user.era, RangedUnit.stats[user.era - 1]).cost);
			Resources.unit3.SetActive(user.cash >= new CavalryUnit(user.era, CavalryUnit.stats[user.era - 1]).cost);
			Resources.unit4.SetActive(user.BuildTurret(null));
			Resources.unit5.SetActive(user.era < 4 && user.xp >= Stronghold.GetRequiredXp(user.era));
			Resources.unitUl.SetActive(user.ultimateDelay <= 0);

			// delay bars
			final float cd = user.deploymentQueue.size > 0 ? user.deploymentQueue.first().GetDeploymentDelay() : 100;
			final float b = user.deploymentDelay / cd < 0 ? 0 : user.deploymentDelay / cd;

			Resources.unitQueueBarInner.SetViewBox((1 - b) * Resources.unitQueueBarInner.naturalWidth, Float.NaN);
			Resources.ultimateBarInner.SetViewBox((1 - user.ultimateDelay / (float) Player.ULTIMATE_LOADING_DELAY) * 198, Float.NaN);
			Resources.healthBarL.SetViewBox(user.stronghold.GetPercentageHealth(Resources.healthBarL.naturalWidth), Float.NaN);
			Resources.healthBarR.SetViewBox(foe.stronghold.GetPercentageHealth(Resources.healthBarR.naturalWidth), Float.NaN);

			Resources.unitUl.SetTexture(Resources.ultimateBannerImages[user.era - 1]);

			// queue ui
			for (byte i = 0; i < Resources.unitQueueIcons.length; i++) {
				if (i < user.deploymentQueue.size) {
					final Unit qu = user.deploymentQueue.get(i);
					Resources.unitQueueIcons[i].isVisible = true;

					if (qu instanceof MeleeUnit) Resources.unitQueueIcons[i].SetTexture(Resources.unitQueueImages[0]);
					if (qu instanceof RangedUnit) Resources.unitQueueIcons[i].SetTexture(Resources.unitQueueImages[1]);
					if (qu instanceof CavalryUnit) Resources.unitQueueIcons[i].SetTexture(Resources.unitQueueImages[2]);
				}
				else {
					Resources.unitQueueIcons[i].isVisible = false;
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

		// for events
		EventHandlingManager.Update();

		// update components
		UIController.Update();
		Renderer.Update();
	}
	
	@Override
	public void dispose() {
		Asset.Unload();
	}

	public void InitEventListener() {
		// onclick
		Resources.playBtn.AddEventListener("onclick", (MouseEvent e) -> {
			e.StopPropagation();

			UIController.GetBoxModule("mode-selection-menu").SetVisibility(true);
			UIController.GetBoxModule("start-menu").SetVisibility(false);

			Resources.mode1.SetOpacity(foe.difficulty == 1 ? 1 : 0.75f);
			Resources.mode2.SetOpacity(foe.difficulty == 2 ? 1 : 0.75f);
			Resources.mode3.SetOpacity(foe.difficulty == 3 ? 1 : 0.75f);

			Resources.menuClickSound.play();
		});

		Resources.creditBtn.AddEventListener("onclick", (MouseEvent e) -> {
			e.StopPropagation();

			UIController.GetBoxModule("start-menu").SetVisibility(false);
			Resources.creditBanner.isVisible = true;

			Resources.menuClickSound.play();
		});

		Resources.creditBanner.AddEventListener("onclick", (MouseEvent e) -> {
			UIController.GetBoxModule("start-menu").SetVisibility(true);
			Resources.creditBanner.isVisible = false;

			Resources.menuClickSound.play();
		});

		Resources.musicBtn.AddEventListener("onclick", (MouseEvent e) -> {
			final boolean t = Resources.themeMusic.getVolume() == 0;
			Resources.themeMusic.setVolume(t ? 0.7f : 0);
			Resources.musicBtn.SetActive(t);
		});

		Resources.mode1.AddEventListener("onclick", (MouseEvent e) -> {
			if (foe.difficulty == 1) return;

			Resources.mode1.SetOpacity(1);
			Resources.mode2.SetOpacity(0.75f);
			Resources.mode3.SetOpacity(0.75f);

			foe.difficulty = 1;
			Resources.menuClickSound.play();
		});

		Resources.mode2.AddEventListener("onclick", (MouseEvent e) -> {
			if (foe.difficulty == 2) return;

			Resources.mode1.SetOpacity(0.75f);
			Resources.mode2.SetOpacity(1);
			Resources.mode3.SetOpacity(0.75f);

			foe.difficulty = 2;
			Resources.menuClickSound.play();
		});

		Resources.mode3.AddEventListener("onclick", (MouseEvent e) -> {
			if (foe.difficulty == 3) return;

			Resources.mode1.SetOpacity(0.75f);
			Resources.mode2.SetOpacity(0.75f);
			Resources.mode3.SetOpacity(1);

			foe.difficulty = 3;
			Resources.menuClickSound.play();
		});

		Resources.startBtn.AddEventListener("onclick", (MouseEvent e) -> {
			UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);
			UIController.GetBoxModule("in-game-menu").SetVisibility(true);

			user.Setup();
			foe.Setup();
			GameStatus.isGameStarted = true;

			Resources.startSound.play();
		});

		Resources.restartBtn.AddEventListener("onclick", (MouseEvent e) -> {
			UIController.GetBoxModule("in-game-menu").SetVisibility(true);
			UIController.GetBoxModule("game-over-menu").SetVisibility(false);
			Resources.victoryBanner.isVisible = false;
			Resources.defeatBanner.isVisible = false;

			user.Setup();
			foe.Setup();
			GameStatus.isGameStarted = true;

			Resources.startSound.play();
		});

		Resources.menuBtn.AddEventListener("onclick", (MouseEvent e) -> {
			UIController.GetBoxModule("start-menu").SetVisibility(true);
			UIController.GetBoxModule("mode-selection-menu").SetVisibility(false);
			UIController.GetBoxModule("game-over-menu").SetVisibility(false);
			Resources.victoryBanner.isVisible = false;
			Resources.defeatBanner.isVisible = false;

			Resources.menuClickSound.play();
		});

		Resources.unit1.AddEventListener("onclick", (MouseEvent e) -> user.DeployUnit(new MeleeUnit(user.era, MeleeUnit.stats[user.era - 1])));
		Resources.unit2.AddEventListener("onclick", (MouseEvent e) -> user.DeployUnit(new RangedUnit(user.era, RangedUnit.stats[user.era - 1])));
		Resources.unit3.AddEventListener("onclick", (MouseEvent e) -> user.DeployUnit(new CavalryUnit(user.era, CavalryUnit.stats[user.era - 1])));
		Resources.unit4.AddEventListener("onclick", (MouseEvent e) -> user.BuildTurret(Turret.GetEra(user.era)));
		Resources.unit5.AddEventListener("onclick", (MouseEvent e) -> user.UpgradeStronghold());
		Resources.unitUl.AddEventListener("onclick", (MouseEvent e) -> user.UseUltimate());

		Resources.speedBtn.AddEventListener("onclick", (MouseEvent e) -> {
			final boolean t = gameSpeed == 1;
			gameSpeed = (byte) (t ? 2 : 1);
			Resources.speedBtn.SetActive(t);
		});

		// onkeydown
		EventHandlingManager.global.AddEventListener("onkeyjustpressed", (KeyboardEvent e) -> {
			if (e.code.equals("M")) Resources.musicBtn.Click();

			// menu
			if (UIController.GetBoxModule("start-menu").IsVisible()) {
				if (e.code.equals("Enter")) Resources.playBtn.Click();
				else if (e.code.equals("Tab")) Resources.creditBtn.Click();
			}
			else if (UIController.GetBoxModule("mode-selection-menu").IsVisible()) {
				switch (e.code) {
					case "Left": {
						switch (foe.difficulty) {
							case 2: Resources.mode1.Click(); break;
							case 3: Resources.mode2.Click(); break;
						}
					} break;
					case "Right": {
						switch (foe.difficulty) {
							case 1: Resources.mode2.Click(); break;
							case 2: Resources.mode3.Click(); break;
						}
					} break;
					case "Enter": {
						Resources.startBtn.Click();
					} break;
					case "Delete": {
						Resources.menuBtn.Click();
					} break;
				}
			}
			else if (UIController.GetBoxModule("game-over-menu").IsVisible()) {
				switch (e.code) {
					case "Enter": {
						Resources.restartBtn.Click();
					} break;
					case "Delete": {
						Resources.menuBtn.Click();
					} break;
				}
			}
			else if (Resources.creditBanner.isVisible) {
				if (e.code.equals("Delete")) Resources.creditBanner.Click();
			}

			// in-game
			if (GameStatus.isGameStarted) {
				switch (e.code) {
					case "Space": {
						camera.position.x = camera.viewportWidth/2;
					} break;
					case "1": Resources.unit1.Click(); break;
					case "2": Resources.unit2.Click(); break;
					case "3": Resources.unit3.Click(); break;
					case "4": Resources.unit4.Click(); break;
					case "5": Resources.unit5.Click(); break;
					case "6": Resources.unitUl.Click(); break;
					case "S": Resources.speedBtn.Click(); break;
				}

				if (devMode) {
					switch (e.code) {
						case "Q": foe.BuildTurret(Turret.GetEra(user.era)); break;
						case "W": user.cash += 1000; break;
						case "E": foe.cash += 1000; break;
						case "T": user.ultimateDelay = 0; break;
						case "Y": foe.ultimateDelay = 0; break;
						case "A": user.xp = Stronghold.GetRequiredXp(user.era < 4 ? MainGame.user.era : 3); break;
						case "Z": foe.xp = Stronghold.GetRequiredXp(foe.era < 4 ? MainGame.foe.era : 3); break;
					}
				}
			}
		});

		EventHandlingManager.global.AddEventListener("onkeypress", (KeyboardEvent e) -> {
			if (GameStatus.isGameStarted) {
				switch (e.code) {
					case "Left": {
						final float dm = CAMERA_SPEED * deltaTime / gameSpeed;
						if (camera.position.x - dm > camera.viewportWidth / 2) {

							camera.translate(-dm, 0);
						}
						else {
							camera.position.x = camera.viewportWidth / 2;
						}
					} break;
					case "Right": {
						final float dm = CAMERA_SPEED * deltaTime / gameSpeed;
						if (camera.position.x + dm < 2080 - camera.viewportWidth / 2) {
							camera.translate(dm, 0);
						}
						else {
							camera.position.x = 2080 - camera.viewportWidth / 2;
						}
					} break;
				}
			}
		});
	}
}