package coma.game.models.contents;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.components.Image;
import org.w3c.dom.ranges.RangeException;

/**
 * turret class.
 */
final public class Turret extends GameObject {

    public int attack;
    public int cost;
    public byte era;
    private short attackDelay = Turret.ATTACK_DELAY;

    private static final short ATTACK_DELAY = 25;
    private static final short ATTACK_RANGE = 420;

    public Turret(final Image img, final int attack, final int cost, final int era) {
        super(img);

        this.attack = attack;
        this.cost = cost;
        this.era = (byte) era;
    }

    // enum
    public static Turret GetEra(final byte era) {
        switch (era) {
            case 1:
                return new Turret(Resources.turretImages[0].Clone(), 15, 1000, era);
            case 2:
                return new Turret(Resources.turretImages[1].Clone(), 35, 3500, era);
            case 3:
                return new Turret(Resources.turretImages[2].Clone(), 65, 11000, era);
            case 4:
                return new Turret(Resources.turretImages[3].Clone(), 130, 50000, era);
            default:
                throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }

    public void Attack(final Unit unit) {
        if (unit == null) return;

        final float dl = Math.abs(this.image.GetTransform().x + this.image.naturalWidth / 2f - unit.image.GetTransform().x);
        final float dh = Math.abs(this.image.GetTransform().y - unit.image.GetTransform().y);

        if (dl < Turret.ATTACK_RANGE) {
            if (this.attackDelay < 0) {
                final float uh = unit.image.naturalHeight / 2f;
                final double deg = Math.atan2(dh - uh, dl) * 180 / Math.PI;

                unit.health -= this.attack;

                this.image.SetRotation((float) deg * (this.image.isFlipped ? 1 : -1));

                this.attackDelay = Turret.ATTACK_DELAY;

                Resources.rangedHitSounds[era - 1].setVolume(Resources.rangedHitSounds[era - 1].play(), 0.5f);
            } else {
                this.attackDelay -= MainGame.deltaTime;
            }
        }
    }

    public void ReplaceWith(final Turret t) {
        this.image.SetTexture(t.image);

        this.attack = t.attack;
        this.cost = t.cost;
        this.era = t.era;
    }
}
