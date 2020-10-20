package coma.game.contents;

import com.badlogic.gdx.audio.Sound;
import coma.game.MainGame;
import coma.game.Resources;
import coma.game.components.Animator;
import coma.game.utils.Mathf;
import coma.game.utils.Renderer;
import coma.game.components.Image;

import java.util.ArrayList;

/**
 * Class for all game units.
 */
public abstract class Unit extends GameObject {

    public final Image healthBar;
    public final Image healthBarInner;
    public final Animator animator;
    public Sound attackSound;

    public float moveX;
    public short health;
    public boolean isMoving;
    public final short maxHealth;
    public final short maxAttackDelay;
    public final short attack;
    public final short cost;
    public final byte era;

    protected float attackDelay;
    private float deadDelay = 100;

    // static constant
    public static final float MAX_MOVE = 1790;
    public static final float MOVE_SPEED = 1.2f;
    public static final byte ANIMATION_DELAY = 10;
    public static final ArrayList<Unit> deadUnits = new ArrayList<>();
    public static final ArrayList<Unit> toRemoveDeadUnits = new ArrayList<>();

    public Unit(final Animator anim, final int health, final int attack, final int delay, final int cost, final int era) {
        super(anim.GetFrameImage(0).Clone());

        this.animator = anim;
        this.animator.refImage = this.image;
        this.animator.maxNextFrameDelay = Unit.ANIMATION_DELAY;

        // this.animator.AddState("standby", 1, 1);
        // this.animator.AddState("walking", 1, 3);
        // this.animator.AddState("attacking", 4, 6);
        // this.animator.AddState("hitting", 6, 6);
        // this.animator.AddState("die", 7, 7);

        this.healthBar = Resources.unitHealthBar.Clone();
        this.healthBarInner = Resources.unitHealthBarInner.Clone();
        this.maxHealth = (short) health;
        this.maxAttackDelay = (short) delay;
        this.health = (short) health;
        this.attack = (short) attack;
        this.attackDelay = (short) delay;
        this.cost = (short) cost;
        this.era = (byte) era;
    }

    public void SpawnAt(final float x, final float y) {
        this.image.SetPosition(x, y);
        this.healthBar.SetPosition(x + (this.image.naturalWidth - this.healthBar.naturalWidth) / 2f, y + 200);
        this.healthBarInner.SetPosition(1 + x + (this.image.naturalWidth - this.healthBar.naturalWidth) / 2f, y + 201);

        Renderer.AddComponents(this.image, this.healthBar, this.healthBarInner);
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

            if (this.animator.currentFrame > 3) this.animator.currentFrame = 3;

            // animation
            this.animator.Continue();
        }
    }

    public boolean Attack(final GameObject toAttackUnit) {
        if (toAttackUnit == null) return false;

        if (this.attackDelay < 0) {
            if (toAttackUnit instanceof Unit) {
                ((Unit) toAttackUnit).health -= Mathf.CalError(this.attack, 0.05f);
            }
            else if (toAttackUnit instanceof Stronghold) {
                ((Stronghold) toAttackUnit).health -= Mathf.CalError(this.attack, 0.05f);
            }

            if (this.attackSound != null) {
                this.attackSound.setVolume(this.attackSound.play(), 0.5f);
            }

            this.animator.SetAnimationFrameTo(6);
            this.animator.nextFrameDelay = 8;

            this.attackDelay = (byte) Mathf.CalError(this.maxAttackDelay, 0.1f);

            return true;
        } else {
            // animate
            if (this.animator.nextFrameDelay < 0) {
                this.animator.nextFrameDelay = this.maxAttackDelay - 8;

                this.animator.SetAnimationFrameTo(this.animator.currentFrame == 4 ? 5 : 4);
            } else {
                this.animator.nextFrameDelay -= MainGame.deltaTime;
            }

            // count down attacking
            if (this.animator.currentFrame == 4 || this.animator.currentFrame == 5) this.attackDelay -= MainGame.deltaTime;

            return false;
        }
    }

    public void Die() {
        this.animator.SetAnimationFrameTo(7);
        Renderer.RemoveComponents(this.healthBar, this.healthBarInner);
        Unit.deadUnits.add(this);
    }

    private void ContinueDeadFading() {
        if (this.deadDelay < 0) {
            Renderer.RemoveComponents(this.image);
            Unit.toRemoveDeadUnits.add(this);
        } else {
            this.image.SetOpacity(this.deadDelay / 100f);
            this.deadDelay -= MainGame.deltaTime;
        }
    }

    public abstract short GetDeploymentDelay();

    // static methods
    public static void UpdateDeadUnits() {
        for (final Unit unit : Unit.deadUnits) {
            unit.ContinueDeadFading();
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
