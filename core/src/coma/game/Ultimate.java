package coma.game;

import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;

public class Ultimate {

    public final ArrayList<UltimateObj> ultimateSpawnContainer = new ArrayList<>();
    public final ArrayList<ImageRegion> explodingObjectContainer = new ArrayList<>();
    public final byte era;
    public Image plane;

    //constant
    public static final float EXPLOSION_FRAME_ANIMATION_TIME = 4f;
    public static final short SPAWN_POS_Y = 600;  // for era 3 plane around 530
    public static final short EXPLODE_POS_Y = 30;
    public static final short SPAWN_POS_XMIN = 0;
    public static final short SPAWN_POS_XMAX = 960;

    public Player target;
    public Player caller;

    public Ultimate(byte era) {
        int n;
        switch (this.era = era) {
            case 1: n = Mathf.CalRange(10, 15); break;
            case 2: n = Mathf.CalRange(25, 30); break;
            case 3: {
                n = Mathf.CalRange(15, 25);

                // set plane
                this.plane = MainGame.ulPlane;
                this.plane.SetPosition(-811, 520);
                Renderer.AddComponents(this.plane);

            } break;
            case 4: n = Mathf.CalRange(30, 35); break;
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }

        for (int i = 0; i < n; i++) {
            UltimateObj obj = new UltimateObj(era, Mathf.CalRange(0, 90f));
            this.SpawnUltimateObj(obj);
            this.ultimateSpawnContainer.add(obj);
        }
    }

    private void SpawnUltimateObj(UltimateObj obj){
        if(obj == null) return;

        float x = Mathf.CalRange(20f, 1600f); //border of x-axis screen

        if(obj.era == 3) obj.SetPosition(x, EXPLODE_POS_Y);
        else obj.SetPosition(x, SPAWN_POS_Y);

        Renderer.AddComponents(obj.image);

        // MainGame  if อยากใส่เพลง
    }

    public void Update() {
        if (this.target == null && this.caller == null) return;

        final ArrayList<UltimateObj> hitUltimateObjects = new ArrayList<>();

        // new distance calculation and checking explosion
        for (final UltimateObj obj : this.ultimateSpawnContainer) {
            if (obj.movedDistanceY < Ultimate.EXPLODE_POS_Y) {
                obj.ultimateExplode(this.target.units);

                final ImageRegion r = MainGame.explosionImageRegion.Clone();
                r.SetPosition(obj.image.GetTransform().x, obj.image.GetTransform().y);
                r.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;

                Renderer.RemoveComponents(obj.image);
                Renderer.AddComponents(r);

                this.explodingObjectContainer.add(r);

                hitUltimateObjects.add(obj);
            }
            else {
                obj.ultimateMove();
            }
        }

        this.UpdateExplosion();

        // update plane
        if (this.plane != null) this.plane.Move(15.5f * MainGame.deltaTime, 0);

        // remove all exploded obj
        this.ultimateSpawnContainer.removeAll(hitUltimateObjects);

        // destroy ultimate caller zero objs are in container
        if (this.explodingObjectContainer.size() == 0) {
            if (this.era == 3 && this.plane != null) {
                if (this.plane.GetTransform().x > 2080) this.caller.ultimateCaller = null;

                Renderer.RemoveComponents(this.plane);
            }
            else {
                if (this.ultimateSpawnContainer.size() == 0) this.caller.ultimateCaller = null;
            }
        }
    }

    private void UpdateExplosion() {
        final ArrayList<ImageRegion> endedExplosionImages = new ArrayList<>();

        for (final ImageRegion r : this.explodingObjectContainer) {
            if (r.tempTimer <= 0) {
                if (r.IsAtTheEnd()) {
                    endedExplosionImages.add(r);
                }
                else {
                    r.NextRegion();
                    r.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;
                }
            }
            else {
                r.tempTimer -= MainGame.deltaTime;
            }
        }

        // remove when ended
        for (final ImageRegion r : endedExplosionImages) {
            Renderer.RemoveComponents(r);
        }

        this.explodingObjectContainer.removeAll(endedExplosionImages);
    }
}

class UltimateObj extends GameObject {

    //constant
    private final float distanceY = Ultimate.SPAWN_POS_Y;

    // ตำแหน่งที่ขยับไปแล้ว
    public float movedDistanceX = 0;
    public float movedDistanceY = Ultimate.SPAWN_POS_Y;
    public float moveSpeedX;
    public float moveSpeedY;
    public final byte era;
    public float damage;
    public float spawnDelay;

    public UltimateObj(byte era, float spawnDelay) {
        super(MainGame.ultimateImages[era - 1].Clone());

        this.spawnDelay = spawnDelay;

        switch (this.era = era) {
            case 1: {
                this.damage = Mathf.CalRange(75f, 210f);
                this.moveSpeedX = 6.7f;
                this.moveSpeedY = 12.4f;
            } break;
            case 2: {
                this.damage = Mathf.CalRange(100f,300f);
                this.moveSpeedX = 6.7f;
                this.moveSpeedY = 12.4f;
            } break; // here
            case 3: {
                this.damage = Mathf.CalRange(200f, 400f);
                this.moveSpeedY = 0;
                this.moveSpeedX = 0;
            } break;
            case 4: {
                this.damage = Mathf.CalRange(300f, 500f);
                this.moveSpeedY = 0;
                this.moveSpeedX = 0;

            } break;
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }
    }

    public void ultimateMove() {
        if (this.spawnDelay <= 0) {
            final float moveY = -this.moveSpeedY * MainGame.deltaTime;
            final float moveX = this.moveSpeedX * MainGame.deltaTime;

            this.movedDistanceX += moveX;
            this.movedDistanceY += moveY;

            this.image.Move(moveX, moveY);
        }
        else {
            this.spawnDelay -= MainGame.deltaTime;
        }
    }

    public void ultimateExplode(ArrayList<Unit> units) {
        switch (era) {
            case 1: {
                // damges target units
                final float objCenterX = this.image.GetTransform().x + this.image.naturalWidth / 2;
                final float minEffectRange = objCenterX - 250;
                final float maxEffectRange = objCenterX + 250;

                for (final Unit u : units) {
                    if (u.image.GetTransform().x >= minEffectRange && u.image.GetTransform().x + u.image.naturalWidth <= maxEffectRange) {
                        u.health -= this.damage;
                    }
                }
            } break;

            case 2: {

            } break;

            case 3:{

            } break;

            case 4:{

            } break;
        }
    }

    public void SetPosition(float x, float y) {
        this.image.SetPosition(x, y);
    }
}