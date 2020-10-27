package coma.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import coma.game.utils.Asset;

public class Image extends Renderable implements Cloneable {

    protected final Texture texture;
    protected final Sprite src;
    public final int naturalWidth;
    public final int naturalHeight;
    public boolean isFlipped;

    public Image(final String internalPath) {
        this.texture = Asset.loadTexture(internalPath);
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

    public void flipHorizontal() {
        this.src.setFlip(!this.src.isFlipX(), this.src.isFlipY());

        this.isFlipped = this.src.isFlipX() || this.src.isFlipY();
    }

    public void flipVertical() {
        this.src.setFlip(this.src.isFlipX(), !this.src.isFlipY());

        this.isFlipped = this.src.isFlipX() || this.src.isFlipY();
    }

    public void move(final float x, final float y) {
        this.src.translate(x, y);
    }

    public Vector2 getTransform() {
        final Vector2 v = new Vector2();
        v.x = this.src.getX();
        v.y = this.src.getY();

        return v;
    }

    public void setColorRgba(final float r, final float g, final float b, final float a) {
        this.src.setColor(r, g, b, a);
    }

    public void setOpacity(final float value) {
        this.src.setColor(1,1,1, value);
    }

    public void setSize(float width, float height) {
        if (Float.isNaN(width)) width = this.naturalWidth;
        if (Float.isNaN(height)) height = this.naturalHeight;

        this.src.setBounds(this.src.getX(), this.src.getY(), width, height);
    }

    public void setPosition(final float x, final float y) {
        this.src.setPosition(x, y);
    }

    public void setRotation(final float degree) {
        this.src.rotate(degree - this.src.getRotation());
    }

    public void setTexture(final Image image) {
        this.src.setTexture(image.src.getTexture());
    }

    public void setTexture(final Texture texture) {
        this.src.setTexture(texture);
    }

    public void render(final SpriteBatch b) {
        if (this.isVisible) {
            this.src.draw(b);
        }
    }

    public Image clone() {
        return new Image(this);
    }
}
