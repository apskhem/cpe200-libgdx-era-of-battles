package coma.game;

import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;

/**
 * Base class for all entities in game scenes.
 */
public abstract class GameObject {

    public final Image image;

    public GameObject(String internalPath) {
        this.image = new Image(internalPath);
    }

    public GameObject(Image image) {
        this.image = image;
    }
}

/**
 * Class for all game units.
 */
abstract class Unit extends GameObject {

    public float moveX;
    public final Image healthBar;
    public final Image healthBarInner;

    public short health;
    public final short maxHealth;
    public final short attack;
    public final short cost;
    public final byte era;
    public boolean isMoving;

    protected float attackDelay = Unit.MELEE_ATTACK_DELAY;
    private float moveAnimationDelay = Unit.ANIMATION_DELAY;
    private float deadDelay = 100;
    protected byte animationState = 1;
    protected short displacedTranslateX = 0;

    public static float MAX_MOVE = 1790;
    public static float MOVE_SPEED = 1.2f;
    public static byte MELEE_ATTACK_DELAY = 44;
    public static byte RANGED_ATTACK_DELAY = 18;
    public static byte CAVALRY_ATTACK_DELAY = 56;
    public static byte ANIMATION_DELAY = 10;
    public static final ArrayList<Unit> deadUnits = new ArrayList<>();
    public static final ArrayList<Unit> toRemoveDeadUnits = new ArrayList<>();

    public Unit(Image img, int health, int attack, int cost, int era) {
        super(img);

        this.healthBar = MainGame.unitHealthBar.Clone();
        this.healthBarInner = MainGame.unitHealthBarInner.Clone();
        this.maxHealth = (short) health;
        this.health = (short) health;
        this.attack = (short) attack;
        this.cost = (short) cost;
        this.era = (byte) era;
    }

    public void SetPosition(float x, float y) {
        this.image.SetPosition(x, y);
        this.healthBar.SetPosition(x + (this.image.naturalWidth - this.healthBar.naturalWidth) / 2f, y + 200);
        this.healthBarInner.SetPosition(1 + x + (this.image.naturalWidth - this.healthBar.naturalWidth) / 2f, y + 201);
    }

    public boolean IsReachedMax() {
        return moveX >= Unit.MAX_MOVE - this.image.naturalWidth;
    }

    public void UpdateHealthBar() {
        this.healthBarInner.SetSize(
                this.health > 0 ? this.health * this.healthBarInner.naturalWidth / (float) this.maxHealth : 0,
                Float.NaN);
    }

    public void Move() {
        // normal moving
        if (moveX < Unit.MAX_MOVE - this.image.naturalWidth) {
            final float mov = Unit.MOVE_SPEED * MainGame.deltaTime;

            moveX += mov;
            this.image.Move((this.image.isFlipped ? -mov : mov), 0);
            this.healthBar.Move(this.image.isFlipped ? -mov : mov, 0);
            this.healthBarInner.Move(this.image.isFlipped ? -mov : mov, 0);

            if (this.animationState > 3) this.animationState = 3;

            // animation
            this.NormalAnimate();
        }
    }

    public boolean Attack(GameObject toAttackUnit) {
        if (toAttackUnit == null) return false;

        if (this.attackDelay < 0) {
            if (toAttackUnit instanceof Unit) {
                ((Unit) toAttackUnit).health -= Mathf.CalError(this.attack, 0.05f);
            }
            else if (toAttackUnit instanceof Stronghold) {
                ((Stronghold) toAttackUnit).health -= Mathf.CalError(this.attack, 0.05f);
            }

            // instance cases
            if (this instanceof MeleeUnit) {
                this.attackDelay = (byte) Mathf.CalError(Unit.MELEE_ATTACK_DELAY, 0.1f);
                MainGame.meleeHitSounds[era - 1].setVolume(MainGame.meleeHitSounds[era - 1].play(), 0.5f);
            }
            else if (this instanceof RangedUnit) {
                this.attackDelay = (byte) Mathf.CalError(Unit.RANGED_ATTACK_DELAY, 0.1f);
                MainGame.rangedHitSounds[era - 1].setVolume(MainGame.rangedHitSounds[era - 1].play(), 0.5f);
            }
            else if (this instanceof CavalryUnit) {
                this.attackDelay = (byte) Mathf.CalError(Unit.CAVALRY_ATTACK_DELAY, 0.1f);
                MainGame.cavalryHitSounds[era - 1].setVolume(MainGame.cavalryHitSounds[era - 1].play(), 0.5f);
            }

            this.SetAnimationStateTo(6);
            this.moveAnimationDelay = 8;

            return true;
        }
        else {
            // animate
            if (this.moveAnimationDelay < 0) {
                if (this instanceof MeleeUnit) {
                    this.moveAnimationDelay = (byte)(Unit.MELEE_ATTACK_DELAY - 8);
                }
                else if (this instanceof RangedUnit) {
                    this.moveAnimationDelay = (byte)(Unit.RANGED_ATTACK_DELAY - 8);
                }
                else if (this instanceof CavalryUnit) {
                    this.moveAnimationDelay = (byte)(Unit.CAVALRY_ATTACK_DELAY - 8);
                }

                this.SetAnimationStateTo(this.animationState == 4 ? 5 : 4);
            }
            else {
                this.moveAnimationDelay -= MainGame.deltaTime;
            }

            // count down attacking
            if (this.animationState == 4 || this.animationState == 5) this.attackDelay -= MainGame.deltaTime;

            return false;
        }
    }

