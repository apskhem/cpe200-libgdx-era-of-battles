package coma.game;

import java.util.ArrayList;

public class Player {

    public final Stronghold stronghold = new Stronghold();
    public final ArrayList<Unit> units = new ArrayList();
    private final Renderer r;

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

    public Player(Renderer r, boolean isBot) {
        this.r = r;
        this.isBot = isBot;

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
        final MeleeUnit u = MeleeUnit.GetEra((byte) 1);

        if (this.cash >= u.cost) {
            this.cash -= u.cost;
            if (isBot) u.SetFlip(true);

            this.units.add(u);
            u.image.SetPosition(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);

            r.AddComponents(u.image);
        }
    }

    public void SpawnRangedUnit() {

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
            final Unit unit = this.units.get(i);

            if (unit.health < 0) {
                deadCost += unit.cost;

                unit.Destroy();
                this.units.remove(unit);
                r.RemoveComponents(unit.image);
            }
        }

        // moving
        for (int i = 0; i < this.units.size(); i++) {
            final Unit currentUnit = this.units.get(i);
            final Unit inFrontUnit = i == 0 ? null : this.units.get(i - 1);

            if (inFrontUnit == null) {
                if (!isOverlapped) {
                    currentUnit.Move();
                }
            }
            else {
                if (currentUnit.moveX + currentUnit.image.src.getWidth() < inFrontUnit.moveX) {
                    currentUnit.Move();
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
                this.SpawnMeleeUnit();
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

    // static method(s)
    public static void Update(Player playerL, Player playerR) {
        // check overlapping
        boolean isOverlapped = false;
        if (playerL.units.size() != 0 && playerR.units.size() != 0) {
            final Unit l = playerL.units.get(0);
            final Unit r = playerR.units.get(0);

            isOverlapped = l.image.src.getX() + l.image.src.getWidth() / 2 > r.image.src.getX();

            // for unit
            if (isOverlapped) {
                l.Attack(r);
                r.Attack(l);
            }
        }
        else if (playerL.units.size() != 0) {
            final Unit l = playerL.units.get(0);

            if (l.IsReachedMax()) l.Attack(playerR.stronghold);
        }
        else if (playerR.units.size() != 0) {
            final Unit r = playerR.units.get(0);

            if (r.IsReachedMax()) r.Attack(playerL.stronghold);
        }

        // playerL
        playerR.UpdateAfter(playerL.UpdateUnits(isOverlapped));

        // playerR
        playerL.UpdateAfter(playerR.UpdateUnits(isOverlapped));
    }
}
