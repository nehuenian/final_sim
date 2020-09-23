package model;

import model.events.Event;
import model.events.EventType;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Vehicle {
    private static AtomicInteger idSingleton = new AtomicInteger(0);
    private static final Random randomGenerator = new Random();
    private final Integer id;
    private double rndVehicleWillTurnToLeft;
    private boolean willTurnToLeft;
    private Double crossingEnd;
    private AvailableLane lane;
    private VehicleState state;
    private VehicleType type;

    public Vehicle() {
        this.id = idSingleton.incrementAndGet();
    }

    public Vehicle(VehicleType type) {
        this.id = idSingleton.incrementAndGet();
        this.type = type;
    }

    public Vehicle(AvailableLane lane, VehicleType type) {
        this.id = idSingleton.incrementAndGet();
        this.lane = lane;
        this.type = type;
        this.state = VehicleState.STOPPED_IN_X;
        this.rndVehicleWillTurnToLeft = randomGenerator.nextDouble();
        this.willTurnToLeft = (lane == AvailableLane.LEFT_LANE && rndVehicleWillTurnToLeft < 0.3);
    }

    public VehicleState getState() {
        return state;
    }

    public void setState(VehicleState state) {
        this.state = state;
    }

    public Event crossStreet(double crossingTime) {
        this.state = VehicleState.CROSSING_X;
        this.crossingEnd = Clock.getInstance().getCurrentMinute() + crossingTime;
        return new Event(crossingTime, EventType.END_OF_CROSSING);
    }

    public static void restartIds(){
        Vehicle.idSingleton = new AtomicInteger(0);
    }

    public int getId() {
        return id;
    }

    public VehicleType getType() { return type; }

    public double getRndVehicleWillTurnToLeft() {
        return rndVehicleWillTurnToLeft;
    }

    public boolean willTurnToLeft() {
        return willTurnToLeft;
    }

    public Double getCrossingEnd() {
        return crossingEnd;
    }

    public AvailableLane getLane() {
        return lane;
    }

    public void setType(VehicleType type) { this.type = type; }

    public void setCrossingEnd(Double crossingEnd) {
        this.crossingEnd = crossingEnd;
    }

    public void setLane(AvailableLane lane) {
        this.lane = lane;
    }
}
