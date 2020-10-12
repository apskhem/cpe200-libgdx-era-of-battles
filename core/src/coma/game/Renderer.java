package coma.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Renderer {

    private static final SpriteBatch b = new SpriteBatch();;
    private static final ArrayList<Texture> textureContainer = new ArrayList<>();
    private static final ArrayList<Image> renderObjects = new ArrayList<>();
    private static final ArrayList<Canvas> canvasObjects = new ArrayList<>();
    private static final ArrayList<TextBox> textObjects = new ArrayList<>();
    private static Camera camera;

    public static void AddImageTexture(Texture texture) {
        Renderer.textureContainer.add(texture);
    }

    public static void AddComponents(Object ...renderObjects) {
        for (final Object r : renderObjects) {
            if (r instanceof Camera) {
                Renderer.camera = (Camera) r;
            }
            else if (r instanceof Canvas) {
                Renderer.canvasObjects.add((Canvas) r);
            }
            else if (r instanceof Image) {
                Renderer.renderObjects.add((Image) r);
            }
            else if (r instanceof TextBox) {
                Renderer.textObjects.add((TextBox) r);
            }
            else if (r instanceof ArrayList) {
                Renderer.AddComponents(((ArrayList<?>) r).toArray(new Object[((ArrayList<?>) r).size()]));
            }
        }
    }

    public static void RemoveComponents(Image ...renderObjects) {
        for (final Image r : renderObjects) {
            Renderer.renderObjects.remove(r);
        }
    }

    public static void Update() {
        Renderer.b.begin();

        // camera
        if (Renderer.camera != null) {
            Renderer.camera.update();
            Renderer.b.setProjectionMatrix(Renderer.camera.combined);
        }

        // images and canvas
        for (Image renderObject : Renderer.renderObjects) {
            renderObject.Render(Renderer.b);
        }

        for (Canvas canvas : Renderer.canvasObjects) {
            canvas.Render(Renderer.b);
        }

        // textboxes
        for (TextBox textObject : Renderer.textObjects) {
            textObject.Render(Renderer.b);
        }

        Renderer.b.end();
    }

    public static void Close() {
        b.dispose();

        for (Texture texture : Renderer.textureContainer) {
            texture.dispose();
        }

        Renderer.textureContainer.clear();
    }
}
