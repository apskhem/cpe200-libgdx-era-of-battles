package coma.game.models.contents;

import coma.game.Resources;

/**
 * Player's stronghold class.
 */
final public class Stronghold extends GameObject {

    public short health = Stronghold.GetMaxHealth((byte) 1);
    public byte era = 1;

    public Stronghold() {
        super(Resources.strongholdImages[0].Clone());
    }

    public float GetPercentageHealth(final float multiplier) {
        return this.health < 0
                ? 0
                : this.health * multiplier / Stronghold.GetMaxHealth(this.era);
    }

    public void UpgradeTo(byte era) {
        final float oHealthPercentage = this.health / (float) Stronghold.GetMaxHealth(this.era);

        this.era = era;
        this.health = (short) (Stronghold.GetMaxHealth(this.era) * oHealthPercentage);
        this.image.SetTexture(Resources.strongholdImages[era - 1]);
    }

    public void SetEra(byte era) {
        this.era = era;
        this.health = Stronghold.GetMaxHealth(this.era);
        this.image.SetTexture(Resources.strongholdImages[era - 1]);
    }

    // static methods
    public static short GetMaxHealth(final byte era) {
        switch (era) {
            case 1:
                return 1500;
            case 2:
                return 2600;
            case 3:
                return 5200;
            case 4:
                return 10400;
            default:
                return Short.MAX_VALUE;
        }
    }

    public static short GetRequiredXp(final byte era) {
        switch (era) {
            case 1:
                return 1000;
            case 2:
                return 6500;
            case 3:
                return 30000;
            default:
                return Short.MAX_VALUE;
        }
    }
}
