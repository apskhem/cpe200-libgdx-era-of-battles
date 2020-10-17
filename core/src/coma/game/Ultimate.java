package coma.game;

import java.util.ArrayList;

public class Ultimate {

    public final ArrayList<UltimateObj> ultimateContainer = new ArrayList<>();

    //constant
    public static short SPAWN_POS_Y = 600;
    public static short EXPLODE_POS_Y = 0;
    public static short SPAWN_POS_XMIN = 0;
    public static short SPAWN_POS_XMAX = 960;

    public Player target;
    public Player caller;

    public Ultimate() {
        int maxSize = Mathf.CalRange(10,15);

        if(ultimateContainer.size() <= maxSize) {
            new UltimateObj((byte) 1);

            //this.nUltimate.add();
        }
    }

    public void ContinueUltimate() {
        if (this.target == null && this.caller == null) return;

        final ArrayList<UltimateObj> explodedUltimateObjects = new ArrayList<>();

        // new distance calculation and checking explosion
        for (final UltimateObj obj : this.ultimateContainer) {
            if (obj.y < Ultimate.EXPLODE_POS_Y) {
                obj.ultimateExplode();

                //++ DO SOMETHING WITH TARGET

                explodedUltimateObjects.add(obj);
            }
            else {
                obj.ultimateMove();
            }
        }

        // remove all exploded objs
        this.ultimateContainer.removeAll(explodedUltimateObjects);

        // destroy ultimate caller zero objs are in container
        if (this.ultimateContainer.size() == 0) this.caller.ultimateCaller = null;
    }
}

class UltimateObj extends GameObject{

    //constant
    public final float speedX;
    public final float speedY;
    public float x;
    public float y = Ultimate.SPAWN_POS_Y;
    public short damage;
    public short era;

    public UltimateObj(byte era) {
        super(MainGame.ultimateImages[era].Clone());
    }

    public void ultimateMove() {

    }

    public void ultimateExplode() {
        //explode & send damage to in-area opponent troops
        //era 1 explode
        //era 2 more arrow
        //era 3 explode
        //era 4 laser beam
    }
}