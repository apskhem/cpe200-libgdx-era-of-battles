package coma.game.models.contents;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.controllers.AudioController;

import java.util.ArrayList;

public class UltimateObj extends GameObject {

    private final byte era;
    private final float moveSpeedX;
    private final float moveSpeedY;
    private final float damage;
    private float spawnDelay;

    public UltimateObj(final byte era, final float spawnDelay, final float damage, final float moveSpeedX, final float moveSpeedY) {
        super(Resources.ultimateImages[era - 1].Clone());

        this.era = era;
        this.spawnDelay = spawnDelay;
        this.damage = damage;
        this.moveSpeedX = moveSpeedX;
        this.moveSpeedY = moveSpeedY;
    }

    public void ultimateMove() {
        if (this.spawnDelay <= 0) {
            final float moveY = -this.moveSpeedY * MainGame.deltaTime;
            final float moveX = this.moveSpeedX * MainGame.deltaTime;

            this.image.Move(moveX, moveY);
        } else {
            this.spawnDelay -= MainGame.deltaTime;
        }
    }

    public void ultimateExplode(final ArrayList<Unit> units) {
        switch (era) {
            case 1: {
                final float objCenterX = this.image.GetTransform().x + this.image.naturalWidth / 2f;
                final float minEffectRange = objCenterX - 250;
                final float maxEffectRange = objCenterX + 250;

                for (final Unit u : units) {
                    if (u.image.GetTransform().x >= minEffectRange && u.image.GetTransform().x + u.image.naturalWidth <= maxEffectRange) {
                        u.health -= this.damage;
                    }
                }
            }
            break;
            case 2:
            case 3:
            case 4: {
                final float objCenterX = this.image.GetTransform().x + this.image.naturalWidth / 2f;

                for (final Unit u : units) {
                    if (u.image.GetTransform().x <= objCenterX && u.image.GetTransform().x + u.image.naturalWidth >= objCenterX) {
                        u.health -= this.damage;

                        if (this.era == 2) {
                            AudioController.PlayAndSetVolume(Resources.cavalryHitSounds[0], MainGame.AUDIO_VOLUME / 3);
                        }
                    }
                }
            }
            break;
        }
    }
}
