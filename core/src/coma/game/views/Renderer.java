package coma.game.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import coma.game.components.*;
import coma.game.MainGame;
import coma.game.utils.Asset;

import java.util.ArrayList;

final public class Renderer {

    private static final SpriteBatch b = Asset.loadSpriteBatch();
    private static final ArrayList<Image> renderObjects = new ArrayList<>();
    private static final ArrayList<Canvas> canvasObjects = new ArrayList<>();
    private static final ArrayList<TextBox> textObjects = new ArrayList<>();

    public static void addComponents(final Object ...renderObjects) {
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
                Renderer.addComponents(((ArrayList<?>) r).toArray());
            }
        }
    }

    public static void removeComponents(final Image ...renderObjects) {
        for (final Image r : renderObjects) {
            Renderer.renderObjects.remove(r);
        }
    }

    public static void update() {
        Renderer.b.begin();

        // camera
        if (MainGame.camera != null) {
            MainGame.camera.update();
            Renderer.b.setProjectionMatrix(MainGame.camera.combined);
        }

        // images and canvas
        for (Image renderObject : Renderer.renderObjects) {
            renderObject.render(Renderer.b);
        }

        for (Canvas canvas : Renderer.canvasObjects) {
            canvas.render(Renderer.b);
        }

        // textboxes
        for (TextBox textObject : Renderer.textObjects) {
            textObject.render(Renderer.b);
        }

        Renderer.b.end();
    }
}
