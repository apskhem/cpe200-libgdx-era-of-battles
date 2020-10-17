package coma.game;

import java.util.ArrayList;

public class Ultimate {

    public final ArrayList<UltimateObj> nUltimate = new ArrayList<>();

    //constant
    private static short ULTIMATE_SPAWN_POS_Y = 600;
    private static short ULTIMATE_EXPLODE_POS_Y = 0;
    private static short ULTIMATE_SPAWN_POS_XMIN = 0;
    private static short ULTIMATE_SPAWN_POS_XMAX = 960;

    public Ultimate(){
        int maxSize = (int)Mathf.CalRange(10,15);

        if(nUltimate.size() <= maxSize){
            spawnObject((byte)1);

            //this.nUltimate.add();
        }

    }

    public void spawnObject(byte era){

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
        if(vectorY == 0){
            //explode & send damage to in-area opponent troops
            //era 1 explode
            //era 2 more arrow
            //era 3 explode
            //era 4 laser beam
        }
    }


}
