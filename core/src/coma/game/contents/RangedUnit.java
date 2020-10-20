package coma.game.contents;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.components.Animator;
import org.w3c.dom.ranges.RangeException;

/**
 * Ranged unit class.
 */
final public class RangedUnit extends Unit {

    public static final int[][] stats = {
            { 80, 10, 18, 220 },
            { 150, 21, 18, 690 },
            { 280, 43, 18, 3000 },
            { 560, 92, 18, 8000 }
    };

    public RangedUnit(final int era, final int[] s) {
        super(new Animator(Resources.rangedUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.rangedHitSounds[era - 1];
    }

    public short GetDeploymentDelay() {
        return 130;
    }
}
