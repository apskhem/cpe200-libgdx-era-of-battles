package coma.game.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Renderable extends EventTarget {
    public boolean isVisible = true;
    public float tempTimer;

    abstract void Render(SpriteBatch b);
}
