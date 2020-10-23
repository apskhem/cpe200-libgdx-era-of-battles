package coma.game.models;

import com.sun.tools.javac.Main;
import coma.game.MainGame;
import coma.game.Resources;
import coma.game.models.contents.*;
import coma.game.utils.Mathf;
import coma.game.views.Renderer;
import org.w3c.dom.ranges.RangeException;

final public class GameBot extends Player {

    // constants
    private final short SPAWN_POSITION_X;

    public byte difficulty = 1;
    private boolean isWaking = false;
    private byte decisionDelay = 120;
    private byte state = 1;

    public static final byte DECISION_DELAY = 120;

    public GameBot() {
        this.SPAWN_POSITION_X = Player.RIGHT_STRONGHOLD_POSITION_X + 210;
        this.stronghold.image.FlipHorizontal();
        this.stronghold.image.SetPosition(Player.RIGHT_STRONGHOLD_POSITION_X, Player.STRONGHOLD_POSITION_Y);
    }

    public static short getUltimateDelay(final byte difficulty) {
        switch (difficulty) {
            case 1: return 6000;
            case 2: return 5000;
            case 3: return 4000;
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }


    @Override
    public void SpawnUnit(final Unit u) {
        if (u == null) return;

        this.units.add(u);
        u.image.FlipHorizontal();
        u.SpawnAt(this.SPAWN_POSITION_X - u.image.naturalWidth, this.SPAWN_POSITION_Y);
    }

    @Override
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
        for (Turret at : this.turrets) {
            if (at.era < this.era && this.cash >= Turret.GetEra(this.era).cost - at.cost) {
                this.cash -= t.cost - at.cost;

                // replace
                at.ReplaceWith(t);

                Resources.unitCallSound.play();

                return true;
            }
        }

        if (this.cash >= t.cost && this.turrets.size() < 2) {
            this.cash -= t.cost;
            this.turrets.add(t);

            t.image.FlipHorizontal();
            t.image.SetPosition(1935, this.turrets.size() == 1 ? 260 : 340);
            Renderer.AddComponents(t.image);

            return true;
        }

        return false;
    }

    @Override
    public boolean UseUltimate(final Player target) {
        if (target == null) return false;

        if (this.ultimateDelay <= 0) {
            this.ultimateCaller = new Ultimate(this, target,  this.era, true);
            this.ultimateDelay = GameBot.getUltimateDelay(this.difficulty);

            if(this.era == 4) this.time2end++;

            return true;
        }

        return false;
    }

    @Override
    public boolean UpgradeStronghold() {
        if (this.era < 4 && this.xp >= Stronghold.GetRequiredXp(this.era)) {
            this.era++;

            this.stronghold.UpgradeTo(this.era);

            return true;
        }

        return false;
    }

    @Override
    public void UpdateAfter(final int rawCost) {
        switch(this.difficulty){
            case 1:
            case 2:
                this.cash += (int) (rawCost * (1.0f + 0.4f * (this.difficulty / (0.3 * this.era) + this.time2end)));
                System.out.println(this.time2end);
                break;
            case 3:
                this.cash += (int) (rawCost * (1.0f + 0.4f * this.difficulty)); break;
        }

        if(this.era == 4)
            this.xp = Stronghold.GetRequiredXp((byte)4);
        else
            this.xp += (int) (rawCost * Math.random() * (0.2f + 0.1f * this.difficulty) + rawCost * (0.05f + 0.01 * this.difficulty));
    }

    @Override
    public void Setup() {
        this.cash = 400;
        this.xp = 0;
        this.deploymentDelay = 0;
        this.ultimateDelay = getUltimateDelay(this.difficulty);
        this.stronghold.SetEra(this.era = 1);
    }

    public void Awake() {
        // init
        if (!this.isWaking) {
            this.ultimateDelay = GameBot.getUltimateDelay(this.difficulty);
            this.isWaking = true;
        }

        if (this.decisionDelay < 0) {

            this.CounterPlayerTactic();
            this.CalculateTurretSetting();
            this.UpgradeStronghold();

            if(MainGame.user.ultimateDelay <= 1000) {
                if (this.units.size() + this.deploymentQueue.size <= MainGame.user.units.size() + (3 - this.difficulty)) // prevent ultimate clear field
                    BotDecision();
            }
            else BotDecision();

            if (MainGame.user.units.size() >= 3 || isBaseHit()) this.UseUltimate(MainGame.user);

            this.decisionDelay = GameBot.DECISION_DELAY;
        }
        else {
            this.decisionDelay -= MainGame.deltaTime;
        }
    }

