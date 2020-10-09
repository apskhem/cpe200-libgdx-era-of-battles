package coma.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Image {
    public Vector3 position = new Vector3();
    private final Texture t;
    public final Sprite src;

    public Image(String internalPath) {
        this.t = new Texture(internalPath);
        this.src = new Sprite(this.t);
    }

    public void Render(SpriteBatch b) {
        this.src.draw(b);
    }

    public void Remove() {
        this.t.dispose();
    }
}
