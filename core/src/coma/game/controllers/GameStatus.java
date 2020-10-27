package coma.game.controllers;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.models.GameBot;
import coma.game.models.Player;
import coma.game.models.contents.Unit;

final public class GameStatus {

    public static boolean isGameStarted;

    public static void checkGameOver(final Player playerL, final GameBot playerR) {
        if (!GameStatus.isGameStarted) return;

        if (playerL.stronghold.health < 0 || playerR.stronghold.health < 0) {
            GameStatus.gameOver(playerL.stronghold.health >= 0);

            playerL.clearAllUnits();
            playerR.clearAllUnits();
            Unit.clearDeadUnitQueue();

            // clear ultimate
            if (playerL.emergencyUltimateCaller != null) playerL.emergencyUltimateCaller.removeImmidiate();
            if (playerR.emergencyUltimateCaller != null) playerR.emergencyUltimateCaller.removeImmidiate();

            playerR.halt();
        }
    }

    public static void gameOver(final boolean isWon) {
        GameStatus.isGameStarted = false;

        UIController.getBoxModule("in-game-menu").setVisibility(false);
        UIController.getBoxModule("game-over-menu").setVisibility(true);

        if (isWon) Resources.winSound.play();
        else Resources.loseSound.play();

        if (MainGame.gameSpeed != 1) {
            Resources.speedBtn.click();
        }

        Resources.victoryBanner.isVisible = isWon;
        Resources.defeatBanner.isVisible = !isWon;
    }
}
