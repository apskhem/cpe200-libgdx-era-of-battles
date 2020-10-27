package coma.game.event;

public abstract class UIEvent {

    private boolean isStopped = false;

    public boolean isPropagationStopped() {
        return this.isStopped;
    }

    public void stopPropagation() {
        this.isStopped = true;
    }
}
