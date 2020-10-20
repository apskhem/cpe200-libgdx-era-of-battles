package coma.game.components;

import coma.game.event.*;

import java.util.ArrayList;

public class EventTarget {

    public final ArrayList<EventListener> listeners = new ArrayList<>();

    public <T extends UIEvent> void AddEventListener(String type, Listener<T> listener) {
        this.listeners.add(new EventListener<T>(type, listener));
        EventHandlingManager.AddEventTarget(this);
    }

    public void Click() {
        final MouseEvent e = new MouseEvent(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (final EventListener listener : this.listeners) {
            if (listener.type.equals("onclick")) {
                listener.fn.Call(e);
            }
        }
    }
}
