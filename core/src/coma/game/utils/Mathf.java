package coma.game.utils;

final public class Mathf {
    public static float calError(final float base, final float errorRate) {
        final float rand = (float) Math.random();

        return base + base * (rand >= 0.5f ? 1 : -1) * errorRate / 2;
    }

    public static int calRange(final int min, final int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public static float calRange(final float min, final float max) {
        return (float) (Math.random() * (max - min) + min);
    }
}
