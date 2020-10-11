package coma.game;

import com.badlogic.gdx.audio.Sound;

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
    public boolean isFlipped = false;
    public final Image healthBar;
    public final Image healthBarInner;

    public short health;
    public final short maxHealth;
    public final short attack;
    public final short cost;

    protected byte attackDelay = Unit.MELEE_ATTACK_DELAY;
    private byte moveAnimationDelay = Unit.ANIMATION_DELAY;
    protected byte animationState = 1;
    private byte deadDelay = 100;
    protected byte flippingTranslateX = 0;

    public static float MAX_MOVE = 1700;
    public static float MOVE_SPEED = 1.2f;
    public static byte MELEE_ATTACK_DELAY = 50;
    public static byte RANGED_ATTACK_DELAY = 18;
    public static byte ANIMATION_DELAY = 10;
    public static Sound meleeHit1;
    public static Sound rangedHit1;
    public static Sound meleeDie1;
    public static Sound unitCall;
    public static final ArrayList<Unit> deadUnits = new ArrayList();
    public static final ArrayList<Unit> toRemoveDeadUnits = new ArrayList();

    public Unit(Image img, int health, int attack, int cost) {
        super(img);

        this.healthBar = Image.unitHealthBar.Clone();
        this.healthBarInner = Image.unitHealthBarInner.Clone();
        this.maxHealth = (short) health;
        this.health = (short) health;
        this.attack = (short) attack;
        this.cost = (short) cost;
    }

    public void SetPosition(float x, float y) {
        this.image.src.setPosition(x, y);
        this.healthBar.src.setPosition(x + this.image.naturalWidth / 2 - this.healthBar.naturalWidth / 2, y + 200);
        this.healthBarInner.src.setPosition(1 + x + this.image.naturalWidth / 2 - this.healthBar.naturalWidth / 2, y + 201);
    }

    public void SetFlip(boolean value) {
        this.isFlipped = value;
        this.image.src.setFlip(value, false);
    }

    public boolean IsReachedMax() {
        return moveX >= Unit.MAX_MOVE;
    }

    public void UpdateHealthBar() {
        this.healthBarInner.src.setBounds(this.healthBarInner.src.getX(),
                this.healthBarInner.src.getY(),
                this.health * this.healthBarInner.naturalWidth / this.maxHealth,
                this.healthBarInner.naturalHeight);
    }

    public void Move() {
        // normal moving
        if (moveX < Unit.MAX_MOVE) {
            moveX += Unit.MOVE_SPEED;
            this.image.src.translateX(this.isFlipped ? -Unit.MOVE_SPEED : Unit.MOVE_SPEED);
            this.healthBar.src.translateX(this.isFlipped ? -Unit.MOVE_SPEED : Unit.MOVE_SPEED);
            this.healthBarInner.src.translateX(this.isFlipped ? -Unit.MOVE_SPEED : Unit.MOVE_SPEED);

            if (this.animationState > 3) this.animationState = 3;

            // animation
            this.NormalAnimate();
        }
    }

    public boolean Attack(GameObject toAttackUnit) {
        if (toAttackUnit == null) return false;

        if (this.attackDelay < 0) {
            if (toAttackUnit instanceof Unit) {
                ((Unit) toAttackUnit).health -= this.attack + this.attack * Math.random() * 0.05f;
            }
            else if (toAttackUnit instanceof Stronghold) {
                ((Stronghold) toAttackUnit).health -= this.attack + this.attack * Math.random() * 0.05f;
            }

            // instance cases
            if (this instanceof MeleeUnit) {
                this.attackDelay = (byte)(Unit.MELEE_ATTACK_DELAY - Unit.MELEE_ATTACK_DELAY * Math.random() * 0.1f);
            }
            else if (this instanceof RangedUnit) {
                this.attackDelay = (byte)(Unit.RANGED_ATTACK_DELAY - Unit.RANGED_ATTACK_DELAY * Math.random() * 0.1f);
            }

            Unit.meleeHit1.setVolume(Unit.meleeHit1.play(), 0.5f);
            this.SetAnimationStateTo(6);
            this.moveAnimationDelay = 8;

            return true;
        }
        else {
            // animate
            if (this.moveAnimationDelay < 0) {
                if (this instanceof MeleeUnit) {
                    this.attackDelay = this.moveAnimationDelay = (byte)(Unit.MELEE_ATTACK_DELAY - 10);
                }
                else {
                    this.moveAnimationDelay = (byte)(Unit.MELEE_ATTACK_DELAY - 10);
                }

                this.SetAnimationStateTo(this.animationState == 4 ? 5 : 4);
            }
            else {
                this.moveAnimationDelay -= 1;
            }

            // count down attacking
            if (this.animationState == 4 || this.animationState == 5) this.attackDelay -= 1;

            return false;
        }
    }

    public void Die() {
        if (this.deadDelay < 0) {
            Renderer.RemoveComponents(this.image);
            Unit.toRemoveDeadUnits.add(this);
        }
        else {
            this.image.src.setColor(1,1,1,this.deadDelay/100f);
            this.deadDelay -= 1;
        }
    }

    public void NormalAnimate() {
        if (this.moveAnimationDelay < 0) {
            this.moveAnimationDelay = Unit.ANIMATION_DELAY;

            this.NextAnimationState();
        }
        else {
            this.moveAnimationDelay -= 1;
        }
    }

    public void NormalAnimate(int delay) {
        if (this.moveAnimationDelay < 0) {
            this.moveAnimationDelay = (byte) delay;

            this.NextAnimationState();
        }
        else {
            this.moveAnimationDelay -= 1;
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

    // static method
    public static void UpdateDeadUnits() {
        for (final Unit unit : Unit.deadUnits) {
            unit.Die();
        }

        Unit.deadUnits.removeAll(Unit.toRemoveDeadUnits);
        toRemoveDeadUnits.clear();
    }
}

/**
 * Melee unit class.
 */
class MeleeUnit extends Unit {

    public MeleeUnit(Image img, int health, int attack, int cost) {
        super(img, health, attack, cost);

        this.attackDelay = Unit.MELEE_ATTACK_DELAY;
    }

    public static MeleeUnit GetEra(byte era) {
        switch (era) {
            case 1: return new MeleeUnit(Image.meleeUnitEra1A.Clone(), 100, 23, 80);
            case 2: return new MeleeUnit(Image.meleeUnitEra1A.Clone(), 180, 45, 240);
            case 3: return new MeleeUnit(Image.meleeUnitEra1A.Clone(), 320, 60, 600);
            default: return null;
        }
    }

    @Override
    public void SetAnimationStateTo(int state) {
        if (this.animationState == state) return;

        this.animationState = (byte) state;

        switch (state) {
            case 1: {
                this.image.src.setTexture(Image.meleeUnitEra1A.src.getTexture());

                this.image.src.setBounds(this.image.src.getX() - this.flippingTranslateX, this.image.src.getY(), 90, 180);

                this.flippingTranslateX = 0;
            } break;
            case 2: {
                this.image.src.setTexture(Image.meleeUnitEra1B.src.getTexture());

                this.image.src.setBounds(this.image.src.getX() - this.flippingTranslateX, this.image.src.getY(), 90, 180);

                this.flippingTranslateX = 0;
            } break;
            case 3: {
                this.image.src.setTexture(Image.meleeUnitEra1C.src.getTexture());

                this.image.src.setBounds(this.image.src.getX() - this.flippingTranslateX, this.image.src.getY(), 90, 180);

                this.flippingTranslateX = 0;
            } break;
            case 4: {
                this.image.src.setTexture(Image.meleeUnitEra1D.src.getTexture());

                this.image.src.setBounds(this.image.src.getX() - this.flippingTranslateX, this.image.src.getY(), 90, 180);

                this.flippingTranslateX = 0;
            } break;
            case 5: {
                this.image.src.setTexture(Image.meleeUnitEra1E.src.getTexture());

                this.image.src.setBounds(this.image.src.getX() - this.flippingTranslateX, this.image.src.getY(), 96, 202);

                this.flippingTranslateX = 0;
            } break;
            case 6: {
                this.image.src.setTexture(Image.meleeUnitEra1F.src.getTexture());

                if (this.isFlipped) {
                    this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 135, this.image.naturalHeight);

                    this.image.src.translateX(-55);

                    this.flippingTranslateX = -55;
                }
                else {
                    this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 135, this.image.naturalHeight);
                }
            } break;
            case 7: {
                this.image.src.setTexture(Image.meleeUnitEra1G.src.getTexture());

                this.image.src.setBounds(this.image.src.getX() - this.flippingTranslateX, this.image.src.getY(), 90, 180);
            } break;
        }
    }
}

