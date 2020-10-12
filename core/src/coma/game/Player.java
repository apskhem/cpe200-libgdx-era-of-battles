package coma.game;

import com.badlogic.gdx.utils.Queue;

import java.util.ArrayList;

public class Player {

    public final Stronghold stronghold = new Stronghold();
    public final ArrayList<Unit> units = new ArrayList<>();
    public final Queue<Unit> deploymentQueue = new Queue<>();

    // constants
    private final short SPAWN_POSITION_X;
    protected final short SPAWN_POSITION_Y = 52;

    // properties
    public int cash;
    public int xp;
    public byte era = 1;
    public short deploymentCooldown;
    public short ultimateCooldown = Player.ULTIMATE_COOLDOWN;

    // static
    public static final short LEFT_STRONGHOLD_POSITION_X = -80;
    public static final short RIGHT_STRONGHOLD_POSITION_X = 1760;
    public static final byte STRONGHOLD_POSITION_Y = 20;
    public static final byte MAX_UNIT = 10;
    public static final short ULTIMATE_COOLDOWN = 4000;

    public Player() {
        this.stronghold.SetEra(this.era);
        this.SPAWN_POSITION_X = 120;
        this.stronghold.image.SetPosition(Player.LEFT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
    }

    public void DeployUnit(Unit u) {
        if (u == null) return;

        if (this.cash >= u.cost && this.units.size() + this.deploymentQueue.size < Player.MAX_UNIT) {
            this.cash -= u.cost;

            if (this.deploymentQueue.size == 0) this.deploymentCooldown = u.GetDeploymentCooldown();

            this.deploymentQueue.addLast(u);
        }
    }

    public void SpawnUnit(Unit u) {
        if (u == null) return;

        this.units.add(u);
        u.SetPosition(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);
        Renderer.AddComponents(u.image, u.healthBar, u.healthBarInner);

        Unit.unitCall.play();
    }

    public void ProcessUnitDeployment() {
        if (this.deploymentQueue.size == 0) return;

        if (this.deploymentCooldown == 0) {
            final Unit u = this.deploymentQueue.removeFirst();

            this.SpawnUnit(u);

            if (this.deploymentQueue.size > 0) this.deploymentCooldown = this.deploymentQueue.first().GetDeploymentCooldown();
        }
        else {
            this.deploymentCooldown -= 1;
        }
    }

    public void BuildTurret() {

    }

    public void UpgradeStronghold() {

    }

    public void UseUltimate() {

    }

    public void UpdateAfter(int rawCost) {
        this.cash += (int)(rawCost * 0.8);
        this.xp += (int)(rawCost * Math.random() * 0.3f + rawCost * 0.05f);
    }

    // automation looping
    public int UpdateUnits(boolean isOverlapped) {
        // check dead units
        int deadCost = 0;
        for (int i = 0; i < this.units.size(); i++) {
            final Unit u = this.units.get(i);

            // destroy
            if (u.health < 0) {
                deadCost += u.cost;

                this.units.remove(u);
                u.SetAnimationStateTo(7);
                Renderer.RemoveComponents(u.healthBar, u.healthBarInner);
                Unit.deadUnits.add(u);

                Unit.meleeDie1.play();
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
                if (currentUnit.moveX + currentUnit.image.src.getWidth() < inFrontUnit.moveX) {
                    currentUnit.Move();
                }
                else {
                    if (!((i == 1 || i == 2) && currentUnit instanceof RangedUnit)) {
                        currentUnit.SetAnimationStateTo(1);
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

        this.deploymentQueue.clear();
        this.units.clear();
    }

    public void Setup() {
        this.cash = 500;
        this.xp = 0;
        this.deploymentCooldown = 0;
        this.ultimateCooldown = Player.ULTIMATE_COOLDOWN;
        this.stronghold.SetEra(this.era = 1);
    }

    // static methods
    private static void QueuedRangedUnitAttack(Player player, GameObject toAttackUnit) {
        final Unit ul2 = player.units.size() > 1 ? player.units.get(1) : null;
        final Unit ul3 = player.units.size() > 2 ? player.units.get(2) : null;

        if (ul2 instanceof RangedUnit) ul2.Attack(toAttackUnit);
        if (ul3 instanceof RangedUnit) ul3.Attack(toAttackUnit);
    }

    public static void Update(Player playerL, Player playerR) {
        // check overlapping
        boolean isOverlapped = false;
        if (playerL.units.size() > 0 && playerR.units.size() > 0) {
            final Unit ul1 = playerL.units.get(0);
            final Unit ur1 = playerR.units.get(0);

            isOverlapped = ul1.image.src.getX() + ul1.image.src.getWidth() / 1.2 > ur1.image.src.getX();

            // for front unit
            if (isOverlapped) {
                ul1.Attack(ur1);
                ur1.Attack(ul1);

                Player.QueuedRangedUnitAttack(playerL, ur1);
                Player.QueuedRangedUnitAttack(playerR, ul1);
            }
        }
        else if (playerL.units.size() != 0) {
            final Unit l = playerL.units.get(0);

            if (l.IsReachedMax()) {
                if (l.Attack(playerR.stronghold)) playerL.xp += (int)(playerL.stronghold.GetMaxHealth(playerL.era) * Math.random() * 0.01f);

                Player.QueuedRangedUnitAttack(playerL, playerR.stronghold);
            }
        }
        else if (playerR.units.size() != 0) {
            final Unit r = playerR.units.get(0);

            if (r.IsReachedMax()) {
                if (r.Attack(playerL.stronghold)) playerR.xp += (int)(playerL.stronghold.GetMaxHealth(playerL.era) * Math.random() * 0.03f);

                Player.QueuedRangedUnitAttack(playerR, playerL.stronghold);
            }
        }

        // unit deployment
        playerR.ProcessUnitDeployment();
        playerL.ProcessUnitDeployment();

        // ultimate cooldown
        if (playerL.ultimateCooldown > 0) playerL.ultimateCooldown -= 1;
        if (playerR.ultimateCooldown > 0) playerR.ultimateCooldown -= 1;

        // playerL
        playerR.UpdateAfter(playerL.UpdateUnits(isOverlapped));

        // playerR
        playerL.UpdateAfter(playerR.UpdateUnits(isOverlapped));
    }
}

class GameBot extends Player {

    // constants
    private final short SPAWN_POSITION_X;

    public byte difficulty = 1;
    public boolean isWaking = false;
    public byte spawnDelay = 120;

    public GameBot() {
        this.SPAWN_POSITION_X = Player.RIGHT_STRONGHOLD_POSITION_X + 120;
        this.stronghold.image.src.flip(true, false);
        this.stronghold.image.SetPosition(Player.RIGHT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
    }

    @Override
    public void SpawnUnit(Unit u) {
        if (u == null) return;

        u.SetFlip(true);

        this.units.add(u);
        u.SetPosition(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);
        Renderer.AddComponents(u.image, u.healthBar, u.healthBarInner);
    }

    @Override
    public void UpdateAfter(int rawCost) {
        switch (this.difficulty) {
            case 1: {
                this.cash += (int)(rawCost * 1.5);
                this.xp += (int)(rawCost * Math.random() * 0.3f + rawCost * 0.05f);
            } break;
            case 2: {
                this.cash += (int)(rawCost * 2.2);
                this.xp += (int)(rawCost * Math.random() * 0.4f + rawCost * 0.06f);
            } break;
            case 3: {
                this.cash += (int)(rawCost * 3.1);
                this.xp += (int)(rawCost * Math.random() * 0.5f + rawCost * 0.07f);
            } break;
        }
    }

    public void Awake() {
        this.isWaking = true;

        switch (this.difficulty) {
            case 1: this.Level1Automation(); break;
            case 2: this.Level2Automation(); break;
            case 3: this.Level3Automation(); break;
        }
    }

    private void Level1Automation() {
        if (!this.isWaking) return;

        if (this.spawnDelay < 0) {
            if (this.units.size() < Player.MAX_UNIT) {
                this.DeployUnit(MeleeUnit.GetEra(this.era));
            }

            this.spawnDelay = 120;
        }
        else {
            this.spawnDelay -= 1;
        }
    }

    private void Level2Automation() {
        if (!this.isWaking) return;

        if (this.spawnDelay < 0) {
            if (this.units.size() < Player.MAX_UNIT) {
                int rand = (int)(Math.random() * 100);

                if (rand < 50) {
                    this.DeployUnit(RangedUnit.GetEra(this.era));
                }
                else {
                    this.DeployUnit(MeleeUnit.GetEra(this.era));
                }
            }

            this.spawnDelay = 120;
        }
        else {
            this.spawnDelay -= 1;
        }
    }

    private void Level3Automation() {
        if (!this.isWaking) return;

        if (this.spawnDelay < 0) {
            if (this.units.size() < Player.MAX_UNIT) {
                this.DeployUnit(MeleeUnit.GetEra(this.era));
            }

            this.spawnDelay = 120;
        }
        else {
            this.spawnDelay -= 1;
        }
    }

    public void Halt() {
        this.isWaking = false;
    }
}
