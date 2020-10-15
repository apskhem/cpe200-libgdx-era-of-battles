package coma.game;

public class GameStatus {

    public static boolean isGameStarted;

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

        if (isWon) MainGame.winSound.play();
        else MainGame.loseSound.play();

        MainGame.gameSpeed = 1;
        MainGame.speedBtn.SetActive(false);

        MainGame.victoryBanner.isVisible = isWon;
        MainGame.defeatBanner.isVisible = !isWon;
    }
}
