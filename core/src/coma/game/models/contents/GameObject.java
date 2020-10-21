package coma.game.models.contents;

import coma.game.components.Image;

/**
 * Base class for all entities in game scenes.
 */
public abstract class GameObject {

    public final Image image;

    public GameObject(final Image image) {
        this.image = image;
    }
}

