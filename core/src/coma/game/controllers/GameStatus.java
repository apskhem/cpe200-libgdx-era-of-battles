package coma.game.controllers;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.models.GameBot;
import coma.game.models.Player;
import coma.game.models.contents.Unit;

final public class GameStatus {

    public static boolean isGameStarted;

    public static void CheckGameOver(final Player playerL, final GameBot playerR) {
        if (!GameStatus.isGameStarted) return;

        if (playerL.stronghold.health < 0 || playerR.stronghold.health < 0) {
            GameStatus.GameOver(playerL.stronghold.health >= 0);

            playerL.ClearAllUnits();
            playerR.ClearAllUnits();
            Unit.ClearDeadUnitQueue();

            playerR.Halt();
        }
    }

    public static void GameOver(final boolean isWon) {
        GameStatus.isGameStarted = false;

        UIController.GetBoxModule("in-game-menu").SetVisibility(false);
        UIController.GetBoxModule("game-over-menu").SetVisibility(true);

        if (isWon) Resources.winSound.play();
        else Resources.loseSound.play();

        if (MainGame.gameSpeed != 1) {
            Resources.speedBtn.Click();
        }

        Resources.victoryBanner.isVisible = isWon;
        Resources.defeatBanner.isVisible = !isWon;
    }
}
