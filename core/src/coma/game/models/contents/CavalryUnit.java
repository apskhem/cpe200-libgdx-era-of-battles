package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Cavalry unit class.
 */
final public class CavalryUnit extends Unit {

    public static final int[][] stats = {
            { 320, 45, 56, 600 },
            { 900, 95, 56, 1500 },
            { 2000, 210, 56, 5000 },
            { 3680, 456, 56, 20000 }
    };

    public CavalryUnit(final int era, final int[] s) {
        super(new Animator(Resources.cavalryUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.cavalryHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 300;
    }
}
