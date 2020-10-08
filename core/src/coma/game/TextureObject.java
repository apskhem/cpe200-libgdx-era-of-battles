package coma.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextureObject extends RenderableObject {
    int x;
    int y;
    int translateX;
    int translateY;
    Texture texture;

    public TextureObject(String internalPahth) {
        super();
        this.texture = new Texture(internalPahth);
    }

    public void SetPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void Translate(int translateX, int translateY) {
        this.translateX = translateX;
        this.translateY = translateY;
    }

    public void Render(SpriteBatch b) {
        b.draw(this.texture, x - translateX, y - translateY);
    }

    public void Remove() {
        texture.dispose();
    }
}
