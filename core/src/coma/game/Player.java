package coma.game;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Player {

    public final Stronghold stronghold = new Stronghold();
    public final ArrayList<Unit> units = new ArrayList();

    // constants
    private final short SPAWN_POSITION_X;
    private final short SPAWN_POSITION_Y = 52;

    // properties
    public int cash;
    public int xp;
    public byte era = 1;

    // for bot
    public boolean isBot;
    public byte difficulty = 1;
    public boolean isWaking = false;
    public byte spawnDelay = 120;

    // static
    public static final short LEFT_STRONGHOLD_POSITION_X = -80;
    public static final short RIGHT_STRONGHOLD_POSITION_X = 1760;
    public static final short STRONGHOLD_POSITION_Y = 20;

    public Player(boolean isBot) {
        this.isBot = isBot;

        this.stronghold.SetEra1();

        if (isBot) {
            this.SPAWN_POSITION_X = Player.RIGHT_STRONGHOLD_POSITION_X + 120;
            this.stronghold.image.src.flip(true, false);
            this.stronghold.image.src.setPosition(Player.RIGHT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
        }
        else {
            this.SPAWN_POSITION_X = 120;
            this.stronghold.image.src.setPosition(Player.LEFT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
        }
    }

    public void SpawnMeleeUnit() {
        final MeleeUnit u = MeleeUnit.GetEra(this.era);

        if (this.cash >= u.cost) {
            this.cash -= u.cost;
            if (isBot) u.SetFlip(true);

            this.units.add(u);
            u.SetPosition(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);
            Renderer.AddComponents(u.image, u.healthBar, u.healthBarInner);

            if (!this.isBot) Unit.unitCall.play();
        }
    }

    public void SpawnRangedUnit() {
        final RangedUnit u = RangedUnit.GetEra(this.era);

        if (this.cash >= u.cost) {
            this.cash -= u.cost;
            if (isBot) u.SetFlip(true);

            this.units.add(u);
            u.SetPosition(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);
            Renderer.AddComponents(u.image, u.healthBar, u.healthBarInner);

            if (!this.isBot) Unit.unitCall.play();
        }
    }

    public void SpawnCavalryUnit() {

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

    // bot methods
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

        if (this.spawnDelay < 4) {

            if (this.units.size() < 5) {
                this.SpawnMeleeUnit();
            }

            this.spawnDelay = 120;
        }
        else {
            this.spawnDelay -= 1;
        }
    }

    private void Level2Automation() {
        if (!this.isWaking) return;

        if (this.spawnDelay < 4) {
            if (this.units.size() < 5) {
                int rand = (int)(Math.random() * 100);

                if (rand < 50) {
                    this.SpawnRangedUnit();
                }
                else {
                    this.SpawnMeleeUnit();
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

        if (this.spawnDelay < 4) {
            if (this.units.size() < 5) {
                this.SpawnMeleeUnit();
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

    private static void QueuedRangedUnitAttack(Player player, GameObject toAttackUnit) {
        final Unit ul2 = player.units.size() > 1 ? player.units.get(1) : null;
        final Unit ul3 = player.units.size() > 2 ? player.units.get(2) : null;

        if (ul2 instanceof RangedUnit) ul2.Attack(toAttackUnit);
        if (ul3 instanceof RangedUnit) ul3.Attack(toAttackUnit);
    }

    // static method(s)
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
                if (l.Attack(playerR.stronghold)) playerL.xp += (int)(playerL.stronghold.maxHealth * Math.random() * 0.01f);

                Player.QueuedRangedUnitAttack(playerL, playerR.stronghold);
            }
        }
        else if (playerR.units.size() != 0) {
            final Unit r = playerR.units.get(0);

            if (r.IsReachedMax()) {
                if (r.Attack(playerL.stronghold)) playerR.xp += (int)(playerL.stronghold.maxHealth * Math.random() * 0.03f);

                Player.QueuedRangedUnitAttack(playerR, playerL.stronghold);
            }
        }

        // playerL
        playerR.UpdateAfter(playerL.UpdateUnits(isOverlapped));

        // playerR
        playerL.UpdateAfter(playerR.UpdateUnits(isOverlapped));
    }
}
