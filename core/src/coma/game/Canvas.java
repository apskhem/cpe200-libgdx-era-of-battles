package coma.game;

public class Canvas extends Image {
    public int x;
    public int y;
    public boolean isVisible = true;

    public final int VIEWPORT_WIDTH = 960;
    public final int VIEWPORT_HEIGHT = 600;

    public Canvas(String internalPath) {
        super(internalPath);
    }

    public void SetVisibility(boolean value) {
        this.isVisible = value;

        if (value) {
            this.src.setColor(1,1,1,1);
        }
        else {
            this.src.setColor(0,0,0,0);
        }
    }

    public boolean IsInBound(float x, float y) {
        return x >= this.x && x <= this.x + this.src.getWidth() && y >= this.y && y <= this.y + this.src.getHeight() && this.isVisible;
    }

    public void SetPosition(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    public void SetPosition(String alignX, float y) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }

        this.y = (int) y;
    }

    public void SetPosition(float x, String alignY) {
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }

        this.x = (int) x;
    }

    public void SetPosition(String alignX, String alignY) {
        if (alignX.equals("center")) {
            this.x = (int)(this.VIEWPORT_WIDTH/2 - this.src.getWidth() /2);
        }
        if (alignY.equals("center")) {
            this.y = (int)((this.VIEWPORT_HEIGHT/2 - this.src.getHeight()/2));
        }
    }
}
