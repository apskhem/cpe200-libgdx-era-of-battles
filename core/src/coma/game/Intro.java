package coma.game;

public class Intro {

    private static float soundPlayDelay = 80;
    private static float soundPlaying = 200;
    private static float fadingTime = 100;
    private static final float MAX_FADING = 100;
    private static boolean isSoundPlayed = false;
    private static boolean isDone = false;

    public static void Play() {
        if (Intro.isDone) return;

        if (soundPlayDelay <= 0 && !isSoundPlayed) {
            MainGame.devLogoSound.play();
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
            MainGame.devLogo.isVisible = false;
            MainGame.themeMusic.play();
            MainGame.ui.GetBoxModule("start-menu").SetVisibility(true);
            MainGame.musicBtn.isVisible = true;

            fadingTime -= MainGame.deltaTime;
            isDone = true;
        }
        else if (fadingTime > 0) {
            final float p = fadingTime / MAX_FADING;
            MainGame.devLogo.src.setColor(p, p, p, 1);
            fadingTime -= MainGame.deltaTime;
        }
    }
}
