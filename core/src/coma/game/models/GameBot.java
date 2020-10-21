package coma.game.models;

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
            case 1: return 5000;
            case 2: return 4500;
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
    public void UseUltimate() {
        if (this.ultimateDelay <= 0) {
            this.ultimateCaller = new Ultimate(this.era, true);
            this.ultimateDelay = GameBot.getUltimateDelay(this.difficulty);
        }
    }

    @Override
    public void UpgradeStronghold() {
        if (this.era < 4 && this.xp >= Stronghold.GetRequiredXp(this.era)) {
            this.era++;

            this.stronghold.UpgradeTo(this.era);
        }
    }

    @Override
    public void UpdateAfter(final int rawCost) {
        this.cash += (int)(rawCost * (1.0f + 0.5f * this.difficulty));
        this.xp += (int)(rawCost * Math.random() * (0.2f + 0.1f * this.difficulty) + rawCost * (0.05f + 0.01 * this.difficulty));
    }

    public void Awake() {
        if (!this.isWaking) {
            this.ultimateDelay = GameBot.getUltimateDelay(this.difficulty);
            this.isWaking = true;
        }

        if (this.decisionDelay < 0) {
            this.break322();

            switch (this.CalculatedDecisionState()) { // << old: this.diffulty
                case 1: this.Level1Automation(); break;
                case 2: this.Level2Automation(); break;
                case 3: this.Level3Automation(); break;
            }

            this.CalculateTurretSetting();
            this.UpgradeStronghold();
            if (MainGame.user.units.size() >= 3) this.UseUltimate();

            this.decisionDelay = GameBot.DECISION_DELAY;
        }
        else {
            this.decisionDelay -= MainGame.deltaTime;
        }
    }

    public byte CalculatedDecisionState() {

        if (this.cash >= CavalryUnit.stats[this.era - 1][3] * 5) state = 3;
        else if (this.cash >= CavalryUnit.stats[this.era - 1][3] * 3) state = 2;
        else state = 1;

        return this.state;
    }

    private void Level1Automation() {
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        if (this.units.size() < Player.MAX_UNIT) {

            int idx = Mathf.CalRange(0,100);      // random idx for choosing unit

            if (IsMeleeInFront()){
                this.CalculateBotStrategy(idx);
            }
            else{
                if (idx >= 0 && idx < 40) {
                    this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
                }
                else if (idx >= 40 && idx < 80) {
                    this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                }
                else {
                    this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
                }
            }
        }
    }

    private void Level2Automation() {
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        if (this.units.size() < Player.MAX_UNIT) {
            int idx = (int)(Math.random() * 100);      // random idx for choosing unit

            if (IsMeleeInFront()) {
                this.CalculateBotStrategy(idx);
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

    private void Level3Automation() {
        if (!this.isWaking) return;

        // game bot decision fired >> write decision commands here
        int idx = (int)(Math.random() * 100);      // random idx for choosing unit

        if (IsMeleeInFront()) {
            this.CalculateBotStrategy(idx);
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

    private boolean IsMeleeInFront() {
        if (this.units.size() > 0) {
            final Unit lastU = this.units.get(this.units.size() - 1);

            return lastU instanceof RangedUnit;
        }

        return true;
    }

    private void break322() {
        for(int i = 0; i < MainGame.user.units.size() - 2 ; i++){
            Unit tank = MainGame.user.units.get(i);
            Unit ranged1 = MainGame.user.units.get(i + 1);
            Unit ranged2 = MainGame.user.units.get(i + 2);

            if ((tank instanceof CavalryUnit || tank instanceof MeleeUnit)
                    && ranged1 instanceof RangedUnit
                    && ranged2 instanceof RangedUnit){

                this.DeployUnit(new CavalryUnit(this.era, MeleeUnit.stats[this.era - 1]));
                this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
                this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
            }

            else return;
        }
    }

    private void CalculateBotStrategy(final int idx) {
        if (idx >= 0 && idx < 35) {
            this.DeployUnit(new MeleeUnit(this.era, MeleeUnit.stats[this.era - 1]));
        }
        else if (idx >= 35 && idx < 70) {
            this.DeployUnit(new CavalryUnit(this.era, CavalryUnit.stats[this.era - 1]));
        }
        else {
            this.DeployUnit(new RangedUnit(this.era, RangedUnit.stats[this.era - 1]));
        }
    }

    private void CalculateTurretSetting() {
        if (!this.isWaking) return;

        if (this.units.size() > 4) {
            // unit must be > 4 and cash is enough for build turret and spawn troops in the future
            if (this.cash >= Turret.GetEra(this.era).cost * (3f / this.difficulty) * 2)
                this.BuildTurret(Turret.GetEra(this.era));
        }
    }

    public void Halt() {
        this.isWaking = false;
    }
}
