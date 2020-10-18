package coma.game;

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

        if (isWon) MainGame.winSound.play();
        else MainGame.loseSound.play();

        MainGame.gameSpeed = 1;
        MainGame.speedBtn.SetActive(false);

        MainGame.victoryBanner.isVisible = isWon;
        MainGame.defeatBanner.isVisible = !isWon;
    }
}
