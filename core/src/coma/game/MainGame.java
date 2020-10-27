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
	public static final float AUDIO_VOLUME = 1;
	public static final DecimalFormat DF = new DecimalFormat("###,###,###");

	// debug
	public static final boolean devMode = true;
	
	@Override
	public void create() {
		// load global images
		Resources.load();
		Resources.setup();

		// set fields
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		user = new Player();
		foe = new GameBot();

		EventController.init();

		// set camera
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);

		Renderer.addComponents(Resources.bg, user.stronghold.image, foe.stronghold.image, UIController.getComponents());
	}

	@Override
	public void render() {
		// background color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// set deltaTime
		deltaTime = Gdx.graphics.getDeltaTime() * 60.606f * gameSpeed;

		Intro.play();

		// keyboard event
		if (GameStatus.isGameStarted) {
			// text
			Resources.cashText.textContent = DF.format(user.cash);
			Resources.xpText.textContent = DF.format(user.xp);
			Resources.unitCapText.textContent = user.units.size() + "/" + Player.MAX_UNIT;

			// unit icons
			Resources.unit1.setActive(user.cash >= MeleeUnit.stats[user.era - 1][3]);
			Resources.unit2.setActive(user.cash >= RangedUnit.stats[user.era - 1][3]);
			Resources.unit3.setActive(user.cash >= CavalryUnit.stats[user.era - 1][3]);
			Resources.unit4.setActive(user.buildTurret(null));
			Resources.unit5.setActive(
					(user.xp >= (user.era < 4 ? Stronghold.getRequiredXp(user.era) : EmergencyUltimate.REQUIRED_XP))
					&& user.emergencyUltimateCaller == null);
			Resources.unitUl.setActive(user.ultimateDelay <= 0);

			// textbox
			Resources.unitTexts[0].textContent = DF.format(MeleeUnit.stats[user.era - 1][3]) + " g";
			Resources.unitTexts[1].textContent = DF.format(RangedUnit.stats[user.era - 1][3]) + " g";
			Resources.unitTexts[2].textContent = DF.format(CavalryUnit.stats[user.era - 1][3]) + " g";
			Resources.unitTexts[3].textContent = DF.format(Turret.getEra(user.era).cost) + " g";
			Resources.unitTexts[4].textContent = (user.era >= 4
					? DF.format(EmergencyUltimate.REQUIRED_XP)
					: DF.format(Stronghold.getRequiredXp(user.era))) + " xp";

			// delay bars
			final float cd = user.deploymentQueue.size > 0 ? user.deploymentQueue.first().getDeploymentDelay() : 100;
			final float b = user.deploymentDelay / cd < 0 ? 0 : user.deploymentDelay / cd;

			Resources.unitQueueBarInner.setViewBox((1 - b) * Resources.unitQueueBarInner.naturalWidth, Float.NaN);
			Resources.ultimateBarInner.setViewBox((1 - user.ultimateDelay / (float) Player.ULTIMATE_LOADING_DELAY) * 198, Float.NaN);
			Resources.healthBarL.setViewBox(user.stronghold.getPercentageHealth(Resources.healthBarL.naturalWidth), Float.NaN);
			Resources.healthBarR.setViewBox(foe.stronghold.getPercentageHealth(Resources.healthBarR.naturalWidth), Float.NaN);

			// set texture
			Resources.unit5.setTexture(user.era >= 4 ? Resources.unit5UlImage : Resources.unit5EraImage);
			Resources.unitUl.setTexture(Resources.ultimateBannerImages[user.era - 1]);

			// queue ui
			for (byte i = 0; i < Resources.unitQueueIcons.length; i++) {
				if (i < user.deploymentQueue.size) {
					final Unit qu = user.deploymentQueue.get(i);
					Resources.unitQueueIcons[i].isVisible = true;

					if (qu instanceof MeleeUnit) Resources.unitQueueIcons[i].setTexture(Resources.unitQueueImages[0]);
					if (qu instanceof RangedUnit) Resources.unitQueueIcons[i].setTexture(Resources.unitQueueImages[1]);
					if (qu instanceof CavalryUnit) Resources.unitQueueIcons[i].setTexture(Resources.unitQueueImages[2]);
				}
				else {
					Resources.unitQueueIcons[i].isVisible = false;
				}
			}

			// update fading units
			Unit.updateDeadUnits();

			// update user and foe
			Player.update(user, foe);

			// check game over
			GameStatus.checkGameOver(user, foe);

			// update foe
			foe.awake();
		}

		// unit desc font
		if (Resources.unitDescText.tempTimer < 0) {
			Resources.unitDescText.setOpacity(0);
		}
		else {
			Resources.unitDescText.tempTimer -= deltaTime / gameSpeed;

			Resources.unitDescText.setOpacity(Resources.unitDescText.tempTimer / 40);
		}

		// for events
		EventHandlingManager.update();

		// update components
		UIController.update();
		Renderer.update();
	}
	
	@Override
	public void dispose() {
		Asset.unload();
	}
}