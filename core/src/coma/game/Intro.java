package coma.game;

public class Intro {

    private static float soundPlay = 80;
    private static float soundTime = 200;
    private static float fadingTime = 100;
    private static final float MAX_FADING = 100;
    private static boolean isDone = false;

    public static void Play() {
        if (Intro.isDone) return;

        if (soundPlay == 0) {
            MainGame.devLogoSound.play();
            soundPlay -= MainGame.deltaTime;
        }
        else if (soundPlay > 0) {
            soundPlay -= 1;
            return;
        }

        // sound playing phase
        if (soundTime >= 0) {
            soundTime -= 1;
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
