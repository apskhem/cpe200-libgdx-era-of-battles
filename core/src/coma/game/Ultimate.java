package coma.game;

import java.util.ArrayList;

public class Ultimate {

    public final ArrayList<UltimateObj> ultimateContainer = new ArrayList<>();

    //constant
    private static short ULTIMATE_SPAWN_POS_Y = 600;
    private static short ULTIMATE_EXPLODE_POS_Y = 0;
    private static short ULTIMATE_SPAWN_POS_XMIN = 0;
    private static short ULTIMATE_SPAWN_POS_XMAX = 960;

    public Player target;
    public Player caller;
    public final float speedY;
    public final float speedX;
    public float x;
    public float y;

    public Ultimate() {
        int maxSize = Mathf.CalRange(10,15);

        if(ultimateContainer.size() <= maxSize) {
            new UltimateObj((byte) 1);

            //this.nUltimate.add();
        }
    }

    public void ContinueUltimate() {
        if (this.target == null) return;

        final ArrayList<UltimateObj> explodedUltimateObjects = new ArrayList<>();

        // new distance calculation and checking explosion
        for (final UltimateObj obj : this.ultimateContainer) {
            if (this.y < 0) {
                obj.ultimateExplode();

                //++ DO SOMETHING WITH TARGET

                explodedUltimateObjects.add(obj);
            }
            else {
                this.x += this.speedX;
                this.y += this.speedY;
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
    private final short vectorY = -600;

    short damage;

    public UltimateObj(byte era){
        super(MainGame.ultimateImages[era].Clone());
    }

    public void ultimateMove(){

    }

    public void ultimateExplode(){
        if (vectorY == 0){
            //explode & send damage to in-area opponent troops
            //era 1 explode
            //era 2 more arrow
            //era 3 explode
            //era 4 laser beam
        }
    }
}