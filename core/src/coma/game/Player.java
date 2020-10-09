package coma.game;

public class Player {
    public final Image stronghold;

    public int cash;
    public int xp;
    public byte era = 1;
    public byte difficulty = 1;

    public Player(boolean isBot) {
        this.stronghold = new Image("base-era-1.png");

        if (isBot) {
            this.stronghold.src.flip(true, false);
            this.stronghold.src.setPosition(1760, 0);
        }
        else {
            this.stronghold.src.setPosition(-80, 0);
        }
    }
}
