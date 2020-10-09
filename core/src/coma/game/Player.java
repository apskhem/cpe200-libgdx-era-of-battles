package coma.game;

public class Player {
    public final Image base;

    public int cash;
    public int xp;
    public byte era;

    public Player(boolean isBot) {
        this.base = new Image("base-era-1.png");

        if (isBot) {
            this.base.src.flip(true, false);
            this.base.src.setPosition(1760, 0);
        }
        else {
            this.base.src.setPosition(-80, 0);
        }
    }
}
