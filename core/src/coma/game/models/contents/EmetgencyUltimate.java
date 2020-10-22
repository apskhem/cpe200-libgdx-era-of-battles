package coma.game.models.contents;

import coma.game.MainGame;
import coma.game.Resources;
import coma.game.components.Animator;
import coma.game.components.Image;
import coma.game.components.ImageRegion;
import coma.game.controllers.AudioController;
import coma.game.models.Player;
import coma.game.views.Renderer;

class EmergencyUltimate extends Ultimate{

    //emergency spawn pos
    final private short PLAYER_SPAWN_POS = 152;
    final private short BOT_SPAWN_POS = Player.RIGHT_STRONGHOLD_POSITION_X + 210;

    private short damage = 700;

    private short EXPLODE_POS_X = 1800;
    private float MOVE_SPEED = 20f;
    private float ANIMETION_DELAY = 10f;

    private Image doraemon;
    private Animator anim;

    private short moveX = 0;

    public EmergencyUltimate(byte era, final boolean isFlipped) {
        super((byte) 4, isFlipped);

        //set doraemon
        this.doraemon = Resources.emergencyUltimate;

        this.doraemon.SetPosition(this.isFlipped ? this.PLAYER_SPAWN_POS : this.BOT_SPAWN_POS, 52);

        if (isFlipped) this.doraemon.FlipHorizontal();

        Renderer.AddComponents(this.doraemon);

        //AudioController.PlayAndSetVolume(Resources.ulPlaneSound, MainGame.AUDIO_VOLUME);
    }

    public void emergencyUpdate(){
        if (this.target == null && this.caller == null) return;

        if(doraemon.GetTransform().x > this.EXPLODE_POS_X){
            explode();
            Renderer.RemoveComponents(doraemon);
        }
        else nuclearMove();
    }

    public void nuclearMove() {
        // normal moving
        if (moveX < this.EXPLODE_POS_X - this.doraemon.naturalWidth) {

            final float mov = this.MOVE_SPEED * MainGame.deltaTime;

            moveX += mov;
            this.doraemon.Move((this.doraemon.isFlipped ? -mov : mov), 52);

//            if (this.anim.currentFrame > 3) this.anim.currentFrame = 3;
//
//            // animation
//            this.anim.Continue();
        }
    }

    private void explode(){
        target.stronghold.health -= this.damage;

        final ImageRegion r = Resources.explosionImageRegion.Clone();
        r.SetPosition(doraemon.GetTransform().x, doraemon.GetTransform().y);
        r.tempTimer = Ultimate.EXPLOSION_FRAME_ANIMATION_TIME;

        Renderer.AddComponents(r);

        AudioController.PlayAndSetVolume(Resources.explosionSounds[0], MainGame.AUDIO_VOLUME);


    }

}
