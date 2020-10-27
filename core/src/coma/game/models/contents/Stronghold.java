package coma.game.models.contents;

import coma.game.Resources;

/**
 * Player's stronghold class.
 */
final public class Stronghold extends GameObject {

    public short health = Stronghold.getMaxHealth((byte) 1);
    public byte era = 1;

    public Stronghold() {
        super(Resources.strongholdImages[0].clone());
    }

    public float getPercentageHealth(final float multiplier) {
        return this.health < 0
                ? 0
                : this.health * multiplier / Stronghold.getMaxHealth(this.era);
    }

    public void upgradeTo(byte era) {
        final float oHealthPercentage = this.health / (float) Stronghold.getMaxHealth(this.era);

        this.era = era;
        this.health = (short) (Stronghold.getMaxHealth(this.era) * oHealthPercentage);
        this.image.setTexture(Resources.strongholdImages[era - 1]);
    }

    public void setEra(byte era) {
        this.era = era;
        this.health = Stronghold.getMaxHealth(this.era);
        this.image.setTexture(Resources.strongholdImages[era - 1]);
    }

    // static methods
    public static short getMaxHealth(final byte era) {
        switch (era) {
            case 1:
                return 1000;
            case 2:
                return 1800;
            case 3:
                return 2300;
            case 4:
                return 3000;
            default:
                return Short.MAX_VALUE;
        }
    }

    public static short getRequiredXp(final byte era) {
        switch (era) {
            case 1:
                return 600;
            case 2:
                return 1800;
            case 3:
                return 5000;
            default:
                return Short.MAX_VALUE;
        }
    }
}
