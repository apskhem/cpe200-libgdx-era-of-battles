package coma.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Hashtable;

public class Renderer {

    final SpriteBatch b;
    final Hashtable<Integer, RenderableObject> renderObjects = new Hashtable();
    int hashMaxIndex;

    public Renderer() {
        b = new SpriteBatch();
    }

    public void AddRenderableObjects(RenderableObject ...renderObjects) {
        for (final RenderableObject r : renderObjects) {
            this.renderObjects.put(this.hashMaxIndex, r);
            this.hashMaxIndex++;
        }
    }

    public void Update() {
        b.begin();
        for (int i = 0; i < this.hashMaxIndex; i++) {
            this.renderObjects.get(i).Render(b);
        }
        b.end();
    }

    public void Close() {
        b.dispose();

        for (int i = 0; i < this.hashMaxIndex; i++) {
            this.renderObjects.get(i).Remove();
        }
    }
}
