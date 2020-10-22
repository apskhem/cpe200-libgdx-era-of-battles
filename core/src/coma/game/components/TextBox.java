package coma.game.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import coma.game.controllers.UIController;

final public class TextBox extends Renderable {

    public float x;
    public float y;
    public float translateX;
    public float translateY;

    private final BitmapFont bitmapFont;
    public String textContent = "";

    public TextBox(final BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont.getCache().getFont();

        UIController.AddComponents(this);
    }

    public void SetOpacity(final float value) {
        this.bitmapFont.setColor(1,1,1, value);
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
}
