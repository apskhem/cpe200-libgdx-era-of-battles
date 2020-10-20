package coma.game.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import coma.game.utils.UIController;

final public class TextBox extends Renderable {

    public float x;
    public float y;
    public float translateX;
    public float translateY;

    private final BitmapFont bitmapFont;
    public String textContent = "";

    public TextBox(final BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;

        UIController.AddComponents(this);
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
