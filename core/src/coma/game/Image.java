package coma.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Image extends Renderable {

    public static Image strongholdEra1;
    public static Image meleeUnitEra1A;
    public static Image meleeUnitEra1B;
    public static Image meleeUnitEra1C;
    public static Image meleeUnitEra1D;
    public static Image meleeUnitEra1E;
    public static Image meleeUnitEra1F;
    public static Image meleeUnitEra1G;
    public static Image rangedUnitEra1A;
    public static Image rangedUnitEra1B;
    public static Image rangedUnitEra1C;
    public static Image rangedUnitEra1D;
    public static Image rangedUnitEra1E;
    public static Image rangedUnitEra1F;
    public static Image rangedUnitEra1G;
    public static Image unitHealthBar;
    public static Image unitHealthBarInner;

    private final Texture texture;
    public final Sprite src;
    public final int naturalWidth;
    public final int naturalHeight;

    public Image(String internalPath) {
        this.texture = new Texture(internalPath);
        this.src = new Sprite(this.texture);
        this.naturalWidth = this.texture.getWidth();
        this.naturalHeight = this.texture.getHeight();

        Renderer.AddImageTexture(this.texture);
    }

    public Image(Sprite sprite) {
        this.texture = null;
        this.src = new Sprite(sprite);
        this.naturalWidth = this.src.getTexture().getWidth();
        this.naturalHeight = this.src.getTexture().getHeight();
    }

    public void SetPosition(float x, float y) {
        this.src.setPosition(x, y);
    }

    public void Render(SpriteBatch b) {
        if (this.isVisible) {
            this.src.draw(b);
        }
    }

    public Image Clone() {
        return  new Image(this.src);
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

    public Canvas(Canvas canvas) {
        super(canvas.src);
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

    public void SetViewBox(float width, float height) {
        width = Float.isNaN(width) ? this.naturalWidth : width;
        height = Float.isNaN(height) ? this.naturalHeight : height;

        this.src.setBounds(this.src.getX(), this.src.getY(), width, height);
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
