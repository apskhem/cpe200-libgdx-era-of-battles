package coma.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class RenderableObject {

    public RenderableObject() { }

    abstract void Render(SpriteBatch b);
    abstract void Remove();
}
