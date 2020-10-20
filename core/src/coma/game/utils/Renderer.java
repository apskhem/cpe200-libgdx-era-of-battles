package coma.game.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import coma.game.components.*;
import coma.game.MainGame;

import java.util.ArrayList;

final public class Renderer {

    private static final SpriteBatch b = Asset.LoadSpriteBatch();
    private static final ArrayList<Image> renderObjects = new ArrayList<>();
    private static final ArrayList<Canvas> canvasObjects = new ArrayList<>();
    private static final ArrayList<TextBox> textObjects = new ArrayList<>();

    public static void AddComponents(final Object ...renderObjects) {
        for (final Object r : renderObjects) {
            if (r instanceof Canvas) {
                Renderer.canvasObjects.add((Canvas) r);
            }
            else if (r instanceof Image) {
                Renderer.renderObjects.add((Image) r);
            }
            else if (r instanceof TextBox) {
                Renderer.textObjects.add((TextBox) r);
            }
            else if (r instanceof ArrayList) {
                Renderer.AddComponents(((ArrayList<?>) r).toArray());
            }
        }
    }

    public static void RemoveComponents(final Image ...renderObjects) {
        for (final Image r : renderObjects) {
            Renderer.renderObjects.remove(r);
        }
    }

    public static void Update() {
        Renderer.b.begin();

        // camera
        if (MainGame.camera != null) {
            MainGame.camera.update();
            Renderer.b.setProjectionMatrix(MainGame.camera.combined);
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
}
