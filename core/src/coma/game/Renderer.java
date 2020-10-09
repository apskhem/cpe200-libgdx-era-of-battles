package coma.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Hashtable;

public class Renderer {

    private final SpriteBatch b;
    private final Hashtable<Integer, Image> renderObjects = new Hashtable();
    private int hashMaxIndex;
    private Camera camera;

    public Renderer() {
        b = new SpriteBatch();
    }

    public void AddComponents(Image ...renderObjects) {
        for (final Image r : renderObjects) {
            this.renderObjects.put(this.hashMaxIndex, r);
            this.hashMaxIndex++;
        }
    }

    public void AddCamera(Camera camera) {
        this.camera = camera;
    }

    public void Update() {
        this.b.begin();

        if (this.camera != null) {
            this.camera.update();
            this.b.setProjectionMatrix(this.camera.combined);
        }

        for (int i = 0; i < this.hashMaxIndex; i++) {
            this.renderObjects.get(i).Render(this.b);
        }

        this.b.end();
    }

    public void Close() {
        b.dispose();

        for (int i = 0; i < this.hashMaxIndex; i++) {
            this.renderObjects.get(i).Remove();
        }
    }
}
