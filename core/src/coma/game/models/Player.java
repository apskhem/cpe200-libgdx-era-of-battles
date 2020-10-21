package coma.game.models;

import com.badlogic.gdx.utils.Queue;
import coma.game.MainGame;
import coma.game.Resources;
import coma.game.controllers.AudioController;
import coma.game.models.contents.*;
import coma.game.views.Renderer;

import java.util.ArrayList;

public class Player {

    // constants
    private final short SPAWN_POSITION_X;
    protected final short SPAWN_POSITION_Y = 52;

    // properties
    public final Stronghold stronghold = new Stronghold();
    public final ArrayList<Turret> turrets = new ArrayList<>();
    public final ArrayList<Unit> units = new ArrayList<>();
    public final Queue<Unit> deploymentQueue = new Queue<>();
    public Ultimate ultimateCaller;

    public int cash;
    public int xp;
    public byte era = 1;
    public float deploymentDelay;
    public float ultimateDelay = Player.ULTIMATE_LOADING_DELAY;

    // static
    public static final short LEFT_STRONGHOLD_POSITION_X = -80;
    public static final short RIGHT_STRONGHOLD_POSITION_X = 1760;
    public static final byte STRONGHOLD_POSITION_Y = 20;
    public static final byte MAX_UNIT = 10;
    public static final short ULTIMATE_LOADING_DELAY = 4000;

