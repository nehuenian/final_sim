package view;

public class SimulationResult {

    private final int numberOfLightChanges;
    private final int numberOfBlocksOnY;

    public SimulationResult(int numberOfLightChanges, int numberOfBlocksOnY) {
        this.numberOfLightChanges = numberOfLightChanges;
        this.numberOfBlocksOnY = numberOfBlocksOnY;
    }

    public double getBlockedPercentage() {
        return ((double) numberOfBlocksOnY)/numberOfLightChanges * 100;
    }
}
