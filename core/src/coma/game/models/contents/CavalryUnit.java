package coma.game.models.contents;

import com.badlogic.gdx.audio.Sound;
import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Cavalry unit class.
 */
final public class CavalryUnit extends Unit {

    public static final int[][] stats = {
            { 200, 60, 50, 240 },
            { 320, 90, 50, 360 },
            { 480, 135, 65, 450 },
            { 650, 150, 50, 600 }
    };

    public CavalryUnit(final int era, final int[] s) {
        super(new Animator(Resources.cavalryUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.cavalryHitSounds[era - 1];

        switch (era) {
            case 1: {
                this.deadSound = new Sound[2];
                this.deadSound[0] = Resources.cavalryDieSounds[0];
                this.deadSound[1] = Resources.meleeDie1;
            } break;
            case 2: {
                this.deadSound = new Sound[2];
                this.deadSound[0] = Resources.cavalryDieSounds[1];
                this.deadSound[1] = Resources.meleeDie1;
            } break;
            case 3:
            case 4: {
                this.deadSound = new Sound[2];
                this.deadSound[0] = Resources.cavalryDieSounds[2];
                this.deadSound[1] = Resources.explosionSounds[0];
            }
        }
    }

    public short GetDeploymentDelay() {
        return 200;
    }
}
