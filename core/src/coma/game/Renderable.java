package coma.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Renderable {
    public boolean isVisible = true;

    abstract void Render(SpriteBatch b);
}

class Image extends Renderable {

    protected final Texture texture;
    protected final Sprite src;
    public final int naturalWidth;
    public final int naturalHeight;
    public boolean isFlipped;

    public Image(final String internalPath) {
        this.texture = Asset.LoadTexture(internalPath);
        this.src = new Sprite(this.texture);
        this.naturalWidth = this.texture.getWidth();
        this.naturalHeight = this.texture.getHeight();
    }

    protected Image(final Image image) {
        this.texture = null;
        this.src = new Sprite(image.src);
        this.naturalWidth = this.src.getTexture().getWidth();
        this.naturalHeight = this.src.getTexture().getHeight();
        this.isFlipped = image.isFlipped;
    }

    public void FlipHorizontal() {
        this.src.setFlip(!this.src.isFlipX(), this.src.isFlipY());

        this.isFlipped = this.src.isFlipX() || this.src.isFlipY();
    }

    public void FlipVertical() {
        this.src.setFlip(this.src.isFlipX(), !this.src.isFlipY());

        this.isFlipped = this.src.isFlipX() || this.src.isFlipY();
    }

    public void Move(final float x, final float y) {
        this.src.translate(x, y);
    }

    public Vector2 GetTransform() {
        final Vector2 v = new Vector2();
        v.x = this.src.getX();
        v.y = this.src.getY();

        return v;
    }

    public void SetOpacity(final float value) {
        this.src.setColor(1,1,1, value);
    }

    public void SetSize(float width, float height) {
        if (Float.isNaN(width)) width = this.naturalWidth;
        if (Float.isNaN(height)) height = this.naturalHeight;

        this.src.setBounds(this.src.getX(), this.src.getY(), width, height);
    }

    public void SetPosition(final float x, final float y) {
        this.src.setPosition(x, y);
    }

    public void SetRotation(final float degree) {
        this.src.rotate(degree - this.src.getRotation());
    }

    public void SetTexture(final Image image) {
        this.src.setTexture(image.src.getTexture());
    }

    public void SetTexture(final Texture texture) {
        this.src.setTexture(texture);
    }

    public void Render(final SpriteBatch b) {
        if (this.isVisible) {
            this.src.draw(b);
        }
    }

    public Image Clone() {
        return  new Image(this);
    }
}

final class ImageRegion extends Image {

    public int regionWidth;
    public int regionHeight;
    public int currentIRegionIndex;
    public int currentJRegionIndex;
    public int maxIRegionIndex;
    public int maxJRegionIndex;
    public float tempTimer;

    public ImageRegion(final String internalPath, final int width, final int height, final int maxI, final int maxJ) {
        super(internalPath);

        this.regionWidth = width;
        this.regionHeight = height;
        this.maxIRegionIndex = maxI;
        this.maxJRegionIndex = maxJ;

        this.src.setRegion(0, 0, width, height);
        this.src.setBounds(0,0, width, height);
    }

    public ImageRegion(final Image image, final int width, final int height, final int maxI, final int maxJ) {
        super(image);

        this.regionWidth = width;
        this.regionHeight = height;
        this.maxIRegionIndex = maxI;
        this.maxJRegionIndex = maxJ;

        this.src.setRegion(0, 0, width, height);
        this.src.setBounds(0,0, width, height);
    }

    public void NextRegion() {
        if (this.currentIRegionIndex + 1 == this.maxIRegionIndex) {
            this.currentJRegionIndex = (this.currentJRegionIndex + 1) % this.maxJRegionIndex;
        }

        this.currentIRegionIndex = (this.currentIRegionIndex + 1) % this.maxIRegionIndex;

        this.src.setRegion(
                this.regionWidth * this.currentIRegionIndex,
                this.regionHeight * this.currentJRegionIndex,
                this.regionWidth,
                this.regionHeight);
    }

    public boolean IsAtTheEnd() {
        return this.currentIRegionIndex + 1 == this.maxIRegionIndex && this.currentJRegionIndex + 1 == this.maxJRegionIndex;
    }

    public ImageRegion Clone() {
        return  new ImageRegion(this, this.regionWidth, this.regionHeight, this.maxIRegionIndex, this.maxJRegionIndex);
    }
}

/**
 * Class for containing a UI element.
 */
final class Canvas extends Image {
    public float x;
    public float y;

    public final int VIEWPORT_WIDTH = 960;
    public final int VIEWPORT_HEIGHT = 600;

    public Canvas(final String internalPath) {
        super(internalPath);
    }

    public Canvas(final Image canvas) {
        super(canvas);
    }

    public void SetActive(final boolean value) {
        if (value) {
            this.src.setColor(1,1,1,1);
        }
        else {
            this.src.setColor(0.5f,0.5f,0.5f,1);
        }
    }

    public boolean IsInBound(final float x, final float y) {
        return x >= this.x && x <= this.x + this.src.getWidth() && y >= this.y && y <= this.y + this.src.getHeight() && this.isVisible;
    }

    public void SetScale(final float value) {
        if (value < 0) return;

        this.src.scale(value - 1);
    }

    public void SetViewBox(float width, float height) {
        width = Float.isNaN(width) ? this.naturalWidth : width;
        height = Float.isNaN(height) ? this.naturalHeight : height;

        this.src.setBounds(this.src.getX(), this.src.getY(), width, height);
    }

    @Override
    public void SetPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public void SetPosition(final String alignX, final float y) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }

        this.y = (int) y;
    }

    public void SetPosition(final float x, final String alignY) {
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }

        this.x = (int) x;
    }

    public void SetPosition(final String alignX, final String alignY) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }
    }
}

/**
 * Class for manipulating UI text.
 */
final class TextBox extends Renderable {

    public float x;
    public float y;
    public float translateX;
    public float translateY;

    private final BitmapFont bitmapFont;
    public String textContent = "";

    public TextBox(final BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;
    }

    public void SetPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public void Render(final SpriteBatch b) {
        if (this.isVisible) {
            this.bitmapFont.draw(b, this.textContent, this.x + translateX, this.y + translateY);
        }
    }

    public void Remove() {
        this.bitmapFont.dispose();
    }
}