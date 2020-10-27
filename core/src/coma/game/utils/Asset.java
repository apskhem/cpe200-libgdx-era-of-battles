package coma.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

final public class Asset {

    private static final ArrayList<Disposable> loadedAsset = new ArrayList<>();

    public static SpriteBatch loadSpriteBatch() {
        final SpriteBatch spriteBatch = new SpriteBatch();

        Asset.loadedAsset.add(spriteBatch);

        return spriteBatch;
    }

    public static Sound loadSound(final String internalPath) {
        final Sound sound = Gdx.audio.newSound(Gdx.files.internal(internalPath));

        Asset.loadedAsset.add(sound);

        return sound;
    }

    public static Music loadMusic(final String internalPath) {
        final Music music = Gdx.audio.newMusic(Gdx.files.internal(internalPath));

        Asset.loadedAsset.add(music);

        return music;
    }

    public static Texture loadTexture(final String internalPath) {
        final Texture texture = new Texture(internalPath);

        Asset.loadedAsset.add(texture);

        return texture;
    }

    public static BitmapFont loadBitmapFont(final String internalPath, final boolean flip) {
        final BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal(internalPath), flip);

        Asset.loadedAsset.add(bitmapFont);

        return bitmapFont;
    }

    public static void unload() {
        for (final Disposable item : Asset.loadedAsset) {
            item.dispose();
        }

        Asset.loadedAsset.clear();
    }
}
