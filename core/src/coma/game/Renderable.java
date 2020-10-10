package coma.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Renderable {
    public boolean isVisible = true;

    abstract void Render(SpriteBatch b);
    abstract void Remove();
}
