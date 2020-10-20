package coma.game.event;

public class EventListener<T extends UIEvent> {

    public final String type;
    public final Listener<T> fn;

    public EventListener(String type, Listener<T> listener) {
        this.type = type;
        this.fn = listener;
    }
}
