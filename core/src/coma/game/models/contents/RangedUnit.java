package coma.game.models.contents;

import com.badlogic.gdx.audio.Sound;
import coma.game.Resources;
import coma.game.components.Animator;

/**
 * Ranged unit class.
 */
final public class RangedUnit extends Unit {

    public static final int[][] stats = {
            { 80, 9, 15, 100 },
            { 150, 19, 15, 200 },
            { 230, 28, 12, 320 },
            { 350, 75, 30, 450 }
    };

    public RangedUnit(final int era, final int[] s) {
        super(new Animator(Resources.rangedUnitImages[era - 1]), s[0], s[1], s[2], s[3], era);
        this.attackSound = Resources.rangedHitSounds[era - 1];

        this.deadSound = new Sound[1];
        this.deadSound[0] = Resources.meleeDie1;
    }

    public short GetDeploymentDelay() {
        return 130;
    }
}
