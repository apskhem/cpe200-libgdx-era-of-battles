package coma.game;

public class Intro {

    private static short soundPlay = 80;
    private static short soundTime = 200;
    private static short fadingTime = 100;
    private static final float MAX_FADING = 100;
    private static boolean isDone = false;

    public static void Play() {
        if (Intro.isDone) return;

        if (soundPlay == 0) {
            MainGame.devLogoSound.play();
            soundPlay -= 1;
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
        if (fadingTime == 0) {
            MainGame.devLogo.isVisible = false;
            MainGame.themeMusic.play();
            MainGame.ui.GetBoxModule("start-menu").SetVisibility(true);
            MainGame.musicBtn.isVisible = true;

            fadingTime -= 1;
            isDone = true;
        }
        else if (fadingTime > 0) {
            final float p = fadingTime / MAX_FADING;
            MainGame.devLogo.src.setColor(p, p, p, 1);
            fadingTime -= 1;
        }
    }
}
