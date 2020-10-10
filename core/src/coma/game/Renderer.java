package coma.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Renderer {

    private final SpriteBatch b;
    private final ArrayList<Image> renderObjects = new ArrayList();
    private final ArrayList<TextBox> textObjects = new ArrayList();
    private int arrSize;
    private Camera camera;

    public Renderer() {
        b = new SpriteBatch();
    }

    public void AddComponents(Object ...renderObjects) {
        for (final Object r : renderObjects) {
            if (r instanceof Camera) {
                this.camera = (Camera) r;
            }
            else if (r instanceof Image) {
                this.renderObjects.add((Image) r);
                this.arrSize++;
            }
            else if (r instanceof TextBox) {
                this.textObjects.add((TextBox) r);
            }
        }
    }

    public void RemoveComponents(Image ...renderObjects) {
        for (final Image r : renderObjects) {
            if (this.renderObjects.remove(r)) arrSize--;
        }
    }

    public void Update() {
        this.b.begin();

        // camera
        if (this.camera != null) {
            this.camera.update();
            this.b.setProjectionMatrix(this.camera.combined);
        }

        // textures and sprites
        for (int i = 0; i < this.arrSize; i++) {
            this.renderObjects.get(i).Render(this.b);
        }

        // textboxes
        for (int i = 0; i < this.textObjects.size(); i++) {
            this.textObjects.get(i).Render(this.b);
        }

        this.b.end();
    }

    public void Close() {
        b.dispose();

        for (int i = 0; i < this.arrSize; i++) {
            this.renderObjects.get(i).Remove();
        }

        Image.StaticDispose();
    }
}
