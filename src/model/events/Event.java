package model.events;

import model.Clock;

public class Event {
    private final double random;
    private final double minutesToEvent;
    private final double minute;
    private final EventType event;
    private int clientId;

    public Event(double minutesToEvent, EventType event) {
        this.random = 0;
        this.event = event;
        this.minutesToEvent = minutesToEvent;
        this.minute = Clock.getInstance().getCurrentMinute() + minutesToEvent;
        this.clientId = 0;
    }

    public Event(double random, double minutesToEvent, EventType event) {
        this.random = random;
        this.event = event;
        this.minutesToEvent = minutesToEvent;
        this.minute = Clock.getInstance().getCurrentMinute() + minutesToEvent;
        this.clientId = 0;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public double getRandom() {
        return random;
    }

    public double getMinutesToEvent() {
        return minutesToEvent;
    }

    public double getMinute() {
        return minute;
    }

    public EventType getEvent() {
        return event;
    }

    public int getClientId() {
        return clientId;
    }
}

