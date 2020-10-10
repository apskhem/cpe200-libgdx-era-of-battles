package coma.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextBox extends Renderable {

    public float x;
    public float y;
    public float translateX;
    public float translateY;

    private final BitmapFont bitmapFont;
    public String textContent = "";

    public TextBox(BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;
    }

    public void SetPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void Render(SpriteBatch b) {
        if (this.isVisible) {
            this.bitmapFont.draw(b, this.textContent, this.x + translateX, this.y + translateY);
        }
    }

    public void Remove() {
        this.bitmapFont.dispose();
    }
}
