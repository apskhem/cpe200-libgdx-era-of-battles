package coma.game.components;

import coma.game.MainGame;

import java.util.ArrayList;

public class Animator {

    private final ArrayList<AnimationState> states = new ArrayList<>();
    private final Image[] frameImages;
    public Image refImage;
    public Image currentFrameImage;
    public AnimationState currentState;
    public int currentFrame = 1;
    public float nextFrameDelay;
    public float maxNextFrameDelay;

    protected short displacedTranslateX = 0;

    public Animator(Image[] images) {
        this.frameImages = images;
        this.currentFrameImage = images[0];
    }

    public void Continue() {
        if (this.nextFrameDelay < 0) {
            this.NextAnimationFrame();

            this.nextFrameDelay = this.maxNextFrameDelay;
        } else {
            this.nextFrameDelay -= MainGame.deltaTime;
        }
    }

    public Image GetFrameImage(int frame) {
        return this.frameImages[frame];
    }

    private void ChangeAnimationStateTo(final String name) {
        if (this.currentState.name.equals(name)) return;

        for (final AnimationState a : this.states) {
            if (a.name.equals(name)) {
                this.currentState = a;
            }
        }
    }

    public void NextAnimationFrame() {
        // this.SetAnimationFrameTo(this.currentState.NextFrame());

        switch (this.currentFrame) {
            case 1:
                this.SetAnimationFrameTo(2);
                break;
            case 2:
                this.SetAnimationFrameTo(3);
                break;
            case 3:
                this.SetAnimationFrameTo(1);
                break;
            case 4:
                this.SetAnimationFrameTo(5);
                break;
            case 5:
                this.SetAnimationFrameTo(6);
                break;
            case 6:
                this.SetAnimationFrameTo(4);
        }
    }

    public void AddState(final String state, final int fromFrame, final int toFrame) {
        final AnimationState a = new AnimationState(state, fromFrame, toFrame);

        if (this.states.size() == 0) this.currentState = a;

        this.states.add(a);
    }

    public void SetAnimationFrameTo(int frame) {
        if (this.currentFrame == frame || refImage == null) return;

        this.currentFrame = (byte) frame;

        final Image i = this.frameImages[frame - 1];
        this.refImage.SetTexture(i);

        // reset displacement
        this.refImage.SetSize(Float.NaN, Float.NaN);
        this.refImage.Move(-this.displacedTranslateX, 0);
        this.displacedTranslateX = 0;

        // sync to new image
        this.refImage.SetSize(i.naturalWidth, i.naturalHeight);

        if (this.refImage.isFlipped) this.refImage.Move(this.displacedTranslateX = (short)(this.refImage.naturalWidth - i.naturalWidth), 0);
    }
}
