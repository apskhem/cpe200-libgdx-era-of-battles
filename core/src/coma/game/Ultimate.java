package coma.game;

import java.util.ArrayList;

public class Ultimate {

    public final ArrayList<UltimateObj> ultimateSpawnContainer = new ArrayList<>();

    //constant
    public static short SPAWN_POS_Y = 550;  //for era 3 plane around 530
    public static short EXPLODE_POS_Y = 0;
    public static short SPAWN_POS_XMIN = 0;
    public static short SPAWN_POS_XMAX = 960;

    public Player target;
    public Player caller;

    public Ultimate() {
        nUltimateProcess((byte) 2);  //nullpointerX here when use parameter caller.era

    }

    private void SpawnUltimateObj(UltimateObj obj){
        if(obj == null) return;

        int x = Mathf.CalRange(240, 1760);     //border of x-axis screen

        obj.SetPosition(x, SPAWN_POS_Y);
        Renderer.AddComponents(obj.image);

//        MainGame  if อยากใส่เพลง
    }

    private void nUltimateProcess(byte era){
        int n = 0;
        switch(era) {
            case 1: {
                n = Mathf.CalRange(10, 15);
            }
            break;
            case 2: {
                n = Mathf.CalRange(25, 30);
            }
            break;
            case 3: {
                n = Mathf.CalRange(15, 25);
            } break;
            case 4 : {
                n = Mathf.CalRange(30, 35);
            } break;
        }
        for (int i = 0; i < n; i++) {
            UltimateObj obj = new UltimateObj(era);
            SpawnUltimateObj(obj);
            ultimateSpawnContainer.add(obj);
        }
    }

    public void ContinueUltimate() {
        if (this.target == null && this.caller == null) return;

        final ArrayList<UltimateObj> explodedUltimateObjects = new ArrayList<>();

        // new distance calculation and checking explosion
        for (final UltimateObj obj : this.ultimateSpawnContainer) {
            if (obj.movedDistanceY < Ultimate.EXPLODE_POS_Y) {
                //obj.ultimateExplode();

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

    public static float MOVE_SPEED_X = 1.2f;
    public static float MOVE_SPEED_Y = 1.2f;

    public short era;

    short damage;

    public UltimateObj(byte era) {
        super(MainGame.ultimateImages[era].Clone());
        this.era = era;
    }

    public void ultimateMove() {
        switch(this.era){
            case 1 : // meteor

            case 2 : { // arrow

//                float distanceX = Mathf.CalRange(-50, 50);
//
//                MOVE_SPEED_X = distanceX / MainGame.deltaTime;
//
//                if(movedDistanceY > this.image.naturalHeight){
//                    final float moveX = this.MOVE_SPEED_X * MainGame.deltaTime;
//                    final float moveY = this.MOVE_SPEED_Y * MainGame.deltaTime;
//
//                    movedDistanceX += moveX;
//                    movedDistanceY -= moveY;
//                }
            } break;

            case 3 : { // missile

//                float distanceX = Mathf.CalRange(-50, 50);
//
//                MOVE_SPEED_X = distanceX / MainGame.deltaTime;

                final float moveY = this.MOVE_SPEED_Y * MainGame.deltaTime;

                movedDistanceY -= moveY;
                this.image.Move(0, -moveY);

            } break;








//                float distanceX = Mathf.CalRange(0,50);
//
//                MOVE_SPEED_X = distanceX / MainGame.deltaTime;
//
//                if(movedDistanceY > this.image.naturalHeight){
//                    final float moveX = this.MOVE_SPEED_X * MainGame.deltaTime;
//                    final float moveY = this.MOVE_SPEED_Y * MainGame.deltaTime;
//
//                    movedDistanceX += moveX;
//                    movedDistanceY -= moveY;
//                }
//            } break;

            case 4 : { // laser beam
                if(movedDistanceX < this.image.naturalWidth){
                    final float moveX = this.MOVE_SPEED_X * MainGame.deltaTime;

                    movedDistanceX += moveX;
                }
            }
    }

//    public void ultimateExplode() {
//        //explode & send damage to in-area opponent troops
//        //era 1 explode
//        //era 2 more arrow
//        //era 3 explode
//        //era 4 laser beam

    }

    public void calDmg(){
        }

    public void SetPosition(float x, float y) {
        this.image.SetPosition(x, y);
    }
}