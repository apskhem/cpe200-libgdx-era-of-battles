package coma.game.components;

public class AnimationState {

    public int currentFrame = 1;
    public final String name;
    public final int startFrame;
    public final int endFrame;

    public AnimationState(final String name, final int startFrame, final int endFrame) {
        this.name = name;
        this.currentFrame = startFrame;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    public int NextFrame() {
        this.currentFrame++;

        if (this.currentFrame > this.endFrame) this.currentFrame = this.startFrame;

        return this.currentFrame;
    }
}
