package coma.game;

import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;

public class Ultimate {

    private final ArrayList<UltimateObj> ultimateSpawnContainer = new ArrayList<>();
    private final ArrayList<ImageRegion> explodingObjectContainer = new ArrayList<>();
    private final byte era;
    private Image plane;

    //constant
    public static final float EXPLOSION_FRAME_ANIMATION_TIME = 4f;
    public static final short SPAWN_POS_Y = 600;  // for era 3 plane around 530
    public static final short EXPLODE_POS_Y = 30;
    public static final short SPAWN_POS_XMIN = 0;
    public static final short SPAWN_POS_XMAX = 960;

    public Player target;
    public Player caller;

    public Ultimate(byte era) {
        int n = 0;
        switch (this.era = era) {
            case 1: n = Mathf.CalRange(10, 15); break;
            case 2: n = Mathf.CalRange(80, 112); break;
            case 3: {
                n = 14;

                // set plane
                this.plane = MainGame.ulPlane;
                this.plane.SetPosition(-2000, 520);
                Renderer.AddComponents(this.plane);

                MainGame.ulPlaneSound.play();
            } break;
            case 4: n = Mathf.CalRange(30, 35); break;
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }

        for (byte i = 0; i < n; i++) {
            // spawn
            float spawnX = 0; //border of x-axis screen
            float spawnDelay = 0;
            float damage = 0;
            float moveSpeedX = 0;
            float moveSpeedY = 0;

            switch (era) {
                case 1: {
                    spawnX = Mathf.CalRange(20f, 1600f);
                    spawnDelay = Mathf.CalRange(0, 90f);
                    damage = Mathf.CalRange(60f, 89f);
                    moveSpeedX = 6.7f;
                    moveSpeedY = 12.4f;
                } break;
                case 2: {
                    spawnX = Mathf.CalRange(220f, 1600f);
                    spawnDelay = Mathf.CalRange(0, 90f);
                    damage = Mathf.CalRange(100f, 300f);
                    moveSpeedX = Mathf.CalRange(-2.3f, 2.3f);
                    moveSpeedY = 12.4f;
                } break;
                case 3: {
                    spawnX = (i * 120) + 240;
                    spawnDelay = i * 2.5f;
                    damage = Mathf.CalRange(200f, 400f);
                    moveSpeedY = 25.7f;
                    moveSpeedX = 0;
                } break;
                case 4: {
                    spawnX = Mathf.CalRange(220f, 1600f);
                    spawnDelay = Mathf.CalRange(0, 90f);
                    damage = Mathf.CalRange(300f, 500f);
                    moveSpeedY = 30f;
                    moveSpeedX = 0;
                } break;
                default: throw new RangeException((short) 0, "Wrong parameter input.");
            }

            UltimateObj obj = new UltimateObj(era, spawnDelay, damage, moveSpeedX, moveSpeedY);

            obj.SetPosition(spawnX, SPAWN_POS_Y);

            Renderer.AddComponents(obj.image);

            this.ultimateSpawnContainer.add(obj);
        }
    }

    public void Update() {
        if (this.target == null && this.caller == null) return;

        final ArrayList<UltimateObj> hitUltimateObjects = new ArrayList<>();

        // new distance calculation and checking explosion
        for (final UltimateObj obj : this.ultimateSpawnContainer) {
            if (obj.image.GetTransform().y < Ultimate.EXPLODE_POS_Y) {
                obj.ultimateExplode(this.target.units);

                Renderer.RemoveComponents(obj.image);

                if (this.era != 2) {
                    final ImageRegion r = MainGame.explosionImageRegion.Clone();
                    r.SetPosition(obj.image.GetTransform().x, obj.image.GetTransform().y);
                    r.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;

                    Renderer.AddComponents(r);

                    this.explodingObjectContainer.add(r);

                    MainGame.explosionSounds[0].play();
                }

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
                if (this.plane.GetTransform().x > 2080) {
                    this.caller.ultimateCaller = null;
                    Renderer.RemoveComponents(this.plane);
                }
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

    public UltimateObj(byte era, float spawnDelay, float damage, float moveSpeedX, float moveSpeedY) {
        super(MainGame.ultimateImages[era - 1].Clone());

        this.era = era;
        this.spawnDelay = spawnDelay;
        this.damage = damage;
        this.moveSpeedX = moveSpeedX;
        this.moveSpeedY = moveSpeedY;
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
                final float objCenterX = this.image.GetTransform().x + this.image.naturalWidth / 2;
                final float minEffectRange = objCenterX - 250;
                final float maxEffectRange = objCenterX + 250;

                for (final Unit u : units) {
                    if (u.image.GetTransform().x >= minEffectRange && u.image.GetTransform().x + u.image.naturalWidth <= maxEffectRange) {
                        u.health -= this.damage;
                    }
                }
            } break;
            case 2:
            case 3:
            case 4: {
                final float objCenterX = this.image.GetTransform().x + this.image.naturalWidth / 2;

                for (final Unit u : units) {
                    if (u.image.GetTransform().x <= objCenterX && u.image.GetTransform().x + u.image.naturalWidth >= objCenterX) {
                        u.health -= this.damage;
                    }
                }
            } break;
        }
    }

    public void calDmg() {

    }

    public void SetPosition(float x, float y) {
        this.image.SetPosition(x, y);
    }
}