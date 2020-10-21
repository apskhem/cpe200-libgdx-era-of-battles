package coma.game.models.contents;

import coma.game.MainGame;
import coma.game.controllers.AudioController;
import coma.game.models.Player;
import coma.game.Resources;
import coma.game.utils.Mathf;
import coma.game.views.Renderer;
import coma.game.components.*;
import org.w3c.dom.ranges.RangeException;

import java.util.ArrayList;

final public class Ultimate {

    private final ArrayList<UltimateObj> ultimateSpawnContainer = new ArrayList<>();
    private final ArrayList<ImageRegion> explodingObjectContainer = new ArrayList<>();
    private Image plane;
    private final byte era;
    private final boolean isFlipped;

    // constant
    public static final float EXPLOSION_FRAME_ANIMATION_TIME = 4f;
    public static final short SPAWN_POS_Y = 600;  // for era 3 plane around 530
    public static final short EXPLODE_POS_Y = 30;

    public Player target;
    public Player caller;

    public Ultimate(final byte era, final boolean isFlipped) {
        this.isFlipped = isFlipped;

        int n;
        switch (this.era = era) {
            case 1: n = Mathf.CalRange(10, 15); break;
            case 2: n = Mathf.CalRange(80, 112); break;
            case 3: {
                n = 14;

                // set plane
                this.plane = Resources.ulPlane.Clone();

                this.plane.SetPosition(this.isFlipped ? 4080 : -2000, 520);
                if (isFlipped) this.plane.FlipHorizontal();

                Renderer.AddComponents(this.plane);

                AudioController.PlayAndSetVolume(Resources.ulPlaneSound, MainGame.AUDIO_VOLUME);
            } break;
            case 4: n = Mathf.CalRange(30, 35); break;
            default: throw new RangeException((short) 0, "Wrong parameter input.");
        }

        for (byte i = 0; i < n; i++) {
            // spawn
            float spawnX; //border of x-axis screen
            float spawnDelay;
            float damage;
            float moveSpeedX;
            float moveSpeedY;

            switch (era) {
                case 1: {
                    spawnX = this.isFlipped ? Mathf.CalRange(520f, 2180f) : Mathf.CalRange(-100, 1560f);
                    spawnDelay = Mathf.CalRange(0, 90f);
                    damage = Mathf.CalRange(60f, 89f);
                    moveSpeedX = this.isFlipped ? - 6.7f : 6.7f;
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
                    spawnDelay = this.isFlipped ? ((n - 1) - i) * 2.5f : i * 2.5f;
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

            if (this.isFlipped) obj.image.FlipHorizontal();
            obj.image.SetPosition(spawnX, SPAWN_POS_Y);

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
                    final ImageRegion r = Resources.explosionImageRegion.Clone();
                    r.SetPosition(obj.image.GetTransform().x, obj.image.GetTransform().y);
                    r.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;

                    Renderer.AddComponents(r);

                    this.explodingObjectContainer.add(r);

                    AudioController.PlayAndSetVolume(Resources.explosionSounds[0], MainGame.AUDIO_VOLUME);
                }

                hitUltimateObjects.add(obj);
            }
            else {
                obj.ultimateMove();
            }
        }

        this.UpdateExplosion();

        // update plane
        if (this.plane != null) this.plane.Move((this.isFlipped ? -15.5f : 15.5f) * MainGame.deltaTime, 0);

        // remove all exploded obj
        this.ultimateSpawnContainer.removeAll(hitUltimateObjects);

        // destroy ultimate caller zero objects are in container
        if (this.explodingObjectContainer.size() == 0) {
            if (this.era == 3 && this.plane != null) {
                if (this.isFlipped ? this.plane.GetTransform().x < -2000 : this.plane.GetTransform().x > 2080) {
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

