package coma.game.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import coma.game.utils.Asset;

public class Image extends Renderable {

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

    public void SetColorRGBA(final float r, final float g, final float b, final float a) {
        this.src.setColor(r, g, b, a);
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