    public Player() {
        this.stronghold.SetEra(this.era);
        this.SPAWN_POSITION_X = 120;
        this.stronghold.image.SetPosition(Player.LEFT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
    }

    public void DeployUnit(final Unit u) {
        if (u == null) return;

        if (this.cash >= u.cost && this.units.size() + this.deploymentQueue.size < Player.MAX_UNIT) {
            this.cash -= u.cost;

            if (this.deploymentQueue.size == 0) this.deploymentDelay = u.GetDeploymentDelay();

            this.deploymentQueue.addLast(u);
        }
    }

    public void SpawnUnit(final Unit u) {
        if (u == null) return;

        this.units.add(u);
        u.SpawnAt(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);

        AudioController.PlayAndSetVolume(Resources.unitCallSound, MainGame.AUDIO_VOLUME);
    }

    public void ProcessUnitDeployment() {
        if (this.deploymentQueue.size == 0) return;

        if (this.deploymentDelay <= 0) {
            final Unit u = this.deploymentQueue.removeFirst();

            this.SpawnUnit(u);

            if (this.deploymentQueue.size > 0) this.deploymentDelay = this.deploymentQueue.first().GetDeploymentDelay();
        }
        else {
            this.deploymentDelay -= MainGame.deltaTime;
        }
    }

    public boolean BuildTurret(final Turret t) {
        // check if can build
        if (t == null) {
            boolean canBuildTurret = this.cash >= Turret.GetEra(this.era).cost && this.turrets.size() < 2;

            for (final Turret at : this.turrets) {
                if (at.era < this.era && this.cash >= Turret.GetEra(this.era).cost - at.cost) {
                    canBuildTurret = true;
                    break;
                }
            }

            return canBuildTurret;
        }

        // check buying contition from existing turrets
        for (final Turret at : this.turrets) {
            if (at.era < this.era && this.cash >= Turret.GetEra(this.era).cost - at.cost) {
                this.cash -= t.cost - at.cost;

                // replace
                at.ReplaceWith(t);

                AudioController.PlayAndSetVolume(Resources.unitCallSound, MainGame.AUDIO_VOLUME);

                return true;
            }
        }

        if (this.cash >= t.cost && this.turrets.size() < 2) {
            this.cash -= t.cost;
            this.turrets.add(t);

            t.image.SetPosition(65, this.turrets.size() == 1 ? 260 : 340);
            Renderer.AddComponents(t.image);

            AudioController.PlayAndSetVolume(Resources.unitCallSound, MainGame.AUDIO_VOLUME);

            return true;
        }

        return false;
    }

    public void UpgradeStronghold() {
        if (this.era < 4 && this.xp >= Stronghold.GetRequiredXp(this.era)) {
            this.era++;

            this.stronghold.UpgradeTo(this.era);

            AudioController.PlayAndSetVolume(Resources.newEraSound, MainGame.AUDIO_VOLUME);
        }
    }

    public void UseUltimate() {
        if (this.ultimateDelay <= 0) {
            this.ultimateCaller = new Ultimate(this.era, false);
            this.ultimateDelay = Player.ULTIMATE_LOADING_DELAY;
        }
    }

    public void UpdateAfter(final int rawCost) {
        this.cash += (int)(rawCost * 0.9);
        this.xp += (int)(rawCost * Math.random() * 0.3f + rawCost * 0.05f);
    }

    //refractor
    // automation looping
    public int UpdateUnits(final boolean isOverlapped) {
        // update overall
        this.ProcessUnitDeployment();

        if (this.ultimateDelay > 0) this.ultimateDelay -= MainGame.deltaTime;

        if (this.ultimateCaller != null) this.ultimateCaller.Update();

        // check dead units
        int deadCost = 0;
        for (int i = 0; i < this.units.size(); i++) {
            final Unit u = this.units.get(i);

            // destroy
            if (u.health < 0) {
                deadCost += u.cost;

                this.units.remove(u);
                u.Die();

                AudioController.PlayAndSetVolume(Resources.meleeDie1, MainGame.AUDIO_VOLUME);

                if (u instanceof CavalryUnit && u.era == 1) {
                    AudioController.PlayAndSetVolume(Resources.cavalryDie1, MainGame.AUDIO_VOLUME / 3);
                }
            }
        }

        // moving
        for (int i = 0; i < this.units.size(); i++) {
            final Unit currentUnit = this.units.get(i);
            final Unit inFrontUnit = i == 0 ? null : this.units.get(i - 1);

            currentUnit.UpdateHealthBar();

            // the font most unit
            if (inFrontUnit == null) {
                if (!isOverlapped) {
                    currentUnit.Move();
                }
            }
            // in queue units
            else {
                if (currentUnit.moveX + currentUnit.image.naturalWidth < inFrontUnit.moveX) {
                    currentUnit.isMoving = true;
                    currentUnit.Move();
                }
                else {
                    currentUnit.isMoving = false;
                    if (!((i == 1 || i == 2) && currentUnit instanceof RangedUnit)) {
                        currentUnit.animator.SetAnimationFrameTo(1);
                    }
                }
            }
        }

        return deadCost;
    }

    public void ClearAllUnits() {
        for (final Unit unit : this.units) {
            Renderer.RemoveComponents(unit.image, unit.healthBar, unit.healthBarInner);
        }

        for (final Turret turret : this.turrets) {
            Renderer.RemoveComponents(turret.image);
        }

        this.deploymentQueue.clear();
        this.units.clear();
        this.turrets.clear();
    }

    public void Setup() {
        this.cash = 400;
        this.xp = 0;
        this.deploymentDelay = 0;
        this.ultimateDelay = Player.ULTIMATE_LOADING_DELAY;
        this.stronghold.SetEra(this.era = 1);
    }

    // static methods
    private static void QueuedRangedUnitAttack(final Player player, final GameObject toAttackUnit) {
        final Unit u2 = player.units.size() > 1 ? player.units.get(1) : null;
        final Unit u3 = player.units.size() > 2 ? player.units.get(2) : null;

        if (u2 instanceof RangedUnit && !u2.isMoving) u2.Attack(toAttackUnit);
        if (u3 instanceof RangedUnit && !u3.isMoving) u3.Attack(toAttackUnit);
    }

    //refractor
    public static void Update(final Player playerL, final Player playerR) {
        // check overlapping
        boolean isOverlapped = false;
        if (playerL.units.size() > 0 && playerR.units.size() > 0) {
            final Unit ul1 = playerL.units.get(0);
            final Unit ur1 = playerR.units.get(0);

            isOverlapped = ul1.image.GetTransform().x + ul1.image.naturalWidth / 1.2 > ur1.image.GetTransform().x;

            // for front unit
            if (isOverlapped) {
                ul1.Attack(ur1);
                ur1.Attack(ul1);

                Player.QueuedRangedUnitAttack(playerL, ur1);
                Player.QueuedRangedUnitAttack(playerR, ul1);
            }
        }
        else if (playerL.units.size() != 0 || playerR.units.size() != 0) {
            final Player attacker = playerL.units.size() != 0 ? playerL : playerR;
            final Player defender = playerL.units.size() != 0 ? playerR : playerL;
            final Unit u = attacker.units.get(0);

            if (u.IsReachedMax()) {
                if (u.Attack(defender.stronghold)) {
                    attacker.xp += (int)(Stronghold.GetMaxHealth(defender.era) * Math.random() * 0.01f);
                }

                Player.QueuedRangedUnitAttack(attacker, defender.stronghold);
            }
        }

        // turret firing
        for (final Turret turret : playerL.turrets) {
            if (playerR.units.size() > 0) turret.Attack(playerR.units.get(0));
        }

        for (final Turret turret : playerR.turrets) {
            if (playerL.units.size() > 0) turret.Attack(playerL.units.get(0));
        }

        // using ultimate
        if (playerL.ultimateCaller != null) {
            playerL.ultimateCaller.caller = playerL;
            playerL.ultimateCaller.target = playerR;
        }

        if (playerR.ultimateCaller != null) {
            playerR.ultimateCaller.caller = playerR;
            playerR.ultimateCaller.target = playerL;
        }

        // playerL
        playerR.UpdateAfter(playerL.UpdateUnits(isOverlapped));

        // playerR
        playerL.UpdateAfter(playerR.UpdateUnits(isOverlapped));
    }
}