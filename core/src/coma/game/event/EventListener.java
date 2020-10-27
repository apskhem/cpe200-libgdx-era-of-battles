package coma.game.event;

public class EventListener<T extends UIEvent> {

    public final String type;
    private final Listener<T> listenerFn;

    public EventListener(String type, Listener<T> listener) {
        this.type = type;
        this.listenerFn = listener;
    }

    public void call(final T e) {
        this.listenerFn.call(e);
    }
}
