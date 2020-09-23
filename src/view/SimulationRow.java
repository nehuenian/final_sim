package view;

import model.AvailableLane;
import model.LightState;
import model.events.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimulationRow {
    private double time;
    private EventType currentEvent;
    private Double rndDidVehicleTurned;
    private String didVehicleTurned;

    private Double rndParticularArrival;
    private Double timeToNextParticularArrival;
    private Double nextParticularArrival;

    private Double rndTaxiArrival;
    private Double rndTaxiLaneArrival;
    private AvailableLane nextTaxiLane;
    private Double timeToNextTaxiArrival;
    private Double nextTaxiArrival;

    private Double rndBusArrival;
    private Double timeToNextBusArrival;
    private Double nextBusArrival;

    private double nextChangeOfLight;

    private Integer particularsOnY;
    private Integer taxisOnY;
    private Integer busesOnY;

    private int rightXQueue;
    private int leftXQueue;
    private LightState lightStateX;
    private int yQueue;
    private LightState lightStateY;

    private int lightChangesCounter;
    private int blockedYCounter;
    private List<VehicleRow> vehicles;

    private AtomicInteger idSingleton = new AtomicInteger(0);

    public SimulationRow() {
        //Default constructor
    }

    public SimulationRow(SimulationRow simulationRowToClone) {
        this.idSingleton = simulationRowToClone.idSingleton;
        this.time = simulationRowToClone.time;
        this.currentEvent = simulationRowToClone.currentEvent;
        this.rndDidVehicleTurned = simulationRowToClone.rndDidVehicleTurned;
        this.didVehicleTurned = simulationRowToClone.didVehicleTurned;

        this.rndParticularArrival = simulationRowToClone.rndParticularArrival;
        this.timeToNextParticularArrival = simulationRowToClone.timeToNextParticularArrival;
        this.nextParticularArrival = simulationRowToClone.nextParticularArrival;

        this.rndTaxiArrival = simulationRowToClone.rndTaxiArrival;
        this.rndTaxiLaneArrival = simulationRowToClone.rndTaxiLaneArrival;
        this.nextTaxiLane = simulationRowToClone.nextTaxiLane;
        this.timeToNextTaxiArrival = simulationRowToClone.timeToNextTaxiArrival;
        this.nextTaxiArrival = simulationRowToClone.nextTaxiArrival;

        this.rndBusArrival = simulationRowToClone.rndBusArrival;
        this.timeToNextBusArrival = simulationRowToClone.timeToNextBusArrival;
        this.nextBusArrival = simulationRowToClone.nextBusArrival;

        this.nextChangeOfLight = simulationRowToClone.nextChangeOfLight;

        this.particularsOnY = simulationRowToClone.particularsOnY;
        this.taxisOnY = simulationRowToClone.taxisOnY;
        this.busesOnY = simulationRowToClone.busesOnY;

        this.rightXQueue = simulationRowToClone.rightXQueue;
        this.leftXQueue = simulationRowToClone.leftXQueue;
        this.lightStateX = simulationRowToClone.lightStateX;
        this.yQueue = simulationRowToClone.yQueue;
        this.lightStateY = simulationRowToClone.lightStateY;

        this.lightChangesCounter = simulationRowToClone.lightChangesCounter;
        this.blockedYCounter = simulationRowToClone.blockedYCounter;
        this.vehicles = simulationRowToClone.vehicles;
    }

    public List<String> getTableViewRow() {
        List<String> tableViewCells = getTableViewRowBase();

        vehicles.forEach(vehicleRow -> tableViewCells.addAll(vehicleRow.getTableViewRow()));

        return tableViewCells;
    }

    private List<String> getTableViewRowBase() {
        ArrayList<String> tableViewBaseCells = new ArrayList<>();

        tableViewBaseCells.add(String.valueOf(idSingleton.incrementAndGet()));
        tableViewBaseCells.add(String.format("%.4f",time));
        tableViewBaseCells.add(currentEvent.getNameToDraw());
        setSafeValue(tableViewBaseCells, rndDidVehicleTurned);
        setSafeValue(tableViewBaseCells, didVehicleTurned);

        setSafeValue(tableViewBaseCells, rndParticularArrival);
        setSafeValue(tableViewBaseCells, timeToNextParticularArrival);
        setSafeValue(tableViewBaseCells, nextParticularArrival);

        setSafeValue(tableViewBaseCells, rndTaxiLaneArrival);
        setSafeValue(tableViewBaseCells, nextTaxiLane);
        setSafeValue(tableViewBaseCells, rndTaxiArrival);
        setSafeValue(tableViewBaseCells, timeToNextTaxiArrival);
        setSafeValue(tableViewBaseCells, nextTaxiArrival);

        setSafeValue(tableViewBaseCells, rndBusArrival);
        setSafeValue(tableViewBaseCells, timeToNextBusArrival);
        setSafeValue(tableViewBaseCells, nextBusArrival);

        tableViewBaseCells.add(String.format("%.4f",nextChangeOfLight));

        setSafeValue(tableViewBaseCells, particularsOnY);
        setSafeValue(tableViewBaseCells, taxisOnY);
        setSafeValue(tableViewBaseCells, busesOnY);

        tableViewBaseCells.add(String.valueOf(rightXQueue));
        tableViewBaseCells.add(String.valueOf(leftXQueue));
        tableViewBaseCells.add(lightStateX.getNameToDraw());
        tableViewBaseCells.add(String.valueOf(yQueue));
        tableViewBaseCells.add(lightStateY.getNameToDraw());

        tableViewBaseCells.add(String.valueOf(lightChangesCounter));
        tableViewBaseCells.add(String.valueOf(blockedYCounter));

        return tableViewBaseCells;
    }

    private void setSafeValue(ArrayList<String> tableViewBaseCells, Object value) {
        if (value != null) {
            if (value instanceof Double) {
                tableViewBaseCells.add(String.format("%.4f",value));
            } else {
                tableViewBaseCells.add(value.toString());
            }
        } else {
            tableViewBaseCells.add("");
        }
    }

    public void setRndDidVehicleTurned(Double rndDidVehicleTurned) {
        this.rndDidVehicleTurned = rndDidVehicleTurned;
    }

    public void setDidVehicleTurned(Boolean didVehicleTurned) {
        if (didVehicleTurned != null) {
            if (didVehicleTurned) {
                this.didVehicleTurned = "Si";
            } else {
                this.didVehicleTurned = "No";
            }
        } else {
            this.didVehicleTurned = null;
        }
    }

    public void setRndParticularArrival(Double rndParticularArrival) {
        this.rndParticularArrival = rndParticularArrival;
    }

    public void setTimeToNextParticularArrival(Double timeToNextParticularArrival) {
        this.timeToNextParticularArrival = timeToNextParticularArrival;
    }

    public void setRndTaxiArrival(Double rndTaxiArrival) {
        this.rndTaxiArrival = rndTaxiArrival;
    }

    public void setRndTaxiLaneArrival(Double rndTaxiLaneArrival) {
        this.rndTaxiLaneArrival = rndTaxiLaneArrival;
    }

    public void setNextTaxiLane(AvailableLane nextTaxiLane) {
        this.nextTaxiLane = nextTaxiLane;
    }

    public void setTimeToNextTaxiArrival(Double timeToNextTaxiArrival) {
        this.timeToNextTaxiArrival = timeToNextTaxiArrival;
    }

    public void setNextTaxiArrival(Double nextTaxiArrival) {
        this.nextTaxiArrival = nextTaxiArrival;
    }

    public void setRndBusArrival(Double rndBusArrival) {
        this.rndBusArrival = rndBusArrival;
    }

    public void setTimeToNextBusArrival(Double timeToNextBusArrival) {
        this.timeToNextBusArrival = timeToNextBusArrival;
    }

    public void setNextBusArrival(Double nextBusArrival) {
        this.nextBusArrival = nextBusArrival;
    }

    public void setNextChangeOfLight(double nextChangeOfLight) {
        this.nextChangeOfLight = nextChangeOfLight;
    }

    public void setParticularsOnY(Integer particularsOnY) {
        this.particularsOnY = particularsOnY;
    }

    public void setTaxisOnY(Integer taxisOnY) {
        this.taxisOnY = taxisOnY;
    }

    public void setBusesOnY(Integer busesOnY) {
        this.busesOnY = busesOnY;
    }

    public SimulationRow setLeftXQueue(int leftXQueue) {
        this.leftXQueue = leftXQueue;
        return this;
    }

    public void setLightStateX(LightState lightStateX) {
        this.lightStateX = lightStateX;
    }

    public SimulationRow setyQueue(int yQueue) {
        this.yQueue = yQueue;
        return this;
    }

    public void setLightStateY(LightState lightStateY) {
        this.lightStateY = lightStateY;
    }

    public void setLightChangesCounter(int lightChangesCounter) {
        this.lightChangesCounter = lightChangesCounter;
    }

    public void setBlockedYCounter(int blockedYCounter) {
        this.blockedYCounter = blockedYCounter;
    }

    public SimulationRow setTime(double time) {
        this.time = time;
        return this;
    }

    public SimulationRow setCurrentEvent(EventType currentEvent) {
        this.currentEvent = currentEvent;
        return this;
    }

    public void setNextParticularArrival(double nextParticularArrival) {
        this.nextParticularArrival = nextParticularArrival;
    }

    public SimulationRow setRightXQueue(int rightXQueue) {
        this.rightXQueue = rightXQueue;
        return this;
    }

    public SimulationRow setVehicles(List<VehicleRow> vehicles) {
        this.vehicles = vehicles.stream().map(VehicleRow::new).collect(Collectors.toList());
        return this;
    }
}