/**
 * Ranged unit class.
 */
class RangedUnit extends Unit {

    public RangedUnit(Image img, int health, int attack, int cost) {
        super(img, health, attack, cost);

        this.attackDelay = Unit.RANGED_ATTACK_DELAY;
    }

    public static RangedUnit GetEra(byte era) {
        switch (era) {
            case 1: return new RangedUnit(Image.rangedUnitEra1A.Clone(), 80, 10, 220);
            case 2: return new RangedUnit(Image.rangedUnitEra1A.Clone(), 150, 21, 690);
            case 3: return new RangedUnit(Image.rangedUnitEra1A.Clone(), 280, 43, 3000);
            default: return null;
        }
    }

    @Override
    public void SetAnimationStateTo(int state) {
        if (this.animationState == state) return;

        this.animationState = (byte) state;

        switch (state) {
            case 1: {
                this.image.src.setTexture(Image.rangedUnitEra1A.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
            case 2: {
                this.image.src.setTexture(Image.rangedUnitEra1B.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
            case 3: {
                this.image.src.setTexture(Image.rangedUnitEra1C.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
            case 4: {
                this.image.src.setTexture(Image.rangedUnitEra1D.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
            case 5: {
                this.image.src.setTexture(Image.rangedUnitEra1E.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
            case 6: {
                this.image.src.setTexture(Image.rangedUnitEra1F.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
            case 7: {
                this.image.src.setTexture(Image.rangedUnitEra1G.src.getTexture());

                this.image.src.setBounds(this.image.src.getX(), this.image.src.getY(), 90, 180);
            } break;
        }
    }
}

/**
 * Cavalry unit class.
 */
class CavalryUnit extends Unit {

    public CavalryUnit() {
        super(null, 0, 0, 0);
    }

    @Override
    public void SetAnimationStateTo(int state) {
        if (this.animationState == state) return;

        this.animationState = (byte) state;

        switch (state) {
            case 1: {

            } break;
            case 2: {

            } break;
            case 3: {

            } break;
            case 4: {

            } break;
            case 5: {

            } break;
            case 6: {

            } break;
        }
    }
}

/**
 * turret class.
 */
class Turret extends GameObject {

    public short attack;
    public byte era;

    public Turret() {
        super("");
    }
}

/**
 * Player's stronghold class.
 */
class Stronghold extends GameObject {

    public short health;
    public short maxHealth;
    public byte era;

    public Stronghold() {
        super("base-era-1.png");
    }

    public float GetPercentageHealth(float multiplier) {
        return this.health < 0
        ? 0
        : this.health * multiplier / this.maxHealth;
    }

    public void SetEra1() {
        this.health = 600;
        this.maxHealth = 600;
    }
}