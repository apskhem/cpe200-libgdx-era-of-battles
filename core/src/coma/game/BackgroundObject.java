package coma.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackgroundObject extends RenderableObject {
    int translateX;
    private final Texture bg;
    private final int width;
    private final int height;

    public BackgroundObject(String internalPath) {
        super();
        this.bg = new Texture(internalPath);
        this.width = bg.getWidth();
        this.height = bg.getHeight();
    }

    public void SetTranslateX(int x) {
        if (x < 0) x = 0;
        else if (x > this.width - 960) x = this.width - 960;

        this.translateX = x;
    }

    public void Render(SpriteBatch b) {
        b.draw(this.bg, -translateX, 0);
    }

    public void Remove() {
        bg.dispose();
    }
}
