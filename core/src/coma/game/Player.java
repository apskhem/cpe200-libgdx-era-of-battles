package coma.game;

import com.badlogic.gdx.utils.Queue;

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

    public void DeployUnit(Unit u) {
        if (u == null) return;

        if (this.cash >= u.cost && this.units.size() + this.deploymentQueue.size < Player.MAX_UNIT) {
            this.cash -= u.cost;

            if (this.deploymentQueue.size == 0) this.deploymentDelay = u.GetDeploymentDelay();

            this.deploymentQueue.addLast(u);
        }
    }

    public void SpawnUnit(Unit u) {
        if (u == null) return;

        this.units.add(u);
        u.SetPosition(this.SPAWN_POSITION_X, this.SPAWN_POSITION_Y);
        Renderer.AddComponents(u.image, u.healthBar, u.healthBarInner);

        MainGame.unitCall.play();
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

    public void BuildTurret(Turret t) {
        if (t == null) return;

        if (this.cash >= t.cost && this.turrets.size() < 2) {
            this.cash -= t.cost;
            this.turrets.add(t);

            t.image.SetPosition(65, this.turrets.size() == 1 ? 260 : 340);
            Renderer.AddComponents(t.image);

            MainGame.unitCall.play();
        }
    }

    public void UpgradeStronghold() {
        if (this.era < 4 && this.xp >= Stronghold.GetRequiredXp((byte)(this.era + 1))) {
            this.era++;

            this.stronghold.UpgradeTo(this.era);
        }
    }

    public void UseUltimate() {
        if (this.ultimateDelay <= 0) {
            this.ultimateCaller = new Ultimate(this.era);
            this.ultimateDelay = Player.ULTIMATE_LOADING_DELAY;
        }
    }

    public void UpdateAfter(int rawCost) {
        this.cash += (int)(rawCost * 0.8);
        this.xp += (int)(rawCost * Math.random() * 0.3f + rawCost * 0.05f);
    }

    // automation looping
    public int UpdateUnits(boolean isOverlapped) {
        // update overall
        this.ProcessUnitDeployment();

        if (this.ultimateDelay > 0) this.ultimateDelay -= MainGame.deltaTime;

        if (this.ultimateCaller != null) this.ultimateCaller.ContinueUltimate();

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

                MainGame.meleeDie1.play();

                if (u instanceof CavalryUnit && u.era == 1) {
                    MainGame.cavalryDie1.setVolume(MainGame.cavalryDie1.play(), 0.3f);
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

        for (final Turret turret : this.turrets) {
            Renderer.RemoveComponents(turret.image);
        }

        this.deploymentQueue.clear();
        this.units.clear();
        this.turrets.clear();
    }

    public void Setup() {
        this.cash = 500;
        this.xp = 0;
        this.deploymentDelay = 0;
        this.ultimateDelay = Player.ULTIMATE_LOADING_DELAY;
        this.stronghold.SetEra(this.era = 1);
    }

    // static methods
    private static void QueuedRangedUnitAttack(Player player, GameObject toAttackUnit) {
        final Unit u2 = player.units.size() > 1 ? player.units.get(1) : null;
        final Unit u3 = player.units.size() > 2 ? player.units.get(2) : null;

        if (u2 instanceof RangedUnit && !u2.isMoving) u2.Attack(toAttackUnit);
        if (u3 instanceof RangedUnit && !u3.isMoving) u3.Attack(toAttackUnit);
    }

    public static void Update(Player playerL, Player playerR) {
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
                    attacker.xp += (int)(defender.stronghold.GetMaxHealth(defender.era) * Math.random() * 0.01f);
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

class GameBot extends Player {

    // constants
    private final short SPAWN_POSITION_X;

    public byte difficulty = 1;
    public boolean isWaking = false;
    public byte decisionDelay = 120;
    public byte state = 1;

    public static final byte DECISION_DELAY = 120;

    public GameBot() {
        this.SPAWN_POSITION_X = Player.RIGHT_STRONGHOLD_POSITION_X + 210;
        this.stronghold.image.FlipHorizontal();
        this.stronghold.image.SetPosition(Player.RIGHT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
    }

    @Override
    public void SpawnUnit(Unit u) {
        if (u == null) return;

        u.image.FlipHorizontal();

        this.units.add(u);
        u.SetPosition(this.SPAWN_POSITION_X - u.image.naturalWidth, this.SPAWN_POSITION_Y);
        Renderer.AddComponents(u.image, u.healthBar, u.healthBarInner);
    }

    @Override
    public void BuildTurret(Turret t) {
        if (t == null) return;

        if (this.cash >= t.cost && this.turrets.size() < 2) {
            this.cash -= t.cost;
            this.turrets.add(t);

            t.image.FlipHorizontal();
            t.image.SetPosition(1935, this.turrets.size() == 1 ? 260 : 340);
            Renderer.AddComponents(t.image);
        }
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

        if (this.decisionDelay < 0) {
            switch (this.CalculatedDecisionState()) { // << old: this.diffulty
                case 1: {
                    this.Level1Automation();
                    this.setTurret();
                    this.botUltimate();
                    break;
                }
                case 2: {
                    this.Level2Automation();
                    this.setTurret();
                    this.botUltimate();
                    break;
                }
                case 3: {
                    this.Level3Automation();
                    this.setTurret();
                    this.botUltimate();
                    break;
                }
            }

            this.decisionDelay = GameBot.DECISION_DELAY;
        }
        else {
            this.decisionDelay -= MainGame.deltaTime;
        }
    }

    public byte CalculatedDecisionState() {
        switch (this.era){
            case 1:
                if (this.cash >= 1000) state = 2;
                else if (this.cash >= 0) state = 1;
                else state = 3;
            case 2:
                if(this.cash >= 2000)  state = 2;
                else if (this.cash >= 0) state = 1;
                else state = 3;
            case 3:
                if(this.cash >= 5000) state = 2;
                else if (this.cash >= 0) state = 1;
                else state = 3;
        }

        return this.state;
    }

    private boolean isMeleeInFront() {
        if (this.units.size() > 0) {
            final Unit lastU = this.units.get(this.units.size() - 1);

            return !(lastU instanceof RangedUnit);
        }

        return false;
    }

    private void BotStrategy(int idx){
        if (idx >= 0 && idx < 40) {
            this.DeployUnit(MeleeUnit.GetEra(this.era));
            isMeleeInFront();
        }
        else if (idx >= 40 && idx < 70) {
            this.DeployUnit(RangedUnit.GetEra(this.era));
            isMeleeInFront();
        }
        else {
            this.DeployUnit(CavalryUnit.GetEra(this.era));
            isMeleeInFront();
        }
    }

    private void Level1Automation() {
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        if (this.units.size() < Player.MAX_UNIT) {

            int idx = Mathf.CalRange(0,100);      // random idx for choosing unit

            if(!isMeleeInFront()){
                BotStrategy(idx);
            }
            else{
                if (idx >= 0 && idx < 40) this.DeployUnit(MeleeUnit.GetEra(this.era));
                else if (idx >= 40 && idx < 80) this.DeployUnit(RangedUnit.GetEra(this.era));
                else this.DeployUnit(CavalryUnit.GetEra(this.era));
            }
            isMeleeInFront();
        }
    }

    private void Level2Automation() {
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        if (this.units.size() < Player.MAX_UNIT) {
            int idx = (int)(Math.random() * 100);      // random idx for choosing unit

            if(!isMeleeInFront()){
                BotStrategy(idx);
            }
            else{
                if (idx >= 0 && idx < 35) this.DeployUnit(MeleeUnit.GetEra(this.era));
                else if (idx >= 35 && idx < 70) this.DeployUnit(RangedUnit.GetEra(this.era));
                else this.DeployUnit(CavalryUnit.GetEra(this.era));
            }
            isMeleeInFront();
        }
    }

    private void Level3Automation() {
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        int idx = (int)(Math.random() * 100);      // random idx for choosing unit

        if (!isMeleeInFront()){
            BotStrategy(idx);
        }
        else{
            if (idx >= 0 && idx < 30) this.DeployUnit(MeleeUnit.GetEra(this.era));
            else if (idx >= 30 && idx < 60) this.DeployUnit(RangedUnit.GetEra(this.era));
            else this.DeployUnit(CavalryUnit.GetEra(this.era));
        }
        isMeleeInFront();
    }

    private void setTurret() {
        if (!this.isWaking) return;

        if(this.units.size() > 4) {

            // unit must be > 4 and cash is enough for build turret and spawn troops in the future
            switch (this.era) {
                case 1:
                    if (this.cash >= 1000) this.BuildTurret(Turret.GetEra(this.era)); break;
                case 2:
                    if (this.cash >= 3000) this.BuildTurret(Turret.GetEra(this.era)); break;
                case 3:
                    if (this.cash >= 5000) this.BuildTurret(Turret.GetEra(this.era)); break;
            }
        }
    }

    private void botUltimate() {
        switch(this.difficulty){
            // may be bug in case 1 and 2
            case 1: if (this.ultimateDelay + 1000 <= 0) this.UseUltimate(); break;
            case 2: if (this.ultimateDelay +500 <= 0) this.UseUltimate(); break;
            case 3: if (this.ultimateDelay <= 0) this.UseUltimate(); break;
        }
    }

    public void Halt() {
        this.isWaking = false;
    }
}
