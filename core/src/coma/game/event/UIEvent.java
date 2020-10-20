package coma.game.event;

public abstract class UIEvent {

    private boolean isStop = false;

    public boolean IsPropagationStopped() {
        return this.isStop;
    }

    public void StopPropagation() {
        this.isStop = true;
    }
}
