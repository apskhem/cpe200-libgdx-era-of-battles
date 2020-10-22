package coma.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import coma.game.controllers.EventController;
import coma.game.controllers.GameStatus;
import coma.game.controllers.Intro;
import coma.game.models.GameBot;
import coma.game.models.Player;
import coma.game.models.contents.*;
import coma.game.event.*;
import coma.game.utils.Asset;
import coma.game.views.Renderer;
import coma.game.controllers.UIController;

import java.text.DecimalFormat;

public class MainGame extends ApplicationAdapter {

	public static OrthographicCamera camera;

	public static float deltaTime;
	public static byte gameSpeed = 1;

	public static Player user;
	public static GameBot foe;

	// config
	public static final float CAMERA_SPEED = 10f;
	public static final float THEME_VOLUME = 0.6f;
	public static final float AUDIO_VOLUME = 0;
	public static final DecimalFormat DF = new DecimalFormat("###,###,###");

	// debug
	public static final boolean devMode = true;
	
	@Override
	public void create() {
		// load global images
		Resources.Load();
		Resources.Setup();

		// set fields
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		user = new Player();
		foe = new GameBot();

		EventController.Init();

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
			// text
			Resources.cashText.textContent = DF.format(user.cash);
			Resources.xpText.textContent = DF.format(user.xp);
			Resources.unitCapText.textContent = user.units.size() + "/" + Player.MAX_UNIT;

			// unit icons
			Resources.unit1.SetActive(user.cash >= MeleeUnit.stats[user.era - 1][3]);
			Resources.unit2.SetActive(user.cash >= RangedUnit.stats[user.era - 1][3]);
			Resources.unit3.SetActive(user.cash >= CavalryUnit.stats[user.era - 1][3]);
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

		// unit desc font
		if (Resources.unitDescText.tempTimer < 0) {
			Resources.unitDescText.SetOpacity(0);
		}
		else {
			Resources.unitDescText.tempTimer -= deltaTime / gameSpeed;

			Resources.unitDescText.SetOpacity(Resources.unitDescText.tempTimer / 40);
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
}