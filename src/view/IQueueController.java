package view;

import java.util.List;

public interface IQueueController {

    void drawSimulationRows(List<SimulationRow> rows, List<VehicleRow> vehicles);

    void drawSimulationResult(SimulationResult result);

    Double getMinutesFrom();

    Integer getIterationsToDraw();
}
