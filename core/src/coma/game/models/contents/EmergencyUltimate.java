package coma.game.models.contents;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.components.Animator;
import coma.game.components.Image;
import coma.game.components.ImageRegion;
import coma.game.controllers.AudioController;
import coma.game.models.Player;
import coma.game.views.Renderer;

public class EmergencyUltimate extends Ultimate {

    private final short damage = 700;

    // constant
    private final short PLAYER_SPAWN_POS = 152;
    private final short BOT_SPAWN_POS = Player.RIGHT_STRONGHOLD_POSITION_X + 210;
    private final short UNIT_SPAWN_POSITION_Y = 52;

    private final float MOVE_SPEED = 13.5f;

    public static final float REQUIRED_XP = 4000;

    private final Image doraemonImage = Resources.emergencyUltimateImages[0].clone();
    private final Animator anim = new Animator(Resources.emergencyUltimateImages);
    private ImageRegion explsionImageRegion;

    private short moveX = 0;

    public EmergencyUltimate(final Player caller, final Player target, final boolean isFlipped) {
        super(caller, target, (byte) -1, isFlipped);

        if (isFlipped) {
            this.doraemonImage.setPosition(this.BOT_SPAWN_POS - this.doraemonImage.naturalWidth, this.UNIT_SPAWN_POSITION_Y);
        }
        else {
            this.doraemonImage.setPosition(this.PLAYER_SPAWN_POS, this.UNIT_SPAWN_POSITION_Y);
        }

        this.anim.refImage = this.doraemonImage;

        if (isFlipped) this.doraemonImage.flipHorizontal();

        Renderer.addComponents(this.doraemonImage);

        AudioController.playAndSetVolume(Resources.emergencyUlSound, MainGame.AUDIO_VOLUME);
    }

    @Override
    public void update() {
        // normal moving
        if (this.explsionImageRegion == null) {
            if (this.isReachedMax()) {
                this.target.stronghold.health -= this.damage;

                Renderer.removeComponents(this.doraemonImage);

                if (this.target.stronghold.health > 0) {
                    this.explode();
                }
                else {
                    this.caller.emergencyUltimateCaller = null;
                }
            }
            else {
                // move
                this.move();
            }
        }
        // after explosion
        else {
            if (this.explsionImageRegion.tempTimer <= 0) {
                if (this.explsionImageRegion.isAtTheEnd()) {
                    this.caller.emergencyUltimateCaller = null;
                    Renderer.removeComponents(this.explsionImageRegion);
                }
                else {
                    this.explsionImageRegion.nextRegion();
                    this.explsionImageRegion.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;
                }
            }
            else {
                this.explsionImageRegion.tempTimer -= MainGame.deltaTime;
            }
        }
    }

    private void move() {
        // normal moving
        final float mov = this.MOVE_SPEED * MainGame.deltaTime;

        this.moveX += mov;
        this.doraemonImage.move(this.doraemonImage.isFlipped ? -mov : mov, 0);

        if (this.anim.currentFrame > 3) this.anim.currentFrame = 3;

        // animation
        this.anim.Continue();
    }

    public boolean isReachedMax() {
        return this.moveX >= Unit.MAX_MOVE - this.doraemonImage.naturalWidth;
    }

    private void explode() {
        final ImageRegion r = Resources.explosionImageRegion.clone();
        r.setPosition(this.doraemonImage.getTransform().x, this.doraemonImage.getTransform().y);
        r.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;

        Renderer.addComponents(r);

        this.explsionImageRegion = r;

        AudioController.playAndSetVolume(Resources.explosionSounds[0], MainGame.AUDIO_VOLUME);
    }

    public void removeImmidiate() {
        if (this.explsionImageRegion == null) {
            Renderer.removeComponents(this.doraemonImage);
        }
        else {
            Renderer.removeComponents(this.explsionImageRegion);
        }

        this.caller.emergencyUltimateCaller = null;
    }
}
