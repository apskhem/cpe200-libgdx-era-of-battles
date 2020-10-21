package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Ranged unit class.
 */
final public class RangedUnit extends Unit {

    public static final int[][] stats = {
            { 80, 10, 18, 220 },
            { 150, 27, 32, 690 },
            { 280, 34, 12, 3000 },
            { 560, 103, 23, 8000 }
    };

    public RangedUnit(final int era, final int[] s) {
        super(new Animator(Resources.rangedUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.rangedHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 130;
    }
}
