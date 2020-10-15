package coma.game;

public class Mathf {
    public static float CalError(float base, float errorRate) {
        final float rand = (float) Math.random();

        return base + base * (rand >= 0.5f ? 1 : -1) * errorRate / 2;
    }

    public static float CalRange(float min, float max) {
        return (float) (Math.random() * (max - min + 1) + min);
    }
}
