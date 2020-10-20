package coma.game.event;

public class KeyboardEvent extends UIEvent {
    public final String code;

    public KeyboardEvent(final String code) {
        this.code = code;
    }
}
