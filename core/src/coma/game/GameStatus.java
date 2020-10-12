package coma.game;

import com.badlogic.gdx.audio.Sound;

public class GameStatus {

    public static boolean isGameStarted;

    public static Canvas victoryBanner;
    public static Canvas defeatBanner;
    public static Sound winSound;
    public static Sound loseSound;

    public static void CheckGameOver(Player playerL, Player playerR, UIController ui) {
        if (!GameStatus.isGameStarted) return;

        if (playerL.stronghold.health < 0 || playerR.stronghold.health < 0) {
            GameStatus.GameOver(ui, playerL.stronghold.health >= 0);

            playerL.ClearAllUnits();
            playerR.ClearAllUnits();
            Unit.ClearDeadUnitQueue();
        }
    }

    public static void GameOver(UIController ui, boolean isWon) {
        GameStatus.isGameStarted = false;

        ui.GetBoxModule("in-game-menu").SetVisibility(false);
        ui.GetBoxModule("game-over-menu").SetVisibility(true);

        if (isWon) GameStatus.winSound.play();
        else GameStatus.loseSound.play();

        GameStatus.victoryBanner.isVisible = isWon;
        GameStatus.defeatBanner.isVisible = !isWon;
    }
}
