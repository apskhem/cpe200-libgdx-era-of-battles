package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Melee unit class.
 */
final public class MeleeUnit extends Unit {

    public static final int[][] stats = {
            { 100, 23, 44, 80 },
            { 180, 45, 44, 240 },
            { 320, 60, 44, 600 },
            { 900, 150, 44, 2200 }
    };

    public MeleeUnit(final int era, final int[] s) {
        super(new Animator(Resources.meleeUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.meleeHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 100;
    }
}
