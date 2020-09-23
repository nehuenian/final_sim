package model.events;

import java.util.Random;

public class UniformRandomGenerator {
    private final Random randomGenerator = new Random();
    private final int minValue;
    private final int maxValue;

    public UniformRandomGenerator(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int getRandom() {
        return (minValue + randomGenerator.nextInt(maxValue - minValue));
    }
}
