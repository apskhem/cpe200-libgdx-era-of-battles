package coma.game.components;

import coma.game.event.*;

import java.util.ArrayList;

public class EventTarget {

    public final ArrayList<EventListener> listeners = new ArrayList<>();

    public <T extends UIEvent> void addEventListener(String type, Listener<T> listener) {
        this.listeners.add(new EventListener<T>(type, listener));
        EventHandlingManager.addEventTarget(this);
    }

    public void click() {
        final MouseEvent e = new MouseEvent(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (final EventListener listener : this.listeners) {
            if (listener.type.equals("click")) {
                listener.call(e);
            }
        }
    }
}
