package model.events;

import model.Clock;

import java.time.LocalDateTime;
import java.util.Random;

public class NormalEventGenerator implements IEventGenerator {
    private final Random randomGenerator = new Random();
    private final double media;
    private final double standardDeviation;
    private final EventType eventToGenerate;

    public NormalEventGenerator(double media, double standardDeviation, EventType eventName) {
        this.media = media;
        this.standardDeviation = standardDeviation;
        this.eventToGenerate = eventName;
    }

    @Override
    public Event generateEvent() {
        double timeForEvent = (randomGenerator.nextGaussian() * standardDeviation) + media;

        return new Event(timeForEvent, eventToGenerate);
    }
}
