package coma.game.models.contents;

import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Melee unit class.
 */
final public class MeleeUnit extends Unit {

    public static final int[][] stats = {
            { 120, 25, 40, 80 },
            { 250, 50, 40, 200 },
            { 500, 100, 40, 480 },
            { 1000, 200, 40, 1000 }
    };

    public MeleeUnit(final int era, final int[] s) {
        super(new Animator(Resources.meleeUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.meleeHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 100;
    }
}
