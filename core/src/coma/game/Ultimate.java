package coma.game;

import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;

public class Ultimate {

    public final ArrayList<UltimateObj> ultimateSpawnContainer = new ArrayList<>();

    //constant
    public static short SPAWN_POS_Y = 550;  //for era 3 plane around 530
    public static short EXPLODE_POS_Y = 50;
    public static short SPAWN_POS_XMIN = 0;
    public static short SPAWN_POS_XMAX = 960;

    public Player target;
    public Player caller;

    public Ultimate(byte era) {
        int n;
        switch (era) {
            case 1: n = Mathf.CalRange(10, 15); break;
            case 2: n = Mathf.CalRange(25, 30); break;
            case 3: {
                n = Mathf.CalRange(15, 25);

//                UltimateObj plane = new UltimateObj();
//                plane.SetPosition(0,Ultimate.SPAWN_POS_Y);
//                Renderer.AddComponents(plane.image);

            } break;
            case 4: n = Mathf.CalRange(30, 35); break;
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }

        for (int i = 0; i < n; i++) {
            UltimateObj obj = new UltimateObj(era);
            this.SpawnUltimateObj(obj);
            this.ultimateSpawnContainer.add(obj);
        }
    }

    private void SpawnUltimateObj(UltimateObj obj){
        if(obj == null) return;

        int x = Mathf.CalRange(240, 1760); //border of x-axis screen

        obj.SetPosition(x, SPAWN_POS_Y);
        Renderer.AddComponents(obj.image);

        // MainGame  if อยากใส่เพลง
    }

    public void ContinueUltimate() {
        if (this.target == null && this.caller == null) return;

        final ArrayList<UltimateObj> explodedUltimateObjects = new ArrayList<>();

        // new distance calculation and checking explosion
        for (final UltimateObj obj : this.ultimateSpawnContainer) {
            if (obj.movedDistanceY < Ultimate.EXPLODE_POS_Y) {
                obj.ultimateExplode((byte)obj.era);
                Renderer.RemoveComponents(obj.image);

                //++ DO SOMETHING WITH TARGET

                explodedUltimateObjects.add(obj);
            }
            else {
                obj.ultimateMove();
            }
        }

        // remove all exploded obj
        this.ultimateSpawnContainer.removeAll(explodedUltimateObjects);


        // destroy ultimate caller zero objs are in container
        if (this.ultimateSpawnContainer.size() == 0) this.caller.ultimateCaller = null;
    }
}

class UltimateObj extends GameObject{

//    //constant
//    public final float speedX;
//    public final float speedY;
//    public float x;
//    public float y = Ultimate.SPAWN_POS_Y;
//    public short damage;
//    public short era;

    //constant
    private final float distanceY = Ultimate.SPAWN_POS_Y;

    // ตำแหน่งที่ขยับไปแล้ว
    public float movedDistanceX = 0;
    public float movedDistanceY = Ultimate.SPAWN_POS_Y;

    public static float MOVE_SPEED_X;
    public static float MOVE_SPEED_Y = 2.2f;

    public short era;

    short damage;

    public UltimateObj(byte era) {
        super(MainGame.ultimateImages[era - 1].Clone());
        this.era = era;
    }

    public UltimateObj(){
        super(MainGame.ultimatePlane.Clone());
    }

    public void ultimateMove() {
        switch (this.era) {
            case 1 : // meteor

            case 2 : { // arrow

                final float moveY = this.MOVE_SPEED_Y * MainGame.deltaTime;
                final float DistanceX = Mathf.CalRange(-50, 50);

                this.MOVE_SPEED_X = 1.1f;

                float moveX = this.MOVE_SPEED_X * MainGame.deltaTime;

                movedDistanceX += moveX;
                movedDistanceY -= moveY;
                this.image.Move(moveX, -moveY);
            } break;
            case 3 : { // missile

                MOVE_SPEED_X = 1.1f; //same as plane

                final float moveX = this.MOVE_SPEED_X * MainGame.deltaTime;

                movedDistanceX += moveX;
                this.image.Move(moveX, 0);
           } break;

            case 4 : { // laser beam
                final float moveX = this.MOVE_SPEED_X * MainGame.deltaTime;

                movedDistanceX += moveX;
                this.image.Move(moveX,0);
            }
        }
    }

    public void ultimateExplode(byte era) {
        switch(era){
            case 1: {

            } break;

            case 2: {

            } break;

            case 3:{

            } break;

            case 4:{

            } break;
        }
    }

    public void calDmg() {

    }

    public void SetPosition(float x, float y) {
        this.image.SetPosition(x, y);
    }
}