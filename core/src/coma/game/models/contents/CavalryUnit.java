package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Cavalry unit class.
 */
final public class CavalryUnit extends Unit {

    public static final int[][] stats = {
            { 320, 60, 50, 600 },
            { 540, 90, 50, 1500 },
            { 860, 135, 65, 3000 },
            { 1450, 150, 50, 6800 }
    };

    public CavalryUnit(final int era, final int[] s) {
        super(new Animator(Resources.cavalryUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.cavalryHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 200;
    }
}
