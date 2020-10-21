package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Ranged unit class.
 */
final public class RangedUnit extends Unit {

    public static final int[][] stats = {
            { 80, 20, 30, 100 },
            { 150, 40, 30, 200 },
            { 230, 55, 30, 340 },
            { 350, 90, 30, 450 }
    };

    public RangedUnit(final int era, final int[] s) {
        super(new Animator(Resources.rangedUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.rangedHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 130;
    }
}