    private boolean isBaseHit(){
        if(MainGame.user.units.size() > 1) {
            final Unit spy = MainGame.user.units.get(0);
            return spy.IsReachedMax();
        }
        else return false;
    }

    private void BotDecision() {
        switch (this.GetCalculatedDecisionState()) { // << old: this.diffulty
            case 1:
                this.Level1Automation();
                break;
            case 2:
                this.Level2Automation();
                break;
            case 3:
                this.Level3Automation();
                break;
        }
    }

    public byte GetCalculatedDecisionState() {

        if (this.cash >= CavalryUnit.stats[this.era - 1][3] * 5) state = 3;
        else if (this.cash >= CavalryUnit.stats[this.era - 1][3] * 3) state = 2;
        else state = 1;

        return this.state;
    }

    private void Level1Automation() { //less money
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        if (this.units.size() < Player.MAX_UNIT) {

            int idx = Mathf.CalRange(0,100);      // random idx for choosing unit

            if (this.HasMeleeInFront()) {
                this.BotStrategy(idx);
            }
            else{
                if (idx >= 0 && idx < 45) {
                    this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
                }
                else if (idx >= 45 && idx < 90) {
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
                else {
                    this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
                }
            }
        }
    }

    private void Level2Automation() { // fair money
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        if (this.units.size() < Player.MAX_UNIT) {
            int idx = (int)(Math.random() * 100);      // random idx for choosing unit

            if (this.HasMeleeInFront()) {
                this.BotStrategy(idx);
            }
            else{
                if (idx >= 0 && idx < 35) {
                    this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
                }
                else if (idx >= 35 && idx < 70) {
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
                else {
                    this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
                }
            }
        }
    }

    private void Level3Automation() { //more money
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        int idx = (int)(Math.random() * 100);      // random idx for choosing unit

        if (this.HasMeleeInFront()) {
            this.BotStrategy(idx);
        }
        else {
            if (idx >= 0 && idx < 30) {
                this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
            }
            else if (idx >= 30 && idx < 60) {
                this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
            }
            else {
                this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
            }
        }
    }

    private boolean HasMeleeInFront() {
        if (this.units.size() > 1) {
            final Unit lastU = this.units.get(this.units.size() - 1);
            final Unit secondLastU = this.units.get(this.units.size() - 2);

            return !((lastU instanceof RangedUnit) || (secondLastU instanceof RangedUnit));
        }

        return false;
    }

    private void CounterPlayerTactic() { // '322 Fn Code'
        for(int i = 0; i < MainGame.user.units.size() - 2 ; i++){
            Unit tank = MainGame.user.units.get(i);
            Unit ranged1 = MainGame.user.units.get(i + 1);
            Unit ranged2 = MainGame.user.units.get(i + 2);

            if ((tank instanceof CavalryUnit || tank instanceof MeleeUnit)
                    && ranged1 instanceof RangedUnit
                    && ranged2 instanceof RangedUnit){

                if(this.HasMeleeInFront()) {   // not wasting resources for melee infront
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
                else{
                    int idx = Mathf.CalRange(0,20);

                    if(idx < 10)
                        this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era -1]));
                    else
                        this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era -1]));

                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
            }

            else return;
        }
    }

    private void BotStrategy(final int rand) {
        switch (this.GetCalculatedDecisionState()){
            case 1 :{
                if (rand >= 0 && rand < 50) {
                    this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
                }
                else if (rand >= 50 && rand < 80) {
                    this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
                }
                else {
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
            }
            case 2 :{
                if (rand >= 0 && rand < 40) {
                    this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
                }
                else if (rand >= 40 && rand < 80) {
                    this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
                }
                else {
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
            }
            case 3 :{
                if (rand >= 0 && rand < 50) {
                    this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
                }
                else if (rand >= 50 && rand < 80) {
                    this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
                }
                else {
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
            }
        }
    }

    private void CalculateTurretSetting() {
        if (!this.isWaking) return;

        if (this.units.size() > 3) {
            // unit must be > 4 and cash is enough for build turret and spawn troops in the future
            if (this.cash >= Turret.GetEra(this.era).cost + CavalryUnit.stats[this.era - 1][3] * (3 - this.difficulty)) {
                this.BuildTurret(Turret.GetEra(this.era));
            }
        }
    }

    public void Halt() {
        this.isWaking = false;
    }
}
