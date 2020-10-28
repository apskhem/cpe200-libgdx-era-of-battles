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
    public EmergencyUltimate emergencyUltimateCaller;

    public int cash;
    public int xp;
    public byte era = 1;
    public float deploymentDelay;
    public float ultimateDelay = Player.ULTIMATE_LOADING_DELAY;
    protected short time2end = 1;

    // static
    public static final short LEFT_STRONGHOLD_POSITION_X = -80;
    public static final short RIGHT_STRONGHOLD_POSITION_X = 1760;
    public static final byte STRONGHOLD_POSITION_Y = 20;
    public static final byte MAX_UNIT = 10;
    public static final short ULTIMATE_LOADING_DELAY = 4000;

    public Player() {
        this.stronghold.setEra(this.era);
        this.SPAWN_POSITION_X = 120;
        this.stronghold.image.setPosition(Player.LEFT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
    }

    public boolean deployUnit(final Unit u) {
        if (u == null) return false;

        if (this.cash >= u.cost && this.units.size() + this.deploymentQueue.size < Player.MAX_UNIT) {
            this.cash -= u.cost;

            if (this.deploymentQueue.size == 0) this.deploymentDelay = u.getDeploymentDelay();

            this.deploymentQueue.addLast(u);

            return true;
        }

        return false;
    }

    public void spawnUnit(final Unit u) {
        if (u == null) return;

        this.units.add(u);
        u.spawnAt(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);

        AudioController.playAndSetVolume(Resources.unitCallSound, MainGame.AUDIO_VOLUME);
    }

    public void processUnitDeployment() {
        if (this.deploymentQueue.size == 0) return;

        if (this.deploymentDelay <= 0) {
            final Unit u = this.deploymentQueue.removeFirst();

            this.spawnUnit(u);

            if (this.deploymentQueue.size > 0) this.deploymentDelay = this.deploymentQueue.first().getDeploymentDelay();
        }
        else {
            this.deploymentDelay -= MainGame.deltaTime;
        }
    }

    public boolean buildTurret(final Turret t) {
        // check if can build
        if (t == null) {
            boolean canBuildTurret = this.cash >= Turret.getEra(this.era).cost && this.turrets.size() < 2;

            for (final Turret at : this.turrets) {
                if (at.era < this.era && this.cash >= Turret.getEra(this.era).cost - at.cost) {
                    canBuildTurret = true;
                    break;
                }
            }

            return canBuildTurret;
        }

        // check buying contition from existing turrets
        for (final Turret at : this.turrets) {
            if (at.era < this.era && this.cash >= Turret.getEra(this.era).cost - at.cost) {
                this.cash -= t.cost - at.cost;

                // replace
                at.replaceWith(t);

                AudioController.playAndSetVolume(Resources.unitCallSound, MainGame.AUDIO_VOLUME);

                return true;
            }
        }

        if (this.cash >= t.cost && this.turrets.size() < 2) {
            this.cash -= t.cost;
            this.turrets.add(t);

            t.image.setPosition(65, this.turrets.size() == 1 ? 260 : 340);
            Renderer.addComponents(t.image);

            AudioController.playAndSetVolume(Resources.unitCallSound, MainGame.AUDIO_VOLUME);

            return true;
        }

        return false;
    }

    public boolean upgradeStronghold() {
        if (this.era < 4 && this.xp >= Stronghold.getRequiredXp(this.era)) {
            this.era++;

            this.stronghold.upgradeTo(this.era);

            AudioController.playAndSetVolume(Resources.newEraSound, MainGame.AUDIO_VOLUME);

            return true;
        }

        return false;
    }

    public boolean useUltimate(final Player target) {
        if (target == null) return false;

        if (this.ultimateDelay <= 0) {
            this.ultimateCaller = new Ultimate(this, target, this.era, false);
            this.ultimateDelay = Player.ULTIMATE_LOADING_DELAY;

            if (this.era == 4)  this.time2end++;

            return true;
        }

        return false;
    }

    public boolean useEmergencyUltimate(final Player target) {
        if (target == null) return false;

        if (this.era >= 4 && this.xp >= EmergencyUltimate.REQUIRED_XP) {
            this.xp -= EmergencyUltimate.REQUIRED_XP;
            this.emergencyUltimateCaller = new EmergencyUltimate(this, target, false);

            return true;
        }

        return false;
    }

    public void updateAfter(final int rawCost) {

        switch (MainGame.foe.difficulty) {
            case 1:
            case 2: this.cash += (int) (rawCost * 0.9); break;
            case 3: this.cash += (int) ((rawCost * 0.9) - (0.15f * this.time2end)); break;
        }

        this.xp += (int)(rawCost * Math.random() * 0.3f + rawCost * 0.05f);
    }

    // automation looping
    public int updateUnits(final boolean isOverlapped) {
        // update overall
        this.processUnitDeployment();

        if (this.ultimateDelay > 0) this.ultimateDelay -= MainGame.deltaTime;

        if (this.ultimateCaller != null) this.ultimateCaller.update();
        if (this.emergencyUltimateCaller != null) this.emergencyUltimateCaller.update();

        // check dead units
        int deadCost = 0;
        for (int i = 0; i < this.units.size(); i++) {
            final Unit u = this.units.get(i);

            // destroy
            if (u.health < 0) {
                deadCost += u.cost;

                this.units.remove(u);
                u.die();
            }
        }

        // moving
        for (int i = 0; i < this.units.size(); i++) {
            final Unit currentUnit = this.units.get(i);
            final Unit inFrontUnit = i == 0 ? null : this.units.get(i - 1);

            currentUnit.updateHealthBar();

            // the font most unit
            if (inFrontUnit == null) {
                if (!isOverlapped) {
                    currentUnit.move();
                }
            }
            // in queue units
            else {
                if (currentUnit.moveX + currentUnit.image.naturalWidth < inFrontUnit.moveX) {
                    currentUnit.isMoving = true;
                    currentUnit.move();
                }
                else {
                    currentUnit.isMoving = false;
                    if (!((i == 1 || i == 2) && currentUnit instanceof RangedUnit)) {
                        currentUnit.animator.setAnimationFrameTo(1);
                    }
                }
            }
        }

        return deadCost;
    }

    public void clearAllUnits() {
        for (final Unit unit : this.units) {
            Renderer.removeComponents(unit.image, unit.healthBar, unit.healthBarInner);
        }

        for (final Turret turret : this.turrets) {
            Renderer.removeComponents(turret.image);
        }

        this.deploymentQueue.clear();
        this.units.clear();
        this.turrets.clear();
    }

    public void setup() {
        this.cash = 400;
        this.xp = 0;
        this.deploymentDelay = 0;
        this.ultimateDelay = Player.ULTIMATE_LOADING_DELAY;
        this.stronghold.setEra(this.era = 1);
    }

    // static methods
    private static void queuedRangedUnitAttack(final Player player, final GameObject toAttackUnit) {
        final Unit u2 = player.units.size() > 1 ? player.units.get(1) : null;
        final Unit u3 = player.units.size() > 2 ? player.units.get(2) : null;

        if (u2 instanceof RangedUnit && !u2.isMoving) u2.attack(toAttackUnit);
        if (u3 instanceof RangedUnit && !u3.isMoving) u3.attack(toAttackUnit);
    }

    //refractor
    public static void update(final Player playerL, final Player playerR) {
        // check overlapping
        boolean isOverlapped = false;
        if (playerL.units.size() > 0 && playerR.units.size() > 0) {
            final Unit ul1 = playerL.units.get(0);
            final Unit ur1 = playerR.units.get(0);

            isOverlapped = ul1.image.getTransform().x + ul1.image.naturalWidth > ur1.image.getTransform().x;

            // for front unit
            if (isOverlapped) {
                ul1.attack(ur1);
                ur1.attack(ul1);

                Player.queuedRangedUnitAttack(playerL, ur1);
                Player.queuedRangedUnitAttack(playerR, ul1);
            }
        }
        else if (playerL.units.size() != 0 || playerR.units.size() != 0) {
            final Player attacker = playerL.units.size() != 0 ? playerL : playerR;
            final Player defender = playerL.units.size() != 0 ? playerR : playerL;
            final Unit u = attacker.units.get(0);

            if (u.isReachedMax()) {
                if (u.attack(defender.stronghold)) {
                    attacker.xp += (int)(Stronghold.getMaxHealth(defender.era) * Math.random() * 0.01f);
                    attacker.cash += (int)(Stronghold.getMaxHealth(defender.era) * Math.random() * 0.1f);
                }

                Player.queuedRangedUnitAttack(attacker, defender.stronghold);
            }
        }

        // turret firing
        for (final Turret turret : playerL.turrets) {
            if (playerR.units.size() > 0) turret.attack(playerR.units.get(0));
        }

        for (final Turret turret : playerR.turrets) {
            if (playerL.units.size() > 0) turret.attack(playerL.units.get(0));
        }

        // playerL
        playerR.updateAfter(playerL.updateUnits(isOverlapped));

        // playerR
        playerL.updateAfter(playerR.updateUnits(isOverlapped));
    }
}