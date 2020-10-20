package coma.game.components;

import coma.game.utils.UIController;

final public class Canvas extends Image {
    public float x;
    public float y;

    public final int VIEWPORT_WIDTH = 960;
    public final int VIEWPORT_HEIGHT = 600;

    public Canvas(final String internalPath) {
        super(internalPath);
    }

    public Canvas(final Image canvas) {
        super(canvas);
    }

    public void SetActive(final boolean value) {
        if (value) {
            this.src.setColor(1,1,1,1);
        }
        else {
            this.src.setColor(0.5f,0.5f,0.5f,1);
        }
    }

    public boolean IsInBound(final float x, final float y) {
        return x >= this.x && x <= this.x + this.src.getWidth() && y >= this.y && y <= this.y + this.src.getHeight() && this.isVisible;
    }

    public void SetScale(final float value) {
        if (value < 0) return;

        this.src.scale(value - 1);
    }

    public void SetViewBox(float width, float height) {
        width = Float.isNaN(width) ? this.naturalWidth : width;
        height = Float.isNaN(height) ? this.naturalHeight : height;

        this.src.setBounds(this.src.getX(), this.src.getY(), width, height);
    }

    public void SetImagePosition(final float x, final float y) {
        super.SetPosition(x, y);
    }

    @Override
    public void SetPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public void SetPosition(final String alignX, final float y) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }

        this.y = (int) y;
    }

    public void SetPosition(final float x, final String alignY) {
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }

        this.x = (int) x;
    }

    public void SetPosition(final String alignX, final String alignY) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }
    }
}