    public void Die() {
        if (this.deadDelay < 0) {
            Renderer.RemoveComponents(this.image);
            Unit.toRemoveDeadUnits.add(this);
        }
        else {
            this.image.SetOpacity(this.deadDelay/100f);
            this.deadDelay -= MainGame.deltaTime;
        }
    }

    public void NormalAnimate() {
        if (this.moveAnimationDelay < 0) {
            this.NextAnimationState();

            this.moveAnimationDelay = Unit.ANIMATION_DELAY;
        }
        else {
            this.moveAnimationDelay -= MainGame.deltaTime;
        }
    }

    public void NextAnimationState() {
        switch (this.animationState) {
            case 1: this.SetAnimationStateTo(2); break;
            case 2: this.SetAnimationStateTo(3); break;
            case 3: this.SetAnimationStateTo(1); break;
            case 4: this.SetAnimationStateTo(5); break;
            case 5: this.SetAnimationStateTo(6); break;
            case 6: this.SetAnimationStateTo(4);
        }
    }

    abstract void SetAnimationStateTo(int state);
    abstract short GetDeploymentDelay();

    // static methods
    public static void UpdateDeadUnits() {
        for (final Unit unit : Unit.deadUnits) {
            unit.Die();
        }

        Unit.deadUnits.removeAll(Unit.toRemoveDeadUnits);
        toRemoveDeadUnits.clear();
    }

    public static void ClearDeadUnitQueue() {
        for (final Unit unit : Unit.deadUnits) {
            Renderer.RemoveComponents(unit.image);
        }

        Unit.deadUnits.clear();
    }
}

/**
 * Melee unit class.
 */
class MeleeUnit extends Unit {

    public MeleeUnit(Image img, int health, int attack, int cost, int era) {
        super(img, health, attack, cost, era);

        this.attackDelay = Unit.MELEE_ATTACK_DELAY;
    }

    public static MeleeUnit GetEra(byte era) {
        switch (era) {
            case 1: return new MeleeUnit(MainGame.meleeUnitImages[0][0].Clone(), 100, 23, 80, era);
            case 2: return new MeleeUnit(MainGame.meleeUnitImages[1][0].Clone(), 180, 45, 240, era);
            case 3: return new MeleeUnit(MainGame.meleeUnitImages[2][0].Clone(), 320, 60, 600, era);
            case 4: return new MeleeUnit(MainGame.meleeUnitImages[3][0].Clone(), 900, 150, 2200, era);
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }

    @Override
    public void SetAnimationStateTo(int state) {
        if (this.animationState == state) return;

        this.animationState = (byte) state;

        this.image.SetTexture(MainGame.meleeUnitImages[this.era - 1][state - 1]);

        // reset displacement
        this.image.SetSize(Float.NaN, Float.NaN);
        this.image.Move(-this.displacedTranslateX, 0);
        this.displacedTranslateX = 0;

        switch (this.era) {
            case 1: {
                if (state == 5) {
                    this.image.SetSize(96, 202);
                }
                else if (state == 6) {
                    this.image.SetSize(135, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -55, 0);
                }
            } break;
            case 2: {
                if (state == 5) {
                    this.image.SetSize(137, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -38, 0);
                }
                else if (state == 6) {
                    this.image.SetSize(197, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -98, 0);
                }
            } break;
            case 3: {
                if (state == 5) {
                    this.image.SetSize(143, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -53, 0);
                }
                else if (state == 6) {
                    this.image.SetSize(185, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -95, 0);
                }
            } break;
            case 4: {
                if (state == 4) {
                    this.image.SetSize(158, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -68, 0);
                }
                else if (state == 5) {
                    this.image.SetSize(158, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -68, 0);
                }
                else if (state == 6) {
                    this.image.SetSize(200, 206);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -110, 0);
                }
                else if (state == 7) {
                    this.image.SetSize(105, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -15, 0);
                }
            } break;
            default: {
                throw new RangeException((short) 0, "Wrong parameter input.");
            }
        }
    }

