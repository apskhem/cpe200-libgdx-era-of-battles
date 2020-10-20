package coma.game.components;

final public class ImageRegion extends Image {

    public int regionWidth;
    public int regionHeight;
    public int currentIRegionIndex;
    public int currentJRegionIndex;
    public int maxIRegionIndex;
    public int maxJRegionIndex;
    public float tempTimer;

    public ImageRegion(final String internalPath, final int width, final int height, final int maxI, final int maxJ) {
        super(internalPath);

        this.regionWidth = width;
        this.regionHeight = height;
        this.maxIRegionIndex = maxI;
        this.maxJRegionIndex = maxJ;

        this.src.setRegion(0, 0, width, height);
        this.src.setBounds(0,0, width, height);
    }

    public ImageRegion(final Image image, final int width, final int height, final int maxI, final int maxJ) {
        super(image);

        this.regionWidth = width;
        this.regionHeight = height;
        this.maxIRegionIndex = maxI;
        this.maxJRegionIndex = maxJ;

        this.src.setRegion(0, 0, width, height);
        this.src.setBounds(0,0, width, height);
    }

    public void NextRegion() {
        if (this.currentIRegionIndex + 1 == this.maxIRegionIndex) {
            this.currentJRegionIndex = (this.currentJRegionIndex + 1) % this.maxJRegionIndex;
        }

        this.currentIRegionIndex = (this.currentIRegionIndex + 1) % this.maxIRegionIndex;

        this.src.setRegion(
                this.regionWidth * this.currentIRegionIndex,
                this.regionHeight * this.currentJRegionIndex,
                this.regionWidth,
                this.regionHeight);
    }

    public boolean IsAtTheEnd() {
        return this.currentIRegionIndex + 1 == this.maxIRegionIndex && this.currentJRegionIndex + 1 == this.maxJRegionIndex;
    }

    public ImageRegion Clone() {
        return  new ImageRegion(this, this.regionWidth, this.regionHeight, this.maxIRegionIndex, this.maxJRegionIndex);
    }
}
