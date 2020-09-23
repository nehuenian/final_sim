package service;

import model.*;
import model.events.Event;
import model.events.EventType;
import model.events.IEventGenerator;
import model.events.UniformRandomGenerator;
import view.IQueueController;
import view.SimulationResult;
import view.SimulationRow;
import view.VehicleRow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Service implements IService {

    private static final int LIGHT_Y_MAX_CARS = 20;
    private IQueueController _controller;

    private List<Event> _theFuture;
    private Event _currentEvent;

    private LightState _lightXState = LightState.RED;
    private LightState _lightYState = LightState.GREEN;
    private double _lightChangeTime;
    private int _lightChangesCounter;
    private int _blockedCounter;

    private double _crossingTime;

    private List<Vehicle> _lightXLeftQueue;
    private List<Vehicle> _lightXRightQueue;
    private List<Vehicle> _lightYQueue;

    private SimulationRow _simulationRow;
    private List<VehicleRow> _vehiclesToDraw;

    private final Random _randomGenerator = new Random();
    private AvailableLane _nextTaxiLane;

    // variables necesarias para almacenar las filas a mostrar
    List<SimulationRow> _rowsToDraw = new ArrayList<>();
    private int _countOfRowViewGenerated;

    @Override
    public void simulate(
            IEventGenerator particularArrivalGenerator,
            IEventGenerator taxiArrivalGenerator,
            IEventGenerator busArrivalGenerator,
            double lightChangeTime,
            double crossingTime,
            double minutesToSimulate,
            IQueueController controller
    ) {
        _controller = controller;
        _crossingTime = crossingTime;
        _lightChangeTime = lightChangeTime;

        initSimulator();

        UniformRandomGenerator particularYRandomizer = new UniformRandomGenerator(2, 6);
        UniformRandomGenerator taxiYRandomizer = new UniformRandomGenerator(2, 4);
        UniformRandomGenerator busYRandomizer = new UniformRandomGenerator(1, 2);

        addEvent(new Event(minutesToSimulate, EventType.END_OF_SIMULATION));
        addEvent(new Event(_lightChangeTime, EventType.LIGHT_CHANGE));
        addNextArrival(particularArrivalGenerator.generateEvent());
        addNextArrival(taxiArrivalGenerator.generateEvent());
        addNextArrival(busArrivalGenerator.generateEvent());

        _currentEvent = _theFuture.get(0);

        while (_currentEvent.getEvent() != EventType.END_OF_SIMULATION) {
            initializeRowValues();
            if (_theFuture.isEmpty()) {
                _currentEvent = new Event(Clock.getInstance().getCurrentMinute(), EventType.END_OF_SIMULATION);
            } else {
                _currentEvent = _theFuture.remove(0);
            }

            Clock.getInstance().setCurrentMinute(_currentEvent.getMinute());

            switch (_currentEvent.getEvent()) {
                case PARTICULAR_ARRIVAL:
                    Vehicle particularVehicle = new Vehicle(AvailableLane.LEFT_LANE, VehicleType.PARTICULAR);
                    addNextArrival(particularArrivalGenerator.generateEvent());
                    _lightXLeftQueue.add(particularVehicle);

                    if (_lightXState == LightState.GREEN && thereIsSpaceToCross(_lightXLeftQueue, particularVehicle.willTurnToLeft())) {
                        addEvent(particularVehicle.crossStreet(_crossingTime));
                    }

                    if (shouldDrawIteration()) {
                        _vehiclesToDraw.add(new VehicleRow(particularVehicle));
                    }
                    break;

                case TAXI_ARRIVAL:
                    Vehicle taxi = new Vehicle(_nextTaxiLane, VehicleType.TAXI);
                    addNextArrival(taxiArrivalGenerator.generateEvent());
                    List<Vehicle> laneQueue;
                    if (taxi.getLane() == AvailableLane.LEFT_LANE) {
                        _lightXLeftQueue.add(taxi);
                        laneQueue = _lightXLeftQueue;
                    } else {
                        _lightXRightQueue.add(taxi);
                        laneQueue = _lightXRightQueue;
                    }

                    if (_lightXState == LightState.GREEN && thereIsSpaceToCross(laneQueue, taxi.willTurnToLeft())) {
                        addEvent(taxi.crossStreet(_crossingTime));
                    }

                    if (shouldDrawIteration()) {
                        _vehiclesToDraw.add(new VehicleRow(taxi));
                    }
                    break;

                case BUS_ARRIVAL:
                    Vehicle bus = new Vehicle(AvailableLane.RIGHT_LANE, VehicleType.BUS);
                    addNextArrival(busArrivalGenerator.generateEvent());
                    _lightXRightQueue.add(bus);

                    if (_lightXState == LightState.GREEN && thereIsSpaceToCross(_lightXRightQueue, bus.willTurnToLeft())) {
                        addEvent(bus.crossStreet(_crossingTime));
                    }

                    if (shouldDrawIteration()) {
                        _vehiclesToDraw.add(new VehicleRow(bus));
                    }
                    break;

                case LIGHT_CHANGE:
                    changeLight();

                    if (_lightXState == LightState.GREEN) {
                        initializeLightYQueue(
                                particularYRandomizer.getRandom(),
                                taxiYRandomizer.getRandom(),
                                busYRandomizer.getRandom()
                        );

                        sendVehiclesToCross(_lightXLeftQueue);
                        sendVehiclesToCross(_lightXRightQueue);
                    } else {
                        updateVehiclesStoppedOnY();
                        _lightYQueue.clear();
                    }

                    _simulationRow.setLightChangesCounter(_lightChangesCounter);
                    _simulationRow.setNextChangeOfLight(Clock.getInstance().getCurrentMinute() + _lightChangeTime);
                    _simulationRow.setLightStateX(_lightXState);
                    _simulationRow.setLightStateY(_lightYState);
                    break;

                case END_OF_CROSSING:
                    Vehicle vehicleCrossing = findVehicleCrossing();
                    if (vehicleCrossing == null) break;

                    removeFromQueue(vehicleCrossing);
                    if (vehicleCrossing.getLane() == AvailableLane.RIGHT_LANE) {
                        sendVehiclesToCross(_lightXRightQueue);
                    } else {
                        sendVehiclesToCross(_lightXLeftQueue);
                    }
                    vehicleCrossing.setCrossingEnd(null);
                    vehicleCrossing.setLane(null);

                    if (vehicleCrossing.willTurnToLeft() && _lightYState == LightState.RED) {
                        vehicleCrossing.setState(VehicleState.STOPPED_IN_Y);
                        _lightYQueue.add(vehicleCrossing);
                    } else {
                        vehicleCrossing.setState(null);
                    }

                    updateVehicleCrossed(vehicleCrossing);
                    break;

                case END_OF_SIMULATION:
                    addCarsInFinalRow();
                    _rowsToDraw.add(generateSimulationRow());
                    _theFuture.clear();
                    _controller.drawSimulationRows(_rowsToDraw, _vehiclesToDraw);
                    _controller.drawSimulationResult(generateSimulationResult());
                    break;
            }

            if (shouldDrawIteration()) {
                _countOfRowViewGenerated++;
                _rowsToDraw.add(generateSimulationRow());
            }
        }
    }

    private void addCarsInFinalRow() {
        _lightYQueue.stream()
                .filter(vehicle -> vehicle.getType() != VehicleType.NON_IMPORTANT)
                .filter(vehicle -> _vehiclesToDraw.stream().noneMatch(vehicleToDraw -> vehicleToDraw.getId() == vehicle.getId()))
                .forEach(vehicle -> _vehiclesToDraw.add(new VehicleRow(vehicle)));
    }

    private void initializeRowValues() {
        _simulationRow.setRndDidVehicleTurned(null);
        _simulationRow.setDidVehicleTurned(null);
        _simulationRow.setRndParticularArrival(null);
        _simulationRow.setTimeToNextParticularArrival(null);
        _simulationRow.setRndTaxiArrival(null);
        _simulationRow.setTimeToNextTaxiArrival(null);
        _simulationRow.setRndTaxiLaneArrival(null);
        _simulationRow.setRndBusArrival(null);
        _simulationRow.setTimeToNextBusArrival(null);
    }

    private SimulationRow generateSimulationRow() {
        _simulationRow
                .setTime(Clock.getInstance().getCurrentMinute())
                .setCurrentEvent(_currentEvent.getEvent())
                .setRightXQueue(_lightXRightQueue.size())
                .setLeftXQueue(_lightXLeftQueue.size())
                .setyQueue(_lightYQueue.size())
                .setVehicles(_vehiclesToDraw)
                .setBlockedYCounter(_blockedCounter);
        return new SimulationRow(_simulationRow);
    }

    private void updateVehiclesStoppedOnY() {
        _vehiclesToDraw.stream()
                .filter(vehicleRow -> vehicleRow.getState() == VehicleState.STOPPED_IN_Y)
                .forEach(vehicleRow -> {
                    vehicleRow.setLane(null);
                    vehicleRow.setState(null);
                    vehicleRow.setEndOfCruse(null);
                });
    }

    private void removeFromQueue(Vehicle vehicleCrossing) {
        _lightXRightQueue.remove(vehicleCrossing);
        _lightXLeftQueue.remove(vehicleCrossing);
    }

    private Vehicle findVehicleCrossing() {
        return Stream.concat(_lightXRightQueue.stream(), _lightXLeftQueue.stream())
                .filter(vehicle -> vehicle.getState() == VehicleState.CROSSING_X)
                .min(Comparator.comparing(Vehicle::getCrossingEnd))
                .orElse(null);
    }

    private void initializeLightYQueue(int particularNumber, int taxiNumber, int busNumber) {
        _simulationRow.setParticularsOnY(particularNumber);
        _simulationRow.setTaxisOnY(taxiNumber);
        _simulationRow.setBusesOnY(busNumber);

        int queueSize = particularNumber + taxiNumber + 2 * busNumber;
        for (int i = 0; i < queueSize; i++) {
            _lightYQueue.add(new Vehicle(VehicleType.NON_IMPORTANT));
        }
    }

    private void changeLight() {
        _lightChangesCounter++;
        if (_lightXState == LightState.GREEN) {
            _lightXState = LightState.RED;
            _lightYState = LightState.GREEN;
        } else {
            _lightXState = LightState.GREEN;
            _lightYState = LightState.RED;
        }
        addEvent(new Event(_lightChangeTime, EventType.LIGHT_CHANGE));
    }

    private void sendVehiclesToCross(List<Vehicle> vehicles) {
        List<Vehicle> vehiclesWaitingToCross = vehicles.stream()
                .filter(vehicle -> vehicle.getState() == VehicleState.STOPPED_IN_X).collect(Collectors.toList());
        for (Vehicle nextVehicleToCross : vehiclesWaitingToCross) {
            if (thereIsSpaceToCross(vehicles, nextVehicleToCross.willTurnToLeft())) {
                addEvent(nextVehicleToCross.crossStreet(_crossingTime));
                updateVehicleToDraw(nextVehicleToCross);
            } else {
                break;
            }
        }
    }

    private boolean thereIsSpaceToCross(List<Vehicle> queue, boolean willTurn) {
        if (queue.stream().filter(vehicle -> vehicle.getState() == VehicleState.CROSSING_X).count() < 2) {
            if (willTurn && _lightYQueue.size() >= LIGHT_Y_MAX_CARS) {
                _blockedCounter++;
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void updateVehicleCrossed(Vehicle vehicleCrossed) {
        _simulationRow.setRndDidVehicleTurned(vehicleCrossed.getRndVehicleWillTurnToLeft());
        _simulationRow.setDidVehicleTurned(vehicleCrossed.willTurnToLeft());
        updateVehicleToDraw(vehicleCrossed);
    }

    private void updateVehicleToDraw(Vehicle vehicleCrossed) {
        _vehiclesToDraw.stream()
                .filter(vehicleRow -> vehicleRow.getId() == vehicleCrossed.getId())
                .findFirst()
                .ifPresent(vehicleRow -> {
                    vehicleRow.setState(vehicleCrossed.getState());
                    vehicleRow.setLane(vehicleCrossed.getLane());
                    vehicleRow.setEndOfCruse(vehicleCrossed.getCrossingEnd());
                });
    }

    private void addNextArrival(Event nextArrival) {
        if (nextArrival.getEvent() == EventType.PARTICULAR_ARRIVAL) {
            _simulationRow.setNextParticularArrival(nextArrival.getMinute());
            _simulationRow.setRndParticularArrival(nextArrival.getRandom());
            _simulationRow.setTimeToNextParticularArrival(nextArrival.getMinutesToEvent());
        } else if (nextArrival.getEvent() == EventType.TAXI_ARRIVAL) {
            double rndNextTaxiLane = _randomGenerator.nextDouble();
            if (rndNextTaxiLane < 0.5) {
                _nextTaxiLane = AvailableLane.LEFT_LANE;
            } else {
                _nextTaxiLane = AvailableLane.RIGHT_LANE;
            }
            _simulationRow.setNextTaxiArrival(nextArrival.getMinute());
            _simulationRow.setRndTaxiArrival(nextArrival.getRandom());
            _simulationRow.setTimeToNextTaxiArrival(nextArrival.getMinutesToEvent());
            _simulationRow.setNextTaxiLane(_nextTaxiLane);
            _simulationRow.setRndTaxiLaneArrival(rndNextTaxiLane);
        } else if (nextArrival.getEvent() == EventType.BUS_ARRIVAL) {
            _simulationRow.setNextBusArrival(nextArrival.getMinute());
            _simulationRow.setRndBusArrival(nextArrival.getRandom());
            _simulationRow.setTimeToNextBusArrival(nextArrival.getMinutesToEvent());
        }

        addEvent(nextArrival);
    }

    private boolean shouldDrawIteration() {
        return Clock.getInstance().getCurrentMinute() >= _controller.getMinutesFrom()
                && _currentEvent.getEvent() != EventType.END_OF_SIMULATION
                && _countOfRowViewGenerated < _controller.getIterationsToDraw();
    }

    private void addEvent(Event event) {
        _theFuture.add(event);
        _theFuture.sort(Comparator.comparing(Event::getMinute));
    }

    private SimulationResult generateSimulationResult() {
        return new SimulationResult(_lightChangesCounter, _blockedCounter);
    }

    private void initSimulator() {
        _lightXState = LightState.RED;
        _lightYState = LightState.GREEN;
        _lightXLeftQueue = new ArrayList<>();
        _lightXRightQueue = new ArrayList<>();
        _lightYQueue = new ArrayList<>();
        _vehiclesToDraw = new ArrayList<>();
        _theFuture = new ArrayList<>();
        Vehicle.restartIds();
        Clock.getInstance().setCurrentMinute(0.0);
        _countOfRowViewGenerated = 0;
        _lightChangesCounter = 0;
        _blockedCounter = 0;
        _rowsToDraw.clear();
        _simulationRow = new SimulationRow();
        _simulationRow.setLightStateY(_lightYState);
        _simulationRow.setLightStateX(_lightXState);
    }
}