    @Override
    public short GetDeploymentDelay() {
        return 100;
    }
}

/**
 * Ranged unit class.
 */
class RangedUnit extends Unit {

    public RangedUnit(Image img, int health, int attack, int cost, int era) {
        super(img, health, attack, cost, era);

        this.attackDelay = Unit.RANGED_ATTACK_DELAY;
    }

    public static RangedUnit GetEra(byte era) {
        switch (era) {
            case 1: return new RangedUnit(MainGame.rangedUnitImages[0][0].Clone(), 80, 10, 220, era);
            case 2: return new RangedUnit(MainGame.rangedUnitImages[1][0].Clone(), 150, 21, 690, era);
            case 3: return new RangedUnit(MainGame.rangedUnitImages[2][0].Clone(), 280, 43, 3000, era);
            case 4: return new RangedUnit(MainGame.rangedUnitImages[3][0].Clone(), 560, 92, 8000, era);
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }

    @Override
    public void SetAnimationStateTo(int state) {
        if (this.animationState == state) return;

        this.animationState = (byte) state;

        this.image.SetTexture(MainGame.rangedUnitImages[this.era - 1][state - 1]);

        // reset displacement
        this.image.SetSize(Float.NaN, Float.NaN);
        this.image.Move(-this.displacedTranslateX, 0);
        this.displacedTranslateX = 0;

        switch (this.era) {
            case 1: {
                if (state == 5) {
                    this.displacedTranslateX = (byte)(this.image.isFlipped ? -8 : 8);

                    this.image.Move(this.displacedTranslateX, 0);
                }
            } break;
            case 2: {
                if (state == 6) {
                    this.image.SetSize(125, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -35, 0);
                }
            } break;
            case 3: {
                if (state == 4 || state == 5) {
                    this.image.SetSize(132, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -42, 0);
                }
                else if (state == 6) {
                    this.image.SetSize(165, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -75, 0);
                }
            } break;
            case 4: {
                if (state == 4) {
                    this.image.SetSize(100, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -10, 0);
                }
                else if (state == 5) {
                    this.image.SetSize(179, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -89, 0);
                }
                else if (state == 6) {
                    this.image.SetSize(249, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -159, 0);
                }
                else if (state == 7) {
                    this.image.SetSize(150, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -60, 0);
                }
            } break;
            default: {
                throw new RangeException((short) 0, "Wrong parameter input.");
            }
        }
    }

    @Override
    public short GetDeploymentDelay() {
        return 130;
    }
}

/**
 * Cavalry unit class.
 */
class CavalryUnit extends Unit {

    public CavalryUnit(Image img, int health, int attack, int cost, int era) {
        super(img, health, attack, cost, era);
    }

    @Override
    public void SetAnimationStateTo(int state) {
        if (this.animationState == state) return;

        this.animationState = (byte) state;

        this.image.SetTexture(MainGame.cavalryUnitImages[this.era - 1][state - 1]);

        // reset displacement
        this.image.SetSize(Float.NaN, Float.NaN);
        this.image.Move(-this.displacedTranslateX, 0);
        this.displacedTranslateX = 0;

        switch (this.era) {
            case 1: {
                if (state == 6) {
                    this.image.SetSize(Float.NaN, 230);
                }
            } break;
            case 2: {
                if (state == 6) {
                    this.image.SetSize(275, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -55, 0);
                }
            } break;
            case 3: {
                if (state == 6) {
                    this.image.SetSize(720, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -116, 0);
                }
            } break;
            case 4: {
                if (state == 6) {
                    this.image.SetSize(540, Float.NaN);

                    if (this.image.isFlipped) this.image.Move(this.displacedTranslateX = -96, 0);
                }
            } break;
            default: {
                throw new RangeException((short) 0, "Wrong parameter input.");
            }
        }
    }

