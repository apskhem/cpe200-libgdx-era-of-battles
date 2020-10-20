package coma.game.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import coma.game.components.Canvas;
import coma.game.components.EventTarget;

import java.util.ArrayList;

public class EventHandlingManager {

    private static final ArrayList<EventTarget> eventTargets = new ArrayList<>();

    public static EventTarget global = new EventTarget();

    private static String previousKey;

    public static void Update() {
        // onclick
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            final int clientX = Gdx.input.getX();
            final int clientY = (int)(600 * (1 - Gdx.input.getY() / 600f));

            final MouseEvent e = new MouseEvent(clientX, clientY);

            // run all object on clicked position
            for (final EventTarget eventTarget : eventTargets) {
                if (eventTarget instanceof Canvas && ((Canvas) eventTarget).IsInBound(clientX, clientY)) {
                    // run all onclick function
                    for (final EventListener listener : eventTarget.listeners) {
                        if (listener.type.equals("onclick")) {
                            listener.fn.Call(e);
                        }
                    }

                    if (e.IsPropagationStopped()) break;
                }
            }

            // global
            if (!e.IsPropagationStopped()) {
                for (final EventListener listener : global.listeners) {
                    if (listener.type.equals("onclick")) {
                        listener.fn.Call(e);
                    }
                }
            }
        }

        // onkeypress
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            String code = "";
            for (int i = 0; i < 255; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    code = Input.Keys.toString(i);
                    break;
                }
            }

            if (!code.equals("")) previousKey = code;

            final KeyboardEvent e = new KeyboardEvent(code.equals("") ? previousKey : code);

            // global
            for (final EventListener listener : global.listeners) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                    if (listener.type.equals("onkeyjustpressed")) {
                        listener.fn.Call(e);
                    }
                }

                if (listener.type.equals("onkeypress")) {
                    listener.fn.Call(e);
                }
            }
        }
    }

    public static void AddEventTarget(final EventTarget eventTarget) {
        if (!eventTargets.contains(eventTarget)) {
            eventTargets.add(eventTarget);
        }
    }
}
