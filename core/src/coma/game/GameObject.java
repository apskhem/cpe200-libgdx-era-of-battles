package coma.game;

import com.badlogic.gdx.audio.Sound;

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

    public short health;
    public final short attack;
    public final short cost;

    private byte attackDelay = 0;

    public static float MAX_MOVE = 1700;
    public static float MOVE_SPEED = 1.2f;
    public static Sound meleeHit1;
    public static Sound meleeDie1;
    public static Sound unitCall;

    public Unit(Image img, int health, int attack, int cost) {
        super(img);

        this.health = (short) health;
        this.attack = (short) attack;
        this.cost = (short) cost;
    }

    public void SetFlip(boolean value) {
        this.isFlipped = value;
        this.image.src.setFlip(value, false);
    }

    public boolean IsReachedMax() {
        return moveX >= Unit.MAX_MOVE;
    }

    public void Move() {
        if (moveX < Unit.MAX_MOVE) {
            moveX += Unit.MOVE_SPEED;
            this.image.src.translateX(this.isFlipped ? -Unit.MOVE_SPEED : Unit.MOVE_SPEED);
        }
    }

    public void Attack(Unit toAttackUnit) {
        if (this.attackDelay < 10) {
            toAttackUnit.health -= this.attack;

            this.attackDelay = 100;

            Unit.meleeHit1.setVolume(Unit.meleeHit1.play(), 0.5f);
        }
        else {
            this.attackDelay -= 2;
        }
    }

    public void Attack(Stronghold toAttackStronghold) {
        if (this.attackDelay < 10) {
            toAttackStronghold.health -= this.attack;

            this.attackDelay = 100;
            Unit.meleeHit1.play();
        }
        else {
            this.attackDelay -= 2;
        }
    }

    public void Destroy() {
        Unit.meleeDie1.play();
    }
}

/**
 * Melee unit class.
 */
class MeleeUnit extends Unit {

    public MeleeUnit(Image img, int health, int attack, int cost) {
        super(img, health, attack, cost);
    }

    public static MeleeUnit GetEra(byte era) {
        switch (era) {
            case 1: return new MeleeUnit(Image.GetMeleeUnitImage(), 100, 23, 80);
            case 2: return new MeleeUnit(Image.GetMeleeUnitImage(), 180, 45, 240);
            case 3: return new MeleeUnit(Image.GetMeleeUnitImage(), 320, 60, 600);
            default: return null;
        }
    }
}

/**
 * Ranged unit class.
 */
class RangedUnit extends Unit {

    public RangedUnit() {
        super(null, 0, 0, 0);
    }
}

/**
 * Cavalry unit class.
 */
class CavalryUnit extends Unit {

    public CavalryUnit() {
        super(null, 0, 0, 0);
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
    public byte era;

    public Stronghold() {
        super("base-era-1.png");
    }
}