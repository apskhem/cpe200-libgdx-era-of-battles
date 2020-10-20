package coma.game.event;

public interface Listener<T extends UIEvent> {

    void Call(T e);

    default void CallDefault(T e) {

    }
}
