package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Ranged unit class.
 */
final public class RangedUnit extends Unit {

    public static final int[][] stats = {
            { 80, 40, 40, 100 },
            { 160, 80, 40, 250 },
            { 320, 160, 40, 530 },
            { 640, 320, 40, 1200 }
    };

    public RangedUnit(final int era, final int[] s) {
        super(new Animator(Resources.rangedUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.rangedHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 130;
    }
}