    @Override
    public short GetDeploymentDelay() {
        return 300;
    }

    public static CavalryUnit GetEra(byte era) {
        switch (era) {
            case 1: return new CavalryUnit(MainGame.cavalryUnitImages[0][0].Clone(), 320, 45, 600, era);
            case 2: return new CavalryUnit(MainGame.cavalryUnitImages[1][0].Clone(), 900, 95, 1500, era);
            case 3: return new CavalryUnit(MainGame.cavalryUnitImages[2][0].Clone(), 2000, 210, 5000, era);
            case 4: return new CavalryUnit(MainGame.cavalryUnitImages[3][0].Clone(), 3680, 456, 20000, era);
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }
}

/**
 * turret class.
 */
class Turret extends GameObject {

    public int attack;
    public int cost;
    public byte era;
    private short attackDelay = Turret.ATTACK_DELAY;

    private static final short ATTACK_DELAY = 25;
    private static final short ATTACK_RANGE = 420;

    public Turret(Image img, int attack, int cost, int era) {
        super(img);

        this.attack = attack;
        this.cost = cost;
        this.era = (byte) era;
    }

    public void Attack(Unit unit) {
        if (unit == null) return;

        final float dl = Math.abs(this.image.GetTransform().x - unit.image.GetTransform().x);
        final float dh = Math.abs(this.image.GetTransform().y - unit.image.GetTransform().y);

        if (dl < Turret.ATTACK_RANGE) {
            if (this.attackDelay < 0) {
                final float uh = unit.image.naturalHeight / 2f;
                final double deg = Math.atan2(dh - uh, dl) * 180 / Math.PI;

                unit.health -= this.attack;

                this.image.SetRotation((float) deg * (this.image.isFlipped ? 1 : -1));

                this.attackDelay = Turret.ATTACK_DELAY;

                MainGame.rangedHitSounds[era - 1].setVolume(MainGame.rangedHitSounds[era - 1].play(), 0.5f);
            }
            else {
                this.attackDelay -= MainGame.deltaTime;
            }
        }
    }

    public void ReplaceWith(Turret t) {
        this.image.SetTexture(t.image);

        this.attack = t.attack;
        this.cost = t.cost;
        this.era = t.era;
    }

    public static Turret GetEra(byte era) {
        switch (era) {
            case 1: return new Turret(MainGame.turretImages[0].Clone(), 8, 1200, era);
            case 2: return new Turret(MainGame.turretImages[1].Clone(), 21, 3500, era);
            case 3: return new Turret(MainGame.turretImages[2].Clone(), 48, 11000, era);
            case 4: return new Turret(MainGame.turretImages[3].Clone(), 130, 50000, era);
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }
}

/**
 * Player's stronghold class.
 */
class Stronghold extends GameObject {

    public short health = this.GetMaxHealth((byte) 1);
    public byte era = 1;

    public Stronghold() {
        super(MainGame.strongholdImages[0].Clone());
    }

    public float GetPercentageHealth(float multiplier) {
        return this.health < 0
        ? 0
        : this.health * multiplier / Stronghold.GetMaxHealth(this.era);
    }

    public void UpgradeTo(byte era) {
        final float oHealthPercentage = this.health / (float) Stronghold.GetMaxHealth(this.era);

        this.era = era;
        this.health = (short)(Stronghold.GetMaxHealth(this.era) * oHealthPercentage);
        this.image.SetTexture(MainGame.strongholdImages[era - 1]);
    }

    public void SetEra(byte era) {
        this.era = era;
        this.health = Stronghold.GetMaxHealth(this.era);
        this.image.SetTexture(MainGame.strongholdImages[era - 1]);
    }

    // static methods
    public static short GetMaxHealth(byte era) {
        switch (era) {
            case 1: return 600;
            case 2: return 2000;
            case 3: return 5000;
            case 4: return 10000;
            default: return Short.MAX_VALUE;
        }
    }

    public static short GetRequiredXp(byte era) {
        switch (era) {
            case 1: return 1000;
            case 2: return 6500;
            case 3: return 30000;
            default: return Short.MAX_VALUE;
        }
    }
}