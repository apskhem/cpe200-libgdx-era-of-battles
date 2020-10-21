package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Cavalry unit class.
 */
final public class CavalryUnit extends Unit {

    public static final int[][] stats = {
            { 220, 45, 60, 180 },
            { 450, 100, 60, 450 },
            { 900, 200, 60, 950 },
            { 1800, 400, 60, 2000 }
    };

    public CavalryUnit(final int era, final int[] s) {
        super(new Animator(Resources.cavalryUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.cavalryHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 300;
    }
}
