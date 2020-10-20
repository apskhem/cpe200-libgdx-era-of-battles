package coma.game.event;

public class MouseEvent extends UIEvent {

    final int clientX;
    final int clientY;

    public MouseEvent(final int clientX, final int clientY) {
        this.clientX = clientX;
        this.clientY = clientY;
    }
}
