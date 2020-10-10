package coma.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Image extends Renderable {

    public static Image meleeUnit;

    public Vector3 position = new Vector3();
    private final Texture t;
    public final Sprite src;

    public Image(String internalPath) {
        this.t = new Texture(internalPath);
        this.src = new Sprite(this.t);
    }

    public Image(Sprite sprite) {
        this.t = null;
        this.src = new Sprite(sprite);
    }

    public void SetPosition(float x, float y) {
        this.src.setPosition(x, y);
    }

    public void Render(SpriteBatch b) {
        if (this.isVisible) {
            this.src.draw(b);
        }
    }

    public void Remove() {
        if (this.t != null) {
            this.t.dispose();
        }
    }

    public static Image GetMeleeUnitImage() {
        return new Image(Image.meleeUnit.src);
    }

    public static void StaticDispose() {
        Image.meleeUnit.t.dispose();
    }
}

/**
 * Class for contaning a UI element.
 */
class Canvas extends Image {
    public int x;
    public int y;

    public final int VIEWPORT_WIDTH = 960;
    public final int VIEWPORT_HEIGHT = 600;

    public Canvas(String internalPath) {
        super(internalPath);
    }

    public void SetActive(boolean value) {
        if (value) {
            this.src.setColor(1,1,1,1);
        }
        else {
            this.src.setColor(0.5f,0.5f,0.5f,1);
        }
    }

    public boolean IsInBound(float x, float y) {
        return x >= this.x && x <= this.x + this.src.getWidth() && y >= this.y && y <= this.y + this.src.getHeight() && this.isVisible;
    }

    public void SetScale(float value) {
        if (value < 0) return;

        this.src.scale(value - 1);
    }

    @Override
    public void SetPosition(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    public void SetPosition(String alignX, float y) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }

        this.y = (int) y;
    }

    public void SetPosition(float x, String alignY) {
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }

        this.x = (int) x;
    }

    public void SetPosition(String alignX, String alignY) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }
    }
}
