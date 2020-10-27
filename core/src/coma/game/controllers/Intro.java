package coma.game.controllers;

import coma.game.MainGame;
import coma.game.Resources;

final public class Intro {

    private static float soundPlayDelay = 80;
    private static float soundPlaying = 200;
    private static float fadingTime = 100;
    private static boolean isSoundPlayed = false;
    private static boolean isDone = false;

    private static final float MAX_FADING = 100;

    public static void play() {
        if (Intro.isDone) return;

        if (soundPlayDelay <= 0 && !isSoundPlayed) {
            AudioController.playAndSetVolume(Resources.devLogoSound, MainGame.AUDIO_VOLUME);
            soundPlayDelay -= MainGame.deltaTime;
            isSoundPlayed = true;
        }
        else if (soundPlayDelay > 0) {
            soundPlayDelay -= MainGame.deltaTime;
            return;
        }

        // sound playing phase
        if (soundPlaying >= 0) {
            soundPlaying -= MainGame.deltaTime;
            return;
        }

        // fading phase
        if (fadingTime <= 0) {
            Resources.devLogo.isVisible = false;
            Resources.themeMusic.play();
            UIController.getBoxModule("start-menu").setVisibility(true);
            Resources.musicBtn.isVisible = true;

            fadingTime -= MainGame.deltaTime;
            isDone = true;
        }
        else if (fadingTime > 0) {
            final float p = fadingTime / MAX_FADING;
            Resources.devLogo.setColorRgba(p, p, p, 1);
            fadingTime -= MainGame.deltaTime;
        }
    }
}
