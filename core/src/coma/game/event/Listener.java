package coma.game.event;

public interface Listener<T extends UIEvent> {

    void call(T e);

    default void callDefault(T e) { }
}
